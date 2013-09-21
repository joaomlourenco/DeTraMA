package pg03.over;

import java.util.Random;

public class Map {

	Object[] keys, values;
	volatile boolean init_done = false;

	void init() {
		if (!init_done)
			atomic {
				init_done = true;
				// update keys and values
				Random rand = new Random();
				keys = new Object[rand.nextInt(10) + 1];
				values = new Object[keys.length];
			}
	}

	Object get(Object key) {
		Object res = null;
		atomic {
			// read keys and values
			for (int i = 0 ; i < keys.length ; i++) {
				if (key.equals(keys[i])) {
					res = values[i];
					break;
				}
			}
		}
		return res;
	}
}
