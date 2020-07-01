package giphyproxy;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;

import java.io.IOException;

// thread a single in -> out pump for network communication
// without polling and blocking
public class DataUniPipe extends Thread {
	private BufferedInputStream inPipe;
	private BufferedOutputStream outPipe;

	DataUniPipe(BufferedInputStream in, BufferedOutputStream out) {
		this.inPipe = in;
		this.outPipe = out;
	}

	public void run() {
		int numRead = 0;
		byte[] buf = new byte[4096];

		try {
			while (true) {
				numRead = this.inPipe.read(buf, 0, buf.length);

				if (numRead > 0) {
					this.outPipe.write(buf, 0, numRead);
					this.outPipe.flush();
				}
				else if (numRead == -1) {
					this.inPipe.close();
					this.outPipe.close();

					break;
				}
			}
		}
		catch (IOException e) {
			// just cleanly exit the thread, this exception likely
			// indicates broken/closed sockets, so we're done piping data
			System.out.println("IOException in DataUniPipe: " + e.getMessage());
		}
	}
}
