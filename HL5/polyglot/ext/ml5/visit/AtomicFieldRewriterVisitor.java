package polyglot.ext.ml5.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ArrayAccess;
import polyglot.ast.ArrayAccessAssign;
import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.FieldAssign;
import polyglot.ast.IntLit;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Precedence;
import polyglot.ast.Unary;
import polyglot.ext.jl.ast.ArrayAccessAssign_c;
import polyglot.ext.jl.ast.Binary_c;
import polyglot.ext.jl.ast.IntLit_c;
import polyglot.ext.jl5.ast.JL5Field;
import polyglot.ext.jl5.ast.JL5FieldDecl;
import polyglot.ext.jl5.ast.JL5FieldDecl_c;
import polyglot.ext.jl5.ast.JL5NewArray;
import polyglot.ext.jl5.types.FlagAnnotations;
import polyglot.ext.ml5.ast.AtomicCall_c;
import polyglot.ext.ml5.ast.AtomicFieldDecl_c;
import polyglot.ext.ml5.ast.AtomicLocal_c;
import polyglot.ext.ml5.ast.AtomicNewArrayAssign_c;
import polyglot.ext.ml5.ast.AtomicNewArray_c;
import polyglot.ext.ml5.ast.MyLang5NodeFactory;
import polyglot.ext.ml5.visit.context.AtomicFieldContext;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;

public class AtomicFieldRewriterVisitor extends NodeVisitor {

	protected AtomicFieldContext atomicContext;
	protected MyLang5NodeFactory nf;
	protected TypeSystem ts;
	protected Job job;
	
	public AtomicFieldRewriterVisitor(AtomicFieldContext atomicContext, Job job, TypeSystem ts, NodeFactory nf) {
		super();
		this.job = job;
		this.atomicContext = atomicContext;
		this.nf = (MyLang5NodeFactory)nf;
		this.ts = ts;
		//System.out.println("-- AtomicFieldRewriter Instantiated");
	}
	
	private class SubstStack {
		public SubstStack prev;
		public Node orig;
		public Node subst;
		public Unary isUnary;
		
		public SubstStack(SubstStack prev, Node orig, Node subst) {
			this.prev = prev;
			this.orig = orig;
			this.subst = subst;
			isUnary = null;
		}
		
		public SubstStack beginScope() {
			return new SubstStack(this, null, null);
		}
		
		public SubstStack endScope() {
			return prev;
		}
		
	}
	
	private SubstStack stack = new SubstStack(null, null, null);
	
