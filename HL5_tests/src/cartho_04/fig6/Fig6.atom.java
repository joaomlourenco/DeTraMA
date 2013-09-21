package cartho_04.fig6;

public class Fig6 extends Thread {

	static Cell shared;

	public static void main(String[] args) {
		new Fig6().start();
		new Fig6().start();
		new Fig6().start();
	}

	public void run() {
		do_transaction();
	}

	public void do_transaction() {
		int value, fdata;
		boolean done = false;
		while (!done) {
			atomic {
				value = shared.field;
			}
			fdata = f(value); // long computation
			atomic {
				if (value == shared.field) {
					shared.field = fdata;
					// The usage of the locally computed fdata is safe because
					// the shared value is the same as during the computation.
					// Our algorithm and previous atomicity-based approaches
					// report an error (false positive).
					done = true;
				}
			}
		}
	}

	int f(int x) {
		return x * x;
	}

	static class Cell {
		public int field;
	}

}
