package polyglot.ext.ml5.bct.tree;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ext.jl5.ast.JL5Call;
import polyglot.ext.ml5.util.PolyglotUtil;

public class Visitor1AtomTracker extends Visitor {

	protected int depth;
	//atom nesting depth. depth==0 means there is no transaction, depth==2 means
	//there is a transaction inside another.
	
	protected Map<String, MyNode> methods;
	protected Set<String> visitingMethods;
	protected final String method;
	
	public Visitor1AtomTracker (String methodSig, Map<String, MyNode> methods) {
		this.depth = 0;
		this.methods = methods;
		this.visitingMethods = new HashSet<String>();
		this.method = methodSig;
	}
	
	public MyNode visit() {
		return callMethod(method);
	}
	
	protected MyNode callMethod (String sig) {
		if (visitingMethods.contains(sig))
			return null;
			//TODO: allow first recursion
		if (!methods.containsKey(sig))
			return null;
		visitingMethods.add(sig);
		MyNode node = methods.get(sig).accept(this);
		visitingMethods.remove(sig);
		return node;
	}
	
	public void inc() {
		depth++;
	}
	
	public void dec() {
		depth--;
	}
	
	
	/********************** VISITS **********************/
	@Override
	public MyNode visit(NodeAccessSingleRead access) {
		if (depth == 0)
			return null;
		return access.copy();
	}
	
	@Override
	public MyNode visit(NodeAccessSingleWrite access) {
		if (depth == 0)
			return null;
		return access.copy();
	}

	@Override
	public MyNode visit(NodeAtom atom) {
		inc();
		MyNode node = atom.body.accept(this);
		dec();
		return new NodeAtom(atom.astNode, node);
	}

	@Override
	public MyNode visit(NodeCall call) {
		String sig = PolyglotUtil.methodSignature((JL5Call)call.getNode());
		return callMethod(sig);
	}

	@Override
	public MyNode visit(NodeIf node) {
		NodeIf nif = (NodeIf) node.copy();
		nif.branchTrue = nif.branchTrue.accept(this);
		if (nif.branchFalse != null)
			nif.branchFalse = nif.branchFalse.accept(this);
		
		if (nif.branchTrue == null && nif.branchFalse == null) //redundant, if both are false one of the following will be triggered and the result will be the same
			return null;
		if (nif.branchTrue == null)
			return nif.branchFalse;
		if (nif.branchFalse == null)
			return nif.branchTrue;
		
		// if we reach here, then neither is null
		if (depth > 0) {
			NodeSequence seq = new NodeSequence(nif.getNode());
			seq.append(nif.branchTrue);
			seq.append(nif.branchFalse);
			return seq;
		}
		
		return nif; // if none is null, we have to consider the fork
	}

	@Override
	public MyNode visit(NodeLoop loop) {
		//TODO: IMPLEMENT LOOP EXPANSION COUNTER
		return loop.body.accept(this);
	}

	@Override
	public MyNode visit(NodeSequence seq) {
		List<MyNode> list = new LinkedList<MyNode>();
		for (MyNode child : seq) {
			MyNode node = child.accept(this);
			if (node != null) {
				//avoid redundant sequences, i.e. resulting from expansion of IF nodes
				//FIXME: WE ARE STILL GETTING REDUNTANT SEQUENCES
				if (node instanceof NodeSequence) {
					for (MyNode aux : (NodeSequence) node) {
						list.add(aux);
					}
				} else
					list.add(node);
			}
		}
		if (list.isEmpty())
			return null;
		if (list.size() == 1)
			return list.get(0);
		return new NodeSequence(seq.getNode(), list);
	}

	@Override
	public MyNode visit(NodeAccessMultiple access) {
		if (depth == 0)
			return null;
		return access.copy();
	}
}
