package polyglot.ext.ml5.bct.tree;

import polyglot.ast.Node;

public class NodeLoop extends MyNode {
	
	protected MyNode body;
	
	public NodeLoop(Node node) {
		super(node);
	}

	public NodeLoop(Node node, MyNode body) {
		super(node);
		this.body = body;
	}

	@Override
	public MyNode accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public MyNode copy() {
		return new NodeLoop(astNode, body);
	}

	@Override
	public String toString() {
		return "Loop @ " + astNode.position();
	}
}
