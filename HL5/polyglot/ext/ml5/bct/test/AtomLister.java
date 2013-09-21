package polyglot.ext.ml5.bct.test;

import polyglot.ast.ArrayAccess;
import polyglot.ast.ArrayAccessAssign;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Node;
import polyglot.ext.ml5.ast.AtomicBlock_c;
import polyglot.visit.NodeVisitor;

public class AtomLister extends NodeVisitor {

	private Node current;
	
	@Override
	public NodeVisitor begin() {
		current = null;
		return super.begin();
	}

	@Override
	public NodeVisitor enter(Node n) {
		if (n instanceof AtomicBlock_c && current == null) {
			current = n;
			System.out.println("[ATOM]");
		} else if (current != null) {
			if (n instanceof FieldAssign || n instanceof ArrayAccessAssign) {
				System.out.println("\t[WRITE] " + n);
			} else if (n instanceof Field || n instanceof ArrayAccess) {
				System.out.println("\t[READ] " + n);
			} else {
				//System.out.println("\t" + n.getClass() + "\t" + n);
			}
		}
		return super.enter(n);
	}

	@Override
	public Node leave(Node old, Node n, NodeVisitor v) {
		if (old == current) {
			current = null;
			System.out.println("[END]");
		}
		return super.leave(old, n, v);
	}

}
