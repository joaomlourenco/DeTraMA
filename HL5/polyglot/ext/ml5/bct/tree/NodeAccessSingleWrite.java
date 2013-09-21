package polyglot.ext.ml5.bct.tree;

import polyglot.ast.Field;
import polyglot.ast.Node;

public class NodeAccessSingleWrite extends NodeAccessSingle {

	public NodeAccessSingleWrite(Node node, Field field) {
		super(node, field);
	}

	@Override
	public MyNode accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "WRITE:" + this.astNode;
	}
	
	@Override
	public MyNode copy() {
		return new NodeAccessSingleWrite(astNode, field);
	}

	@Override
	protected void addTo(NodeAccessMultiple other) {
		other.add(this);
	}

}
