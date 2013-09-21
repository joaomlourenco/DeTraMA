package polyglot.ext.ml5.visit;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl5.ast.JL5MethodDecl;
import polyglot.ext.ml5.visit.context.MethodTracker;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.NodeVisitor;

public class MethodTrackerVisitor extends AscriptionVisitor {

	protected MethodTracker methodContainer;
	
	public MethodTrackerVisitor(Job job, TypeSystem ts, NodeFactory nf) {
		this(new MethodTracker(), job, ts, nf);
	}
	
	public MethodTrackerVisitor(MethodTracker methodContainer, Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		this.methodContainer = methodContainer;
	}
	
	public NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
		if (n instanceof JL5MethodDecl) {
			JL5MethodDecl node = (JL5MethodDecl)n;
			if (methodContainer.contains(node)) {
				return bypassChildren(node);
			}
			else {
				methodContainer.addMethod(node);
			}
		}
		return super.enterCall(parent, n);
	}

}
