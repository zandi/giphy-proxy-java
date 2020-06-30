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

// TODO: make this runnable to use threads
public class ClientHandler {
	private Socket clientSock;
	private Socket giphySock; // TODO: make SSL before we make listen socket SSL

	// currently only a single hostname for giphy api, convenient!
	private final String giphyApiHostname = "api.giphy.com";
	private ConnectionApprover tunnelTargetApprover = new ConnectionApprover();

	ClientHandler(Socket clientSock) {
		this.clientSock = clientSock;

		ProxyTarget giphySsl = new ProxyTarget("api.giphy.com", 443);
		this.tunnelTargetApprover.addApprovedTarget(giphySsl);
	}

	// once things are all connected, just send/receive until
	// something closes or stops
	private void proxyForever() throws IOException {
		// client and giphy sockets established, just shuffle between,
		// starting with the client

		System.out.println("proxy forever!");

		// get appropriate streams for bytes, to be agnostic
		BufferedOutputStream clientOut = new BufferedOutputStream(this.clientSock.getOutputStream());
		BufferedInputStream clientIn = new BufferedInputStream(this.clientSock.getInputStream());
		BufferedOutputStream giphyOut = new BufferedOutputStream(this.giphySock.getOutputStream());
		BufferedInputStream giphyIn = new BufferedInputStream(this.giphySock.getInputStream());

		int numRead = 0;
		byte[] client2giphy = new byte[4096];
		byte[] giphy2client = new byte[4096];
		while (true) {
			numRead = 0;
			if (clientIn.available() > 0) {
				System.out.println("checking client");
				numRead = clientIn.read(client2giphy, 0, client2giphy.length);
				if (numRead > 0) {
					System.out.println("client -> giphy: " + numRead);
					giphyOut.write(client2giphy, 0, numRead);
					giphyOut.flush();
				}
			}

			numRead = 0;
			if (giphyIn.available() > 0) {
				System.out.println("checking giphy");
				numRead = giphyIn.read(giphy2client, 0, giphy2client.length);
				if (numRead > 0) {
					System.out.println("giphy -> client: " + numRead);
					clientOut.write(giphy2client, 0, numRead);
					clientOut.flush();
				}
			}

			// TODO: detect either socket close, and tear down if so
		}
	}

	// business logic of the proxy
	public void handle() {
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
				// TODO: send HTTP error?
				this.clientSock.close();
			}
		}
		catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			return;
		}
		catch (ParseException e) {
			System.out.println("Error parsing: " + e.getMessage());
			return;
		}
		// TODO: use finally to close sockets? Is that necessary?

		return;
	}
}
