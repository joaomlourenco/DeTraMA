package polyglot.ext.ml5.bct.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import polyglot.ast.Node;

public class NodeSequence extends MyNode implements Iterable<MyNode> {

	public List<MyNode> list;
	
	public NodeSequence(Node node) {
		super (node);
		list = new LinkedList<MyNode>();
	}
	
	public NodeSequence(Node node, List<MyNode> list) {
		super(node);
		this.list = list;
	}
	
	public void append(MyNode node) {
		list.add(node);
	}

	@Override
	public MyNode accept(Visitor visitor) {
		return visitor.visit(this);
	}

	public Iterator<MyNode> iterator() {
		return list.iterator();
	}

	@Override
	public MyNode copy() {
		return new NodeSequence(astNode, list);
	}

	@Override
	public String toString() {
		return "<<<SEQUENCE>>> " + list.size() + " nodes";
	}


}
