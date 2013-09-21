package polyglot.ext.ml5.visit;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl5.ast.JL5Call;
import polyglot.ext.jl5.ast.JL5Field;
import polyglot.ext.ml5.visit.context.ArrayMethodContext;
import polyglot.ext.ml5.visit.context.AtomicFieldContext;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.NodeVisitor;

public class AtomicArrayMethodFinderVisitor extends AscriptionVisitor {

	private AtomicFieldContext fieldContext;
	private ArrayMethodContext arrayContext;
	
	public AtomicArrayMethodFinderVisitor(AtomicFieldContext fieldContext,
			ArrayMethodContext arrayContext, Job job, TypeSystem ts, 
			NodeFactory nf) {
		super(job, ts, nf);
		this.fieldContext = fieldContext;
		this.arrayContext = arrayContext;
	}
	
	public NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
		if ((n instanceof JL5Field) && (parent instanceof JL5Call)
				&& ((JL5Field)n).type().isArray()) {
			if (fieldContext.containsField((JL5Field)n)) {
				
				arrayContext.addMethodCall((JL5Call)parent);
			}
		}
		return super.enterCall(parent, n);
	}

}
