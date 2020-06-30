package giphyproxy;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.net.Socket;

import java.io.IOException;
import java.net.UnknownHostException;

// just pump data between giphy & client
public class DataPipeline {
	private Socket clientSock; // TODO: make SSL
	private Socket targetSock;

	DataPipeline(Socket client, ProxyTarget target) throws IOException, UnknownHostException {
		this.clientSock = client;

		this.targetSock = new Socket(target.getHostname(), target.getPort());
	}

	public void runForever() throws IOException {
		// get appropriate streams for bytes, to be agnostic
		BufferedOutputStream clientOut = new BufferedOutputStream(this.clientSock.getOutputStream());
		BufferedInputStream clientIn = new BufferedInputStream(this.clientSock.getInputStream());
		BufferedOutputStream targetOut = new BufferedOutputStream(this.targetSock.getOutputStream());
		BufferedInputStream targetIn = new BufferedInputStream(this.targetSock.getInputStream());


		// pump data
		int numRead = 0;
		byte[] client2target = new byte[4096];
		byte[] target2client = new byte[4096];
		while (true) {
			numRead = 0;
			if (clientIn.available() > 0) {
				System.out.println("checking client");
				numRead = clientIn.read(client2target, 0, client2target.length);
				if (numRead > 0) {
					System.out.println("client -> target: " + numRead);
					targetOut.write(client2target, 0, numRead);
					targetOut.flush();
				}
			}

			numRead = 0;
			if (targetIn.available() > 0) {
				System.out.println("checking target");
				numRead = targetIn.read(target2client, 0, target2client.length);
				if (numRead > 0) {
					System.out.println("target -> client: " + numRead);
					clientOut.write(target2client, 0, numRead);
					clientOut.flush();
				}
			}

			// TODO: detect either socket close, and tear down if so
		}
	}
}
