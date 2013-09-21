package pg03.jigsaw;

import java.util.HashMap;
import java.util.Map;

public class ResourceStoreManager {

	boolean closed = false;
	Map entries = new HashMap();

	void checkClosed() {
		atomic {
			if (closed)
				throw new RuntimeException();
		}
	}
	
	public static void main(String args[]) {
		ResourceStoreManager rsm = new ResourceStoreManager();
		new Runner(rsm).start();
		new Runner(rsm).start();
		new Runner(rsm).start();
	}

	// not synched!!! HLDR, but we get a false negative!
	ResourceStore loadResourceStore() {
		checkClosed(); // R(closed)
		Entry e = lookupEntry(new Object()); // R(entries), W(entries)
		return e.getStore();
	}

	synchronized Entry lookupEntry(Object key) {
		Entry e;
		atomic {
			e = (Entry) entries.get(key);
			if (e == null) {
				e = new Entry();
				entries.put(key, e);
				entries=null;
			}
		}
		return e;
	}

	void shutdown() {
		atomic {
			entries.clear();
			closed = true;
		}
	}

	public class ResourceStore {

	}

	public class Entry {
		public ResourceStore getStore() {
			return null;
		}
	}
	
	static public class Runner extends Thread {
		private ResourceStoreManager rsm;
		public Runner(ResourceStoreManager rsm) {
			this.rsm = rsm;
		}
		public void run() {
			System.out.println(rsm.loadResourceStore());
			rsm.shutdown();
		}
	}
}
