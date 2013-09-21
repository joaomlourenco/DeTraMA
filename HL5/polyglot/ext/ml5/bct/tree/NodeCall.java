package polyglot.ext.ml5.bct.tree;

import polyglot.ast.Node;

public class NodeCall extends MyNode {

	public NodeCall(Node node) {
		super(node);
	}

	@Override
	public MyNode accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "CALL:" + astNode;
	}

	@Override
	public MyNode copy() {
		return new NodeCall(astNode);
	}
	
}
