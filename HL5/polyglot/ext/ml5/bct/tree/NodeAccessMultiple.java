package polyglot.ext.ml5.bct.tree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import polyglot.ext.jl5.ast.JL5Field;
import polyglot.ext.ml5.bct.test.Utils;
import polyglot.ext.ml5.util.PolyglotUtil;

public class NodeAccessMultiple extends NodeAccess {

	//FIXME: shouldn't be public, but couldn't find a better way
	public List<NodeAccessSingle> accesses;
	public List<NodeAccessSingleRead> reads;
	public List<NodeAccessSingleWrite> writes;
	public Map<String, List<NodeAccessSingle>> fieldsToAll;
	public Map<String, List<NodeAccessSingleRead>> fieldsToReads;
	public Map<String, List<NodeAccessSingleWrite>> fieldsToWrites;
	
	public NodeAccessMultiple(List<NodeAccessSingle> list) {
		super(null);
		this.accesses = new LinkedList<NodeAccessSingle>();
		this.reads = new LinkedList<NodeAccessSingleRead>();
		this.writes = new LinkedList<NodeAccessSingleWrite>();
		this.fieldsToAll = new HashMap<String, List<NodeAccessSingle>>();
		this.fieldsToReads = new HashMap<String, List<NodeAccessSingleRead>>();
		this.fieldsToWrites = new HashMap<String, List<NodeAccessSingleWrite>>();
		if (list != null)
			for (NodeAccessSingle access : list) {
				this.add(access);
			}
	}
	
	public NodeAccessMultiple () {
		this(null);
	}

	@Override
	public MyNode accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public MyNode copy() {
		return new NodeAccessMultiple(this.accesses);
	}

	@Override
	public String toString() {
		return "READ/WRITE: " + accesses.size() + " fields";
	}

	public void add(NodeAccess access) {
		access.addTo(this);
	}
	
	// double-dispatch pwns!
	protected void add(NodeAccessSingleRead access) {
		accesses.add(access);
		reads.add(access);
		String sig = Utils.fieldSignature((JL5Field)access.field);
		if (fieldsToAll.containsKey(sig)) {
			List<NodeAccessSingle> lst = fieldsToAll.get(sig);
			lst.add(access);
		} else {
			List<NodeAccessSingle> lst = new LinkedList<NodeAccessSingle>();
			lst.add(access);
			fieldsToAll.put(sig, lst);
		}
		if (fieldsToReads.containsKey(sig)) {
			List<NodeAccessSingleRead> lst = fieldsToReads.get(sig);
			lst.add(access);
		} else {
			List<NodeAccessSingleRead> lst = new LinkedList<NodeAccessSingleRead>();
			lst.add(access);
			fieldsToReads.put(sig, lst);
		}
	}
	
	protected void add(NodeAccessSingleWrite access) {
		accesses.add(access);
		writes.add(access);
		String sig = Utils.fieldSignature((JL5Field)access.field);
		if (fieldsToAll.containsKey(sig)) {
			List<NodeAccessSingle> lst = fieldsToAll.get(sig);
			lst.add(access);
		} else {
			List<NodeAccessSingle> lst = new LinkedList<NodeAccessSingle>();
			lst.add(access);
			fieldsToAll.put(sig, lst);
		}
		if (fieldsToWrites.containsKey(sig)) {
			List<NodeAccessSingleWrite> lst = fieldsToWrites.get(sig);
			lst.add(access);
		} else {
			List<NodeAccessSingleWrite> lst = new LinkedList<NodeAccessSingleWrite>();
			lst.add(access);
			fieldsToWrites.put(sig, lst);
		}
	}
	
	public boolean isEmpty() {
		return accesses.isEmpty();
	}

	@Override
	protected void addTo(NodeAccessMultiple other) {
		for (NodeAccessSingle access : this.accesses) {
			access.addTo(other);
		}
	}

	public boolean reads (String field) {
		return fieldsToReads.containsKey(field) && !fieldsToReads.get(field).isEmpty();
	}
	
	public boolean writes (String field) {
		return fieldsToWrites.containsKey(field) && !fieldsToWrites.get(field).isEmpty();
	}

}
