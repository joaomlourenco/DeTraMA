package polyglot.ext.ml5.bct.tree;

import polyglot.ast.Node;

public class NodeIf extends MyNode {

	public NodeIf(Node node) {
		super(node);
	}

	public NodeIf(Node node, MyNode branchTrue, MyNode branchFalse) {
		super(node);
		this.branchTrue = branchTrue;
		this.branchFalse = branchFalse;
	}

	public MyNode branchTrue, branchFalse;

	@Override
	public MyNode accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public MyNode copy() {
		return new NodeIf(astNode, branchTrue, branchFalse);
	}

	@Override
	public String toString() {
		return "If @ " + astNode.position();
	}
	
	
}
