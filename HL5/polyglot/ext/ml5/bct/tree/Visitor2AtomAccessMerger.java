package polyglot.ext.ml5.bct.tree;

import java.util.LinkedList;
import java.util.List;

public class Visitor2AtomAccessMerger extends Visitor {

	protected boolean inside = false;
	protected NodeAccessMultiple current = null;
	
	@Override
	public MyNode visit(NodeAccessSingleRead access) {
		if (current != null)
			current.add((NodeAccess)access.copy());
		return null;
	}

	@Override
	public MyNode visit(NodeAccessSingleWrite access) {
		if (current != null)
			current.add((NodeAccess)access.copy());
		return null;
	}

	@Override
	public MyNode visit(NodeAtom atom) {
		if (inside) {
			return atom.body.accept(this);
		}
		inside = true;
		current = new NodeAccessMultiple();
		atom = (NodeAtom) atom.copy();
		atom.body.accept(this);
		atom.body = current;
		current = null;
		inside = false;
		return atom;
	}

	@Override
	public MyNode visit(NodeCall call) {
		return call.copy();
	}

	@Override
	public MyNode visit(NodeIf nif) {
		nif = (NodeIf) nif.copy();
		nif.branchTrue = nif.branchTrue.accept(this);
		nif.branchFalse = nif.branchFalse.accept(this);
		return nif;
	}

	@Override
	public MyNode visit(NodeLoop loop) {
		loop = (NodeLoop) loop.copy();
		loop.body = loop.body.accept(this);
		return loop;
	}

	@Override
	public MyNode visit(NodeSequence seq) {
		List<MyNode> list = new LinkedList<MyNode>();
		for (MyNode node : seq) {
			list.add(node.accept(this));
		}
		if (list.isEmpty())
			return null;
		if (list.size() == 1)
			return list.get(0);
		return new NodeSequence(seq.getNode(), list);
	}

	@Override
	public MyNode visit(NodeAccessMultiple access) {
		if (current != null)
			for (NodeAccess node : access.accesses) {
				current.add(node);
			}
		return null;
	}

}
