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

public class ClientHandler {
	private Socket clientSock;
	private Socket giphySock; // TODO: make SSL before we make listen socket SSL

	// currently only a single hostname for giphy api, convenient!
	private final String giphyApiHostname = "api.giphy.com";

	ClientHandler(Socket clientSock) {
		this.clientSock = clientSock;
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

	// if the host is allowed, and the port is 443 (https)
	private Boolean hostIsAllowed(String host, String port) {
		return host.equals(this.giphyApiHostname) && port.equals("80");
	}

	// parse an HTTP CONNECT string, returning
	// the host to connect to with hostname/port.
	// Throw a ParseException if the argument doesn't
	// seem to be in valid format
	// 
	// format: "CONNECT [hostname]:[port] HTTP/1.1
	private String[] parseConnect(String line) throws ParseException {
		String[] parts = line.split(" ");

		// verify the connect line is OK, and the host is allowed
		if (parts.length == 3 &&
			parts[0].equals("CONNECT") &&
			parts[2].equals("HTTP/1.1"))
		{
			String[] hostParts = parts[1].split(":");
			
			if (hostParts.length == 2 &&
				hostIsAllowed(hostParts[0], hostParts[1]))
			{
				return hostParts;
			}
			else {
				throw new ParseException("malformed or forbidden host", 0);
			}
		}
		else {
			throw new ParseException("malformed CONNECT line", 0);
		}

	}

	// business logic of the proxy
	public void handle() {
		// TODO: read from client, try to parse HTTP CONNECT.
		System.out.println("Handling!");

		try {
			BufferedReader clientReader = new BufferedReader(new InputStreamReader(this.clientSock.getInputStream()));
			BufferedWriter clientWriter = new BufferedWriter(new OutputStreamWriter(this.clientSock.getOutputStream()));

			String line = clientReader.readLine();
			String[] host = parseConnect(line);
			System.out.println("CONNECT request to: " + host[0] + ":" + host[1]);

			// flush any remaining lines we were sent, we're ignoring
			// headers at the moment
			// TODO: can we just flush()?
			while(clientReader.ready()) {
				System.out.println("reading extra input...");
				clientReader.readLine();
			}
			System.out.println("done flushing");

			this.giphySock = new Socket(host[0], Integer.parseInt(host[1]));
			System.out.println("Sending success to client");
			String httpSuccess = "HTTP/1.1 200 OK\r\n\r\n";
			clientWriter.write(httpSuccess, 0, httpSuccess.length());
			clientWriter.flush();

			proxyForever();

		}
		catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			return;
		}
		catch (ParseException e) {
			System.out.println("Error parsing: " + e.getMessage());
			return;
		}

		return;
	}
}
