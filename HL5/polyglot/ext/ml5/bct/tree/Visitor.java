package polyglot.ext.ml5.bct.tree;

public abstract class Visitor {
	/**
	public MyNode visit (MyNode node) {
		System.out.println("UNIMPLEMENTED FEATURE");
	}
	/**/
	//public abstract MyNode visit (NodeAccess access);
	public abstract MyNode visit (NodeAccessSingleRead access);
	public abstract MyNode visit (NodeAccessSingleWrite access);
	public abstract MyNode visit (NodeAtom atom);
	public abstract MyNode visit (NodeCall call);
	public abstract MyNode visit (NodeIf nif);
	public abstract MyNode visit (NodeLoop loop);
	public abstract MyNode visit (NodeSequence seq);
	
	public abstract MyNode visit (NodeAccessMultiple access);
}
