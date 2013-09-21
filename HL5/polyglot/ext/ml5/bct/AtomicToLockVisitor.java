package polyglot.ext.ml5.bct;

import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Synchronized;
import polyglot.ext.ml5.ast.AtomicBlock_c;
import polyglot.ext.ml5.ast.MyLang5NodeFactory;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

public class AtomicToLockVisitor extends NodeVisitor {
	
	//private int n = 0;
	//private String mainClass;
	private String globalLock;// = null;
	GlobalLockGeneratorVisitor glgv;
	private TypeSystem ts;
	private MyLang5NodeFactory nf;
	private Expr lockNode;

	public AtomicToLockVisitor(GlobalLockGeneratorVisitor glgv, TypeSystem ts, MyLang5NodeFactory nf) {
		super();
		this.glgv = glgv;
		this.ts = ts;
		this.nf = nf;
	}

	@Override
	public NodeVisitor begin() {
		if (!glgv.isDone()) {
			throw new RuntimeException(
					"\nCould not generate a global lock --\nDid you specify your main class correctly?\nMust be full name, with package");
		}
		this.globalLock = this.glgv.getFullLockName();
		//Expr expr = nf.AmbExpr(Position.COMPILER_GENERATED, globalLock);
		//Prefix prefix = nf.AmbPrefix(Position.COMPILER_GENERATED, globalLock);
		int i = globalLock.lastIndexOf(".");
		String prefix = globalLock.substring(0, i);
		String suffix = globalLock.substring(i+1);
		Receiver rcv = nf.AmbReceiver(Position.COMPILER_GENERATED, prefix);
		Field f = nf.Field(Position.COMPILER_GENERATED, rcv, suffix);
		lockNode = f;
		return super.begin();
	}

	@Override
	public NodeVisitor enter(Node parent, Node n) {
		return super.enter(parent, n);
	}
	
	@Override
	public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
		if (n instanceof AtomicBlock_c) {
			AtomicBlock_c ab = (AtomicBlock_c) n;
			Block block = nf.Block(Position.COMPILER_GENERATED, ab.statements());
			Synchronized sync = nf.Synchronized(Position.COMPILER_GENERATED, (Field)lockNode.copy(), block);
			return sync;
		}
		return n;
	}

}
