package cartho_04.fig5;

public class Fig5 extends Thread {
	Buffer buffer;

	public Fig5(Buffer buffer) {
		super();
		this.buffer = buffer;
	}

	public static void main(String[] args) {
		Buffer buffer = new Buffer();
		new Fig5(buffer).start();
		new Fig5(buffer).start();
		new Fig5(buffer).start();
	}

	public void run() {
		work();
	}

	public void work() {
		int value, fdata;
		while (true) {
			atomic {
				value = buffer.next();
			}
			fdata = f(value); // long computation
			atomic { // Data flow from previous block!
				buffer.add(fdata); // However, the program is correct because
			} // the buffer protocol ensures that the
		} // returned data remains thread-local.
	}

	int f(int x) {
		return x * x;
	}

	static class Buffer {
		// I know it doesn't make sense, we're just trying to simulate reads and writes
		int cell = 0;
		int head = 0;
		int tail = 0;

		public int next() {
			int res;
			atomic {
				head = head + 1;
				res = cell;
			}
			return res;
		}

		public void add(int x) {
			atomic {
				tail = tail + 1;
				cell = x;
			}
		}

	}
}
