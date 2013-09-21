package polyglot.ext.ml5.bct.tree;

import polyglot.ast.Node;

public abstract class NodeAccess extends MyNode {
	
	public NodeAccess(Node node) {
		super(node);
	}
	
	protected abstract void addTo (NodeAccessMultiple other);

}
