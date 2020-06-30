package giphyproxy;

// Essentially an immutable (host, port) tuple
public class ProxyTarget {
	private String hostname;
	private int port;

	ProxyTarget(String host, int port) throws IllegalArgumentException {
		this.hostname = host;
		if (port > 0 && port < 65536) {
			this.port = port;
		}
		else {
			throw new IllegalArgumentException("port for ProxyTarget is invalid");
		}
	}

	public String getHostname() {
		return this.hostname;
	}

	public int getPort() {
		return this.port;
	}
}