	public NodeVisitor enter(Node parent, Node n) {
		
		//System.out.println("ENTER: "+n+" -> "+n.getClass());
		
		stack = stack.beginScope();

		if (n instanceof JL5Field && !(parent instanceof FieldAssign) /*&&
				!(parent instanceof ArrayAccess)*/ /*&& !(((JL5Field)n).type().isArray())*/
				&& !(n instanceof AtomicLocal_c)) {
			if (atomicContext.containsField((JL5Field)n)) {
					//System.out.println("ACERTOU---> "+n);
	//				stack.subst = new AtomicCall_c(n.position(), (JL5Field)n, 
	//						"get", new ArrayList(), new ArrayList());
					stack.orig = n;
					if (parent instanceof Unary && 
							(((Unary)parent).operator() == Unary.POST_INC ||
							((Unary)parent).operator() == Unary.POST_DEC)) {
						stack.isUnary = (Unary)parent;
					}
			}
		}
		
		if (n instanceof JL5Field && !(parent instanceof FieldAssign) &&
				(parent instanceof ArrayAccess) && ((ArrayAccess)parent).index() == n) {
			if (atomicContext.containsField((JL5Field)n)) {
//				stack.subst = new AtomicCall_c(n.position(), (JL5Field)n, 
//						"get", new ArrayList(), new ArrayList());
				stack.orig = n;
			}
		}
		
		if (n instanceof JL5Field && (parent instanceof FieldAssign) &&
				/*!(((JL5Field)n).type().isArray()) &&*/
				((FieldAssign)parent).right() == n) {
			if (atomicContext.containsField((JL5Field)n)) {
					stack.orig = n;
			}
		}
		
		if (n instanceof ArrayAccess && !(parent instanceof ArrayAccessAssign) &&
				!(parent instanceof ArrayAccess) && !((ArrayAccess)n).type().isArray()) {
			if (((ArrayAccess)n).array() instanceof JL5Field) {
//				System.out.println("ACERTOU---> "+n+" --> "+n.hashCode());
				if (atomicContext.containsField((JL5Field)((ArrayAccess)n).array())) {
//					stack.subst = new AtomicCall_c(n.position(), (ArrayAccess)n, 
//							"get", new ArrayList(), new ArrayList());
					stack.orig = n;
				}
			}
			else if (((ArrayAccess)n).array() instanceof ArrayAccess) {
				ArrayAccess arr = (ArrayAccess)n;
				while(arr.array() instanceof ArrayAccess) { arr = (ArrayAccess)arr.array(); }
				if (arr.array() instanceof JL5Field) {
					if (atomicContext.containsField((JL5Field)arr.array())) {
//						stack.subst = new AtomicCall_c(n.position(), (ArrayAccess)n, 
//								"get", new ArrayList(), new ArrayList());
						stack.orig = n;
					}
				}
			}
		}
		else if (n instanceof ArrayAccess && 
				(parent instanceof ArrayAccess) &&
				((ArrayAccess)parent).index() == n) {
			//System.out.println("HHHHHEEEEELLOO -> "+n+" ---> "+((ArrayAccess)parent).index());
			ArrayAccess arr = (ArrayAccess)n;
			while(arr.array() instanceof ArrayAccess) { arr = (ArrayAccess)arr.array(); }
			if (arr.array() instanceof JL5Field) {
				if (atomicContext.containsField((JL5Field)arr.array())) {
					stack.orig = n;
				}
			}
		}
		
		
		
		
		if (n instanceof ArrayAccess && (parent instanceof ArrayAccessAssign) &&
				((ArrayAccessAssign)parent).right() == n) {
			ArrayAccess arr = (ArrayAccess)n;
			while(arr.array() instanceof ArrayAccess) { arr = (ArrayAccess)arr.array(); }
			if (arr.array() instanceof JL5Field) {
				if (atomicContext.containsField((JL5Field)arr.array())) {
//					stack.subst = new AtomicCall_c(n.position(), (ArrayAccess)n, 
//							"get", new ArrayList(), new ArrayList());
					stack.orig = n;
				}
			}
		}
		
		return super.enter(parent, n);
		
	}
	
