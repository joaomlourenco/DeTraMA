package polyglot.ext.ml5.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl5.ast.JL5ClassBody_c;
import polyglot.ext.jl5.ast.JL5Formal_c;
import polyglot.ext.jl5.ast.JL5MethodDecl;
import polyglot.ext.jl5.ast.JL5MethodDecl_c;
import polyglot.ext.ml5.ast.AtomicArrayFormal_c;
import polyglot.ext.ml5.visit.context.ArrayMethodContext;
import polyglot.ext.ml5.visit.context.AtomicFieldContext;
import polyglot.ext.ml5.visit.context.ArrayMethodContext.ClassBodyStack;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

public class ArrayMethodReplication extends AscriptionVisitor {

	private ArrayMethodContext arrayContext;
	private AtomicFieldContext context;
	private TypeChecker tc;
	
	
	public ArrayMethodReplication(ArrayMethodContext arrayContext, AtomicFieldContext context, Job job, 
			TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		this.arrayContext = arrayContext;
		tc = new TypeChecker(job, ts, nf);
		this.context = context;
	}

	public NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
		if (n instanceof JL5ClassBody_c) {
			ClassBodyStack cbs = arrayContext.new ClassBodyStack(null, (JL5ClassBody_c)n);
			arrayContext.addClassBody(cbs);
		}
		else if (n instanceof JL5MethodDecl_c) {
			if (arrayContext.isArrayMethod((JL5MethodDecl)n)) {
				ClassBodyStack cbs = arrayContext.peekClassBody();
				JL5MethodDecl_c mym = (JL5MethodDecl_c)((JL5MethodDecl_c)n).copy();
				mym = applyMethodTransformation(mym);
				if (cbs.classBody == null) {
					cbs.classBody = (JL5ClassBody_c)((JL5ClassBody_c)parent).addMember(mym);
				}
				else {
					cbs.classBody = (JL5ClassBody_c)cbs.classBody.addMember(mym);
				}
			}
		}
		return super.enterCall(parent, n);
	}
	
	public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
		if (n instanceof JL5ClassBody_c) {
			ClassBodyStack cbs = arrayContext.getClassBody();
			if (cbs.classBody != null && n == cbs.origBody) {
				return cbs.classBody;
			}
		}
		
		return n;
	}
	
	private JL5MethodDecl_c applyMethodTransformation(JL5MethodDecl_c mym) throws SemanticException {
		List<JL5Formal_c> formals = new ArrayList<JL5Formal_c>();
		List<AtomicArrayFormal_c> atomicFormals = new ArrayList<AtomicArrayFormal_c>();
		for (int i=0; i < mym.formals().size(); i++) {
			JL5Formal_c f = (JL5Formal_c)mym.formals().get(i);
			if (f.type().type().isArray()) {
				AtomicArrayFormal_c fa = new AtomicArrayFormal_c(f);
				fa = (AtomicArrayFormal_c)fa.typeCheck(tc);
				//System.out.println("LOCAL INSTANCE -> "+fa.localInstance());
				formals.add(fa);
				atomicFormals.add(fa);
			}
			else {
				formals.add(f);
			}
		}
		mym = (JL5MethodDecl_c)mym.formals(formals);
		
		//System.out.println("START LOCAL TO FIELD");
		NodeVisitor nv = new LocalToFieldVisitor(atomicFormals, mym, context, job, ts, nf);
		nv.begin();
		mym = (JL5MethodDecl_c)mym.visitChildren(nv);
		nv.finish();
		
		return mym;
	}
}
