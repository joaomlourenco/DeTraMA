package polyglot.ext.ml5.bct.detection;

import java.util.LinkedList;

import polyglot.ext.ml5.bct.tree.MyNode;
import polyglot.ext.ml5.bct.tree.NodeAccessMultiple;
import polyglot.ext.ml5.bct.tree.NodeAtom;
import polyglot.ext.ml5.bct.tree.NodeSequence;

public class Trace extends LinkedList<NodeAccessMultiple> {

	private static final long serialVersionUID = 1L;
	
	private Trace () {
		super();
	}

	/**
	 * Takes a MyNode that should be a sequence containing only atoms,
	 * each atom containing exactly one multiple access.
	 * If any class cast fails, handle exception and return null;
	 * @return list of atoms
	 */
	public static Trace get(MyNode node) {
		Trace trace = new Trace();
		try {
			NodeSequence seq = (NodeSequence) node;
			for (MyNode child : seq) {
				NodeAtom atom = (NodeAtom) child;
				NodeAccessMultiple nam = (NodeAccessMultiple) atom.body;
				trace.add(nam);
			}
			return trace;
		} catch (ClassCastException e) {
			return null;
		}
	}
}
