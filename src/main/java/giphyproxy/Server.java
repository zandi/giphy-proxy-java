// main listening socket for proxy server
package giphyproxy;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	// declare the IOExceptions thrown because we can't recover from
	// them anyways, and this keeps our code cleaner
	public static void main(String args[]) throws java.io.IOException {
		ServerSocket sock;
		Socket clientSock;

		// TODO: once we have things working, make this SSL
		sock = new ServerSocket(1080);

		while (true) {
			clientSock = sock.accept();

			// TODO: once this works for a single client, thread
			ClientHandler handler = new ClientHandler(clientSock);
			handler.handle();
		}
	}
}
