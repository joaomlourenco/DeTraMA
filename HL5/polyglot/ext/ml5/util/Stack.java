package polyglot.ext.ml5.util;

public class Stack<T> {
	private T value;
	private Stack<T> prev;
	
	public Stack(T value, Stack<T> prev) {
		this.value = value;
		this.prev = prev;
	}
	
	public Stack(T value) {
		this(value, null);
	}
	
	public Stack() {
		this(null);
	}
	
	public Stack<T> push() {
		return new Stack<T>(null, this);
	}
	
	public Stack<T> pop() {
		return this.prev;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return this.value;
	}
	
}
