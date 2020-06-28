// handle individual proxy clients
package giphyproxy;

import java.net.Socket;

public class ClientHandler {
	private Socket clientSock;

	ClientHandler(Socket clientSock) {
		this.clientSock = clientSock;
	}

	// business logic of the proxy
	public void handle() {
		// TODO: read from client, try to parse HTTP CONNECT.
		System.out.println("Handling!");
		return;
	}
}
