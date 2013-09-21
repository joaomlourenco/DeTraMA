package polyglot.ext.ml5.bct.tree;

public class VisitorSimplePrinter extends Visitor {

	private int n = 0;

	private void makeTabs() {
		for (int i = 0; i < n; i++)
			System.out.print("\t");
	}

	private void print(Object msg) {
		makeTabs();
		System.out.println(msg);
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
		print(atom);
		n++;
		atom.body.accept(this);
		n--;
		return atom;
	}

	@Override
	public MyNode visit(NodeCall call) {
		print(call);
		return call;
	}

	@Override
	public MyNode visit(NodeIf nif) {
		print(nif);
		n++;
		nif.branchTrue.accept(this);
		n--;
		if (nif.branchFalse != null) {
			print("ELSE");
			n++;
			nif.branchFalse.accept(this);
			n--;
		}
		return nif;
	}

	@Override
	public MyNode visit(NodeLoop loop) {
		print(loop);
		n++;
		loop.body.accept(this);
		n--;
		return loop;
	}

	@Override
	public MyNode visit(NodeSequence seq) {
		print(seq);
		n++;
		for (MyNode node : seq) {
			node.accept(this);
		}
		n--;
		return seq;
	}

	@Override
	public MyNode visit(NodeAccessMultiple access) {
		print(access);
		return access;
	}

}
