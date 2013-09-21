package bba08;

import java.io.IOException;
import java.net.Socket;

class Connection {

	private final Counter counter;
	private Socket socket;

	public Connection() {
		this.socket = null;
		this.counter = new Counter();
	}
	
	// BCT
	public void connect() {
		this.socket = new Socket();
	}

	public void disconnect() throws IOException {
		atomic {
			this.socket.close();
			this.socket = null;
		}
		this.counter.reset();
	}

	public boolean isConnected() {
		atomic {
			return (this.socket != null);
		}
	}

	public void send(String msg) throws IOException {
		atomic {
			this.socket.getOutputStream().write(msg.getBytes());
			this.counter.increment();
		}
	}

}
