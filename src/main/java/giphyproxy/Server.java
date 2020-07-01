// main listening socket for proxy server
package giphyproxy;

import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

public class Server {

	// declare the IOExceptions thrown because we can't recover from
	// them anyways, and this keeps our code cleaner
	public static void main(String args[]) throws java.io.IOException {
		ServerSocket sock;
		Socket clientSock;

		// use the default keystore (easiest)
		ServerSocketFactory sslSF = SSLServerSocketFactory.getDefault();
		sock = sslSF.createServerSocket(1080);

		while (true) {
			clientSock = sock.accept();

			ClientHandler handler = new ClientHandler(clientSock);
			handler.run();
		}
	}
}
