package cartho_03.nasa;

public class Task extends Thread {

	public Cell[] table;

	public Task(Cell[] table) {
		super();
		this.table = table;
	}

	public void run() {

		int N = 42;

		Object v = new Object();

		atomic {
			table[N].value = v;
		}

		/* achieve property */

		atomic {
			table[N].achieved = true;
		}
	}
}