	@SuppressWarnings("unchecked")
	public Node leave(Node old, Node n, NodeVisitor v) {
		//System.out.println("LEAVE: "+n+" --> "+n.getClass());
		if (n instanceof JL5FieldDecl) {

			if (atomicContext.containsField((JL5FieldDecl)n)) {
				//System.out.println("ATOMIC FIELD DECL ->"+n);
				JL5FieldDecl fd = (JL5FieldDecl)n;
				return new AtomicFieldDecl_c(fd.position(),
					new FlagAnnotations(fd.flags(), ((JL5FieldDecl_c)fd).annotations()), 
					fd.type(), fd.name(), fd.init(), ts);
			}
		}
		
		
		if (n instanceof ArrayAccessAssign) {
			if (((ArrayAccessAssign)n).left() instanceof ArrayAccess && 
					!(((ArrayAccessAssign)n).right() instanceof JL5NewArray)) {
				
				
				ArrayAccess arr = (ArrayAccess)((ArrayAccessAssign)n).left();
				while(arr.array() instanceof ArrayAccess) { arr = (ArrayAccess)arr.array(); }
				//System.out.println("ACERTEI --> "+arr.array().getClass());
				if (arr.array() instanceof JL5Field) {
					if (atomicContext.containsField((JL5Field)arr.array())) {
						List l = new ArrayList(); 
						l.add(((ArrayAccessAssign)n).right());
						return new AtomicCall_c(n.position(), ((ArrayAccessAssign)n).left(), 
								"set", l, new ArrayList());
					}
				}
				if (arr.array() instanceof AtomicCall_c) {
					if (atomicContext.containsField((JL5Field)((AtomicCall_c)arr.array()).target())) {
						List l = new ArrayList(); 
						l.add(((ArrayAccessAssign)n).right());
						return new AtomicCall_c(n.position(), ((ArrayAccessAssign)n).left(), 
								"set", l, new ArrayList());
					}
				}
			}
			else if (((ArrayAccessAssign)n).left() instanceof ArrayAccess && 
					(((ArrayAccessAssign)n).right() instanceof JL5NewArray)) {
				JL5NewArray na = (JL5NewArray)((ArrayAccessAssign)n).right();
				List l = new ArrayList();
				AtomicNewArray_c arr = new AtomicNewArray_c(((ArrayAccess)((ArrayAccessAssign)n).left()).toString(), ts, na.position(), na.baseType(), na.dims(),
						na.additionalDims(), na.init());
				
				arr.setPrecedence(Precedence.LITERAL);
				ArrayAccessAssign_c aaa = (ArrayAccessAssign_c)((ArrayAccessAssign_c)n).right(arr);
				
				AtomicNewArrayAssign_c anaa = new AtomicNewArrayAssign_c(aaa.position(), (ArrayAccess)aaa.left(), aaa.operator(), (AtomicNewArray_c)aaa.right());
				
				return anaa;
			}
		}
		
		if (n instanceof Unary) {
			if (old != n && 
					(((Unary)n).operator() == Unary.POST_INC || 
					((Unary)n).operator() == Unary.POST_DEC)) {
				return ((Unary)n).expr();
			}
		}
		
		if (n instanceof JL5Field) {
//			if (stack.orig != null) {
//				System.out.println("ACERTOU LEAVE --> "+old+" --> "+(old == stack.orig));
//			}
			
			if (old == stack.orig && stack.orig != null) {
				stack.orig = null;
				if (stack.isUnary == null) {
					stack.subst = new AtomicCall_c(n.position(), (JL5Field)n, 
							"get", new ArrayList(), new ArrayList());
				}
				else {
					ArrayList args = new ArrayList();
					args.add(((JL5Field)n));
					Expr right = new Binary_c(n.position(), new AtomicCall_c(n.position(), (JL5Field)n, 
							"get", new ArrayList(), new ArrayList()), 
							stack.isUnary.operator() == Unary.POST_INC ? Binary.ADD : Binary.SUB, (Expr)new IntLit_c(n.position(), IntLit.INT, 1));
					args.add(right);
					
					stack.subst = new AtomicCall_c(n.position(), null, 
							polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".ITObject.Aux.getAndSet",
									args, new ArrayList());
				}
				Node res = stack.subst;
				stack.isUnary = null;
				stack = stack.endScope();
				return res;
			}
		}

		
		if (n instanceof FieldAssign) {
			
			
			
			if (((FieldAssign)n).left() instanceof JL5Field) {
				if (atomicContext.containsField((JL5Field)((FieldAssign)n).left()) /*&& 
						!((JL5Field)((FieldAssign)n).left()).type().isArray()*/) {
					
					
					if (!(((FieldAssign)n).right() instanceof JL5NewArray)) {
						//if (!((JL5Field)((FieldAssign)n).left()).type().isArray()) {
						List l = new ArrayList(); 
						l.add(((FieldAssign)n).right());
						return new AtomicCall_c(n.position(), ((FieldAssign)n).left(), 
								"set", l, new ArrayList());
						//}
					}
					else {
						JL5NewArray na = (JL5NewArray)((FieldAssign)n).right();
						List l = new ArrayList();
						AtomicNewArray_c arr = new AtomicNewArray_c(((JL5Field)((FieldAssign)n).left()).name()+".get()", ts, na.position(), na.baseType(), na.dims(),
								na.additionalDims(), na.init());
						
						arr.setPrecedence(Precedence.LITERAL);
						//FieldAssign_c fa = (FieldAssign_c)((FieldAssign_c)n).right(arr);
						
						List l2 = new ArrayList(); 
						l2.add(arr);
						return new AtomicCall_c(n.position(), ((FieldAssign)n).left(), 
								"set", l2, new ArrayList());
					}
				}
		
			}
		}
		
		if (n instanceof ArrayAccess) {
//			if (stack.orig != null) {
//				System.out.println("ACERTOU LEAVE --> "+n+" --> "+(old == stack.orig));
//			}
			if (old == stack.orig && stack.orig != null) {
				stack.orig = null;
				stack.subst = new AtomicCall_c(n.position(), (ArrayAccess)n, 
						"get", new ArrayList(), new ArrayList());
				Node res = stack.subst;
				stack = stack.endScope();
				return res;
			}
		}
		
		stack = stack.endScope();
		
		return super.leave(old, n, v);
	}
	
	
	

}
