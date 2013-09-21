package polyglot.ext.ml5.bct.detection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import polyglot.ext.ml5.bct.tree.MyNode;
import polyglot.ext.ml5.bct.tree.NodeAccessMultiple;
import polyglot.ext.ml5.bct.tree.NodeAtom;
import polyglot.ext.ml5.bct.tree.NodeIf;
import polyglot.ext.ml5.bct.tree.NodeSequence;

public class Analyzer {
	
	private Collection<Anomaly> getAnomalies (NodeAccessMultiple a, NodeAccessMultiple b, NodeAccessMultiple foreign) {
		List<Anomaly> list = new LinkedList<Anomaly>();
		
		for (String field : a.fieldsToReads.keySet()) {
			if (a.reads(field) && foreign.writes(field)) {
				for (String otherField : b.fieldsToReads.keySet()) {
					if (!field.equals(otherField))
						list.add(new AnomalyRwR(field, otherField));
				}
			}
		}
		
		for (String field : a.fieldsToWrites.keySet()) {
			if (a.writes(field) && foreign.reads(field)) {
				for (String otherField : b.fieldsToWrites.keySet()) {
					if (!field.equals(otherField))
						list.add(new AnomalyWrW(field, otherField));
				}
			}
		}
		
		for (String field : a.fieldsToReads.keySet()) {
			if (a.reads(field) && foreign.writes(field) && b.writes(field)) {
				list.add(new AnomalyRwW(field));
			}
		}
		
		return list;
	}
	
	private Collection<Anomaly> getAnomalies (NodeAtom a, NodeAtom b, NodeAtom foreign) {
		if (!(a.body instanceof NodeAccessMultiple && b.body instanceof NodeAccessMultiple && foreign.body instanceof NodeAccessMultiple))
			throw new RuntimeException("Wrong tree structure");
		NodeAccessMultiple nama = (NodeAccessMultiple) a.body;
		NodeAccessMultiple namb = (NodeAccessMultiple) b.body;
		NodeAccessMultiple namc = (NodeAccessMultiple) foreign.body;
		Collection<Anomaly> anomalies = getAnomalies(nama, namb, namc);
		for (Anomaly anomaly : anomalies) {
			anomaly.atom1 = a;
			anomaly.atom2 = b;
			anomaly.threat = foreign;
		}
		return anomalies;
	}
	
	private Collection<Anomaly> getAnomalies (NodeAtom a, NodeAtom b, MyNode foreign) {
		
		Collection<Anomaly> anomalies;
		
		if (foreign instanceof NodeSequence) {
			NodeSequence seq = (NodeSequence) foreign;
			anomalies = new LinkedList<Anomaly>();
			for (MyNode node : seq) {
				anomalies.addAll(getAnomalies(a, b, node));
			}
		} else if (foreign instanceof NodeIf) {
			NodeIf nif = (NodeIf) foreign;
			anomalies = getAnomalies(a, b, nif.branchTrue);
			anomalies.addAll(getAnomalies(a, b, nif.branchFalse));
		} else if (foreign instanceof NodeAtom) {
			NodeAtom atom = (NodeAtom) foreign;
			anomalies = getAnomalies(a, b, atom);
		} else
			throw new RuntimeException("Wrong tree structure");
		
		return anomalies;
	}
	
	private Collection<Anomaly> getAnomalies (NodeAtom a, MyNode b, MyNode foreign) {
		List<Anomaly> list = new LinkedList<Anomaly>();
		
		if (b instanceof NodeIf) {
			NodeIf nif = (NodeIf) b;
			list.addAll(getAnomalies(a, nif.branchTrue, foreign));
			list.addAll(getAnomalies(a, nif.branchFalse, foreign));
		} else if (b instanceof NodeSequence) {
			NodeSequence seq = (NodeSequence) b;
			list.addAll(getAnomalies(a, seq.list.get(0), foreign));
			list.addAll(getAnomalies(seq, foreign));
		} else {
			if (!(b instanceof NodeAtom))
				throw new RuntimeException("Wrong tree structure");
			NodeAtom atom = (NodeAtom) b;
			list.addAll(getAnomalies(a, atom, foreign));
		}
		
		return list;
	}
	
	private Collection<Anomaly> getAnomalies (MyNode a, MyNode b, MyNode foreign) {
		List<Anomaly> list = new LinkedList<Anomaly>();
		
		if (a instanceof NodeIf) {
			NodeIf nif = (NodeIf) a;
			list.addAll(getAnomalies(nif.branchTrue, b, foreign));
			list.addAll(getAnomalies(nif.branchFalse, b, foreign));
		} else if (a instanceof NodeSequence) {
			NodeSequence seq = (NodeSequence) a;
			list.addAll(getAnomalies(seq, foreign));
			list.addAll(getAnomalies(seq.list.get(seq.list.size()-1), b, foreign));
		} else {
			if (!(a instanceof NodeAtom))
				throw new RuntimeException("Wrong tree structure");
			NodeAtom atom = (NodeAtom) a;
			list.addAll(getAnomalies(atom, b, foreign));
		}
		
		return list;
	}
	
	public Collection<Anomaly> getAnomalies (MyNode domestic, MyNode foreign) {
		List<Anomaly> list = new LinkedList<Anomaly>();
		if (domestic instanceof NodeSequence) {
			NodeSequence seq = (NodeSequence) domestic;
			for (int i = 1 ; i < seq.list.size() ; i++) {
				MyNode a = seq.list.get(i-1);
				MyNode b = seq.list.get(i);
				list.addAll(getAnomalies(a, b, foreign));
			}
		} else if (domestic instanceof NodeIf) {
			NodeIf nif = (NodeIf) domestic;
			list.addAll(getAnomalies(nif.branchTrue, foreign));
			list.addAll(getAnomalies(nif.branchFalse, foreign));
		}
		return list;
	}
}
