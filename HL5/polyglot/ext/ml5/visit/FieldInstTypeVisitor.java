package polyglot.ext.ml5.visit;

import polyglot.ast.FieldAssign;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl5.ast.JL5CanonicalTypeNode;
import polyglot.ext.jl5.ast.JL5Field;
import polyglot.ext.jl5.ast.JL5FieldDecl;
import polyglot.ext.jl5.ast.JL5New;
import polyglot.ext.ml5.visit.context.FieldContext;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.NodeVisitor;

public class FieldInstTypeVisitor extends AscriptionVisitor {

	protected FieldContext context;
	
	public FieldInstTypeVisitor(FieldContext context, Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		this.context = context;
	}
	
	public NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
		if (n instanceof JL5FieldDecl) {
			context.addField((JL5FieldDecl)n);
		}
		
		if (n instanceof JL5New) {
			if (parent instanceof FieldAssign) {
				if (((FieldAssign)parent).left() instanceof JL5Field) {
					JL5Field field = (JL5Field)((FieldAssign)parent).left();
					context.setFieldInstType(field, (JL5CanonicalTypeNode)((JL5New)n).objectType());
				}
			}
		}
		
		return super.enterCall(parent, n);
	}

}
