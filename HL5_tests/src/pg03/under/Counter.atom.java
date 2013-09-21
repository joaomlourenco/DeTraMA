package pg03.under;

public class Counter {

	int i;
	
	int inc(int a) {
		int res;
		atomic {
			i += a;
			res = i;
		}
		return res;
	}
}
