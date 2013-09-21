package polyglot.ext.ml5.bct.tree;

import polyglot.ast.Node;

public abstract class MyNode {
	protected Node astNode;
	
	public MyNode (Node node) {
		this.astNode = node;
	}

	public Node getNode() {
		return astNode;
	}

	public void setNode(Node astNode) {
		this.astNode = astNode;
	}
	
	public abstract MyNode accept (Visitor visitor);
	
	public String toString() {
		return astNode.toString();
	}
	
	public abstract MyNode copy();
}
