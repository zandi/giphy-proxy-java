// handle individual proxy clients
package giphyproxy;

import java.net.Socket;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.IOException;
import java.text.ParseException;

public class ClientHandler extends Thread {
	private Socket clientSock;
	private Socket giphySock;

	// currently only a single hostname for giphy api, convenient!
	private final String giphyApiHostname = "api.giphy.com";
	private ConnectionApprover tunnelTargetApprover = new ConnectionApprover();

	ClientHandler(Socket clientSock) {
		this.clientSock = clientSock;

		ProxyTarget giphySsl = new ProxyTarget("api.giphy.com", 443);
		this.tunnelTargetApprover.addApprovedTarget(giphySsl);
	}

	// business logic of the proxy
	public void run() {
		System.out.println("Handling!");

		try {
			BufferedReader clientReader = new BufferedReader(new InputStreamReader(this.clientSock.getInputStream()));
			BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(this.clientSock.getOutputStream()));

			// parse CONNECT line
			ProxyTarget tunnelTarget = ConnectParser.parse(clientReader);

			// verify the host is allowed
			if (this.tunnelTargetApprover.isApproved(tunnelTarget)) {
				// send success
				ConnectParser.sendHttpSuccess(clientWriter);

				// serve forever
				DataPipeline dataPipeline = new DataPipeline(this.clientSock, tunnelTarget);
				dataPipeline.runForever();
			}
			else {
				// tear down connection
				this.clientSock.close();
			}
		}
		catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			return;
		}
		catch (ParseException e) {
			System.out.println("Error parsing: " + e.getMessage());
			try {
				this.clientSock.close();
			}
			catch (IOException e2) { // already gone }

			return;
		}

		return;
	}
}
