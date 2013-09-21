package bba08;

public class Counter {

	int n = 0;

	public void reset() {
		atomic {
			n = 0;
		}
	}

	public void increment() {
		atomic {
			n++;
		}
	}

}
