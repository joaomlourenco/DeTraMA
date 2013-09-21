package polyglot.ext.ml5.bct.detection;

import polyglot.ext.ml5.bct.tree.NodeAtom;

public abstract class Anomaly {
	public NodeAtom atom1, atom2, threat;

	@Override
	public String toString() {
		return "Anomaly [atom1=" + atom1.getNode().position() + ", atom2="
				+ atom2.getNode().position() + ", threat="
				+ threat.getNode().position() + "]";
	}

}
