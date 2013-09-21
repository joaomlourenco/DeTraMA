package polyglot.ext.ml5.bct.tree;

public class VisitorPrettyPrinter extends Visitor {

	private int n;
	
	public VisitorPrettyPrinter () {
		this.n = 0;
	}
	
	private void makeTabs () {
		for (int i = 0 ; i < n ; i++)
			System.out.print("\t");
	}
	
	public void print (Object msg) {
		makeTabs();
		System.out.println(msg);
	}
	
	public void inc() {
		n++;
	}
	
	public void dec() {
		n--;
	}
	
	@Override
	public MyNode visit(NodeAccessSingleRead access) {
		print(access);
		return access;
	}
	
	@Override
	public MyNode visit(NodeAccessSingleWrite access) {
		print(access);
		return access;
	}

	@Override
	public MyNode visit(NodeAtom atom) {
		print("[ATOM]");
		inc();
		atom.body.accept(this);
		dec();
		return atom;
	}

	@Override
	public MyNode visit(NodeCall call) {
		print (call);
		return call;
	}

	@Override
	public MyNode visit(NodeIf nif) {
		print ("[IF]");
		inc();
		nif.branchTrue.accept(this);
		dec();
		if (nif.branchFalse != null) {
			print ("ELSE");
			inc();
			nif.branchFalse.accept(this);
			dec();
		}
		return nif;
	}

	@Override
	public MyNode visit(NodeLoop loop) {
		print ("[LOOP]");
		inc();
		loop.body.accept(this);
		dec();
		return loop;
	}

	@Override
	public MyNode visit(NodeSequence seq) {
		for (MyNode node : seq) {
			node.accept(this);
		}
		return seq;
	}

	@Override
	public MyNode visit(NodeAccessMultiple access) {
		print(access);
		return access;
	}
}
