package ff04.stringBuffer;

/*
 * Simulate java.lang.StringBuffer
 */
public final class MyStringBuffer {

	private java.lang.StringBuffer buffer;
	
	public MyStringBuffer(String string) {
		this.buffer = new StringBuffer(string);
	}

	public static void main(String args[]) {
		MyStringBuffer ham = new MyStringBuffer("ham");
		MyStringBuffer burger = new MyStringBuffer("burger");
		ham.append(burger);
		System.out.println(ham);
	}

	public MyStringBuffer append(MyStringBuffer other) {

		int len = other.length();
		
		// ...other threads may change sb.length(),
		// ...so len does not reflect the length of 'other'
		
		char[] value = new char[len];
		other.getChars(0, len, value, 0);
		
		// ...
		
		return this;
	}
	
	public int length() {
		int len;
		atomic {
			len = this.buffer.length();
		}
		return len;
	}

	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		atomic {
			this.buffer.getChars(srcBegin, srcEnd, dst, dstBegin);
		}
	}
	

}
