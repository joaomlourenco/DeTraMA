package polyglot.ext.ml5.bct.tree;

import java.util.List;
import java.util.Map;

public class Visitor3AtomPrinter extends Visitor {

	private int tab;
	private int depth;
	
	public Visitor3AtomPrinter () {
		this.tab = 0;
		this.depth = 0;
	}
	
	private void makeTabs () {
		for (int i = 0 ; i < tab ; i++)
			System.out.print("\t");
	}
	
	public void print (Object msg) {
		makeTabs();
		System.out.println(msg);
	}
	
	public void inc() {
		tab++;
	}
	
	public void dec() {
		tab--;
	}
	
	@Override
	public MyNode visit(NodeAccessSingleRead access) {
		print("| R:" + access.astNode + "");
		return access;
	}
	
	@Override
	public MyNode visit(NodeAccessSingleWrite access) {
		print("| W:" + access.astNode + "");
		return access;
	}

	@Override
	public MyNode visit(NodeAtom atom) {
		depth++;
		print("+--------+");
		print("|  ATOM  |\t" + atom.astNode.position());
		atom.body.accept(this);
		print("+--------+");
		depth--;
		return atom;
	}

	@Override
	public MyNode visit(NodeCall call) {
		print (call);
		return call;
	}

	@Override
	public MyNode visit(NodeIf nif) {
		print("+--------+");
		print("|   IF   |");
		print("+--------+");
		print("    |");
		print("    |");
		inc();
		nif.branchTrue.accept(this);
		if (nif.branchFalse != null) {
			print ("");
			print ("    OR");
			print ("");
			nif.branchFalse.accept(this);
		}
		dec();
		return nif;
	}

	@Override
	public MyNode visit(NodeLoop loop) {
		print ("[LOOP]");
		inc();
		loop.body.accept(this);
		dec();
		return loop;
	}

	@Override
	public MyNode visit(NodeSequence seq) {
		boolean first = true;
		for (MyNode node : seq) {
			if (first)
				first = false;
			else if (depth == 0){
				print("    |");
				print("    |");
			}
			node.accept(this);
		}
		return seq;
	}

	@Override
	public MyNode visit(NodeAccessMultiple access) {
		//print ("| " + access);
		//for (NodeAccess child : access.accesses) {
		//	print("|   " + child.getNode() + " @ " + child.getNode().position());
		//}
		
		for (Map.Entry<String, List<NodeAccessSingleRead>> entry : access.fieldsToReads.entrySet()) {
			String sig = entry.getKey();
			List<NodeAccessSingleRead> lst = entry.getValue();
			print ("|   R: " + sig + " (" + lst.size() + ")");
		}
		
		for (Map.Entry<String, List<NodeAccessSingleWrite>> entry : access.fieldsToWrites.entrySet()) {
			String sig = entry.getKey();
			List<NodeAccessSingleWrite> lst = entry.getValue();
			print ("|   W: " + sig + " (" + lst.size() + ")");
		}
		
		return access;
	}
}
