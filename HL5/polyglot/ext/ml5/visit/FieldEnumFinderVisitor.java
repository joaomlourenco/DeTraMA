package polyglot.ext.ml5.visit;


import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl5.ast.EnumConstantDecl;
import polyglot.ext.jl5.ast.EnumConstantDecl_c;
import polyglot.ext.ml5.visit.context.EnumContext;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.NodeVisitor;

public class FieldEnumFinderVisitor extends AscriptionVisitor {

	private EnumContext context;
	
	public FieldEnumFinderVisitor(EnumContext context, Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		this.context = context;
	}
	
	public NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
		if (n instanceof EnumConstantDecl_c) {
			context.addEnumDecl((EnumConstantDecl)n);
		}
		
		return super.enterCall(parent, n);
	}

}
