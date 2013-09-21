package polyglot.ext.ml5.visit.context;

public class AtomicContext {
	private AtomicContextBody atomicScope;
	
	public AtomicContext() {
		atomicScope = null;
	}
	
	public void enterAtomic() {
		atomicScope = new AtomicContextBody(atomicScope);
	}
	
	public void leaveAtomic() {
		atomicScope = atomicScope.next;
	}
	
	public boolean inAtomic() {
		return atomicScope != null;
	}
	
	private class AtomicContextBody {
		public AtomicContextBody next;
		
		public AtomicContextBody(AtomicContextBody next) {
			this.next = next;
		}
		public AtomicContextBody() {
			this(null);
		}
	}
	
	public void initPassRun() {
		
	}
	
}
