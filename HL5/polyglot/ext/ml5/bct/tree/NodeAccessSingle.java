package polyglot.ext.ml5.bct.tree;

import polyglot.ast.Field;
import polyglot.ast.Node;

public abstract class NodeAccessSingle extends NodeAccess {

	protected Field field;
	
	public NodeAccessSingle(Node node, Field field) {
		super(node);
		this.field = field;
	}

}
