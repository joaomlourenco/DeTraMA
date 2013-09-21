package polyglot.ext.ml5.bct.tree;

import polyglot.ast.Field;
import polyglot.ast.Node;

public class NodeAccessSingleRead extends NodeAccessSingle {

	public NodeAccessSingleRead(Node node, Field field) {
		super(node, field);
	}

	@Override
	public MyNode accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "READ:" + this.astNode;
	}

	@Override
	public MyNode copy() {
		return new NodeAccessSingleRead(this.astNode, this.field);
	}

	@Override
	protected void addTo(NodeAccessMultiple other) {
		other.add(this);
	}
	
}
