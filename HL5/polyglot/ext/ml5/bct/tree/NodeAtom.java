package polyglot.ext.ml5.bct.tree;

import polyglot.ast.Node;

public class NodeAtom extends MyNode {

	public MyNode body;
	
	public NodeAtom(Node node) {
		super(node);
	}

	public NodeAtom(Node node, MyNode body) {
		super(node);
		this.body = body;
	}

	@Override
	public MyNode accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public MyNode copy() {
		return new NodeAtom(astNode, body);
	}

	@Override
	public String toString() {
		return "Atom @ " + astNode.position();
	}
	
}
