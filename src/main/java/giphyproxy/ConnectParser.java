package giphyproxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;
import java.text.ParseException;

// very rudimentary HTTP CONNECT parsing
public class ConnectParser {

	ConnectParser() {}

	private static ProxyTarget parseConnectLine(String line) throws ParseException, NumberFormatException, IllegalArgumentException {

		String[] parts = line.split(" ");

		// verify the connect line is OK
		// exact syntax: "CONNECT [host]:[post] HTTP/1.1"
		if (parts.length == 3 &&
			parts[0].equals("CONNECT") &&
			parts[2].equals("HTTP/1.1") )
		{
			String[] hostParts = parts[1].split(":");

			if (hostParts.length != 2) {
				throw new ParseException("malformed host", 0);
			}

			// bubble up number format exceptions for invalid port
			int port = Integer.parseInt(hostParts[1]);
			ProxyTarget target = new ProxyTarget(hostParts[0], port);

			return target;
		}
		else {
			throw new ParseException("malformed CONNECT line", 0);
		}
	}

	// parse an HTTP CONNECT command, throw away any headers
	public static ProxyTarget parse(BufferedReader sockReader) throws ParseException, IOException, NumberFormatException, IllegalArgumentException {
		// TODO: handle malformed connections without newlines
		String commandLine = sockReader.readLine();

		// parse HTTP CONNECT out of line
		ProxyTarget target = parseConnectLine(commandLine);

		// throw away headers to clear out the buffer
		while(sockReader.ready()) {
			//System.out.println("reading extra input...");
			sockReader.readLine();
		}

		// return requested host:port (ProxyTarget)
		return target;
	}

	public static void sendHttpSuccess(BufferedWriter sockWriter) throws IOException {
		String httpSuccess = "HTTP/1.1 200 OK\r\n\r\n";
		sockWriter.write(httpSuccess, 0, httpSuccess.length());
		sockWriter.flush();
	}
}
