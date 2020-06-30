package giphyproxy;

// Essentially an immutable (host, port) tuple
public class ProxyTarget {
	private String hostname;
	private int port;

	// TODO: move target verification here, and only allow this object type
	// for our data handling class, so that only valid ProxyTarget objects
	// are usable, and they will always only describe allowed targets
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

	public String toString() {
		return this.hostname + ":" + Integer.toString(this.port);
	}

	public boolean equals(ProxyTarget B) {
		boolean hostMatches = this.hostname.equals(B.getHostname());
		boolean portMatches = this.port == B.getPort();

		return hostMatches && portMatches;
	}
}
