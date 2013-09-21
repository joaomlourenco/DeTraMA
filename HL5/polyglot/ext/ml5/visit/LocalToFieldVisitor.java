package polyglot.ext.ml5.visit;

import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl.ast.Local_c;
import polyglot.ext.jl5.ast.JL5MethodDecl_c;
import polyglot.ext.ml5.ast.AtomicArrayFormal_c;
import polyglot.ext.ml5.ast.AtomicLocal_c;
import polyglot.ext.ml5.visit.context.AtomicFieldContext;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.NodeVisitor;

public class LocalToFieldVisitor extends AscriptionVisitor {

	private List<AtomicArrayFormal_c> formals;
	private JL5MethodDecl_c method;
	private AtomicFieldContext context;
	
	public LocalToFieldVisitor(List<AtomicArrayFormal_c> formals, 
			JL5MethodDecl_c method, AtomicFieldContext context, Job job, 
			TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		this.formals = formals;
		this.method = method;
		this.context = context;
	}
	
	public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
//		System.out.println("[LocalToFieldVisitor]: Found Local_c -> "+n);
		if (n instanceof Local_c) {
//			System.out.println("[LocalToFieldVisitor]: Found Local_c -> "+n);
			if (isInFormal(((Local_c)n).name())) {
				AtomicLocal_c al = new AtomicLocal_c((Local_c)n, n.position(), 
						((Local_c)n).name(), method);
//				System.out.println("ADD LOCAL FIELD -> "+PolyglotUtil.fieldSignature(al));
				context.addField(al);
				return al;
			}
		}
		
		return n;
	}
	
	private boolean isInFormal(String name) {
		for (AtomicArrayFormal_c f : formals) {
			if (f.name().compareTo(name) == 0) {
				return true;
			}
		}
		return false;
	}

}
