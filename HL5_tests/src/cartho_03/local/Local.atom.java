package cartho_03.local;

public class Local extends Thread {

	static Cell x = new Cell();

	public static void main(String[] args) {
		new Local().start();
		new Local().start();
	}

	public void run() {
		int tmp;
		atomic {
			tmp = x.getValue();
		}
		tmp++;
		atomic {
			x.setValue(tmp);
		}
	}

	static class Cell {
		int n = 0;

		int getValue() {
			return n;
		}

		void setValue(int x) {
			n = x;
		}
	}

}
