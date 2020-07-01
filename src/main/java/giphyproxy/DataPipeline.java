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
		DataUniPipe clientToTarget = new DataUniPipe(clientIn, targetOut);
		DataUniPipe targetToClient = new DataUniPipe(targetIn, clientOut);

		System.out.println("starting client -> target");
		clientToTarget.start();
		System.out.println("starting target -> client");
		targetToClient.start();

		while (true) {
			if (!clientToTarget.isAlive() || !targetToClient.isAlive()) {
				System.out.println("one of the pipe threads has died");
				// if either half has die, close the sockets
				this.clientSock.close();
				this.targetSock.close();

				return;
			}
		}
	}
}
