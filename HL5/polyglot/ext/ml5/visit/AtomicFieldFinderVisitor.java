package polyglot.ext.ml5.visit;

import polyglot.ast.ArrayAccess;
import polyglot.ast.ArrayAccessAssign;
import polyglot.ast.FieldAssign;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.jl5.ast.JL5Call;
import polyglot.ext.jl5.ast.JL5CanonicalTypeNode;
import polyglot.ext.jl5.ast.JL5Field;
import polyglot.ext.jl5.ast.JL5MethodDecl;
import polyglot.ext.jl5.types.JL5MethodInstance_c;
import polyglot.ext.ml5.ast.AtomicBlock;
import polyglot.ext.ml5.ast.AtomicBlock_c;
import polyglot.ext.ml5.util.PolyglotUtil;
import polyglot.ext.ml5.visit.context.AtomicFieldContext;
import polyglot.ext.ml5.visit.context.EnumContext;
import polyglot.ext.ml5.visit.context.FieldContext;
import polyglot.frontend.Job;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.NodeVisitor;

public class AtomicFieldFinderVisitor extends MethodTrackerVisitor {

	private AtomicFieldContext atomicContext;
	private FieldContext fieldContext;
	private EnumContext enumContext;

	public AtomicFieldFinderVisitor(EnumContext enumContext, FieldContext fieldContext,
			AtomicFieldContext context, Job job, 
			TypeSystem ts, NodeFactory nf) {
		super(context, job, ts, nf);
		this.enumContext = enumContext;
		this.atomicContext = context;
		this.fieldContext = fieldContext;
		
	}

	public NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
		
	
		if (n instanceof AtomicBlock) {
			((AtomicBlock_c)n).setLevel(atomicContext.getAtomicLevel());
			atomicContext.enterAtomicScope();
		}

		if (atomicContext.inAtomicScope()) {
		
			/*if (n instanceof JL5Field && PolyglotUtil.hasFieldChildren(n) && !enumContext.isFieldEnum((JL5Field)n)) {
				if (!((JL5Field)n).flags().isFinal()) { 
					atomicContext.addField((JL5Field) n);
				}
				else {
					if (((JL5Field)n).type().isArray()) {
						atomicContext.addField((JL5Field) n);
					}
				}
			} else if (n instanceof JL5Field && !(parent instanceof JL5Field)
					&& !(parent instanceof JL5Call) && !enumContext.isFieldEnum((JL5Field)n)) {
				if (!((JL5Field)n).flags().isFinal()) {
					atomicContext.addField((JL5Field) n);
				}
				else {
					if (((JL5Field)n).type().isArray()) {
						atomicContext.addField((JL5Field) n);
					}
				}
			}*/
			if (parent instanceof FieldAssign) {
				FieldAssign fa = (FieldAssign)parent;
				if (fa.left() instanceof JL5Field) {
					//System.out.println("Found AtomicField -> "+PolyglotUtil.fieldSignature((JL5Field)fa.left()));
					atomicContext.addField((JL5Field)fa.left());	
				}
			}
			else if (parent instanceof ArrayAccessAssign) {
				ArrayAccessAssign aaa = (ArrayAccessAssign)parent;
				ArrayAccess arr = (ArrayAccess)aaa.left();
				while(arr.array() instanceof ArrayAccess) { arr = (ArrayAccess)arr.array(); }
				if (arr.array() instanceof JL5Field) {
					atomicContext.addField((JL5Field)arr.array());
				}
			}
			else if (n instanceof JL5Call) {
				JL5Call c = (JL5Call) n;
				if (c.methodInstance().flags().isAbstract()) {
					if (c.target() instanceof JL5Field) {
						JL5CanonicalTypeNode type = fieldContext
								.getFieldInstType((JL5Field) c.target());
						if (type != null) {

							JL5MethodInstance_c jmi = (JL5MethodInstance_c) c
									.methodInstance().copy();
							jmi = (JL5MethodInstance_c) jmi.flags(jmi.flags()
									.clearAbstract());
							jmi = (JL5MethodInstance_c) jmi
									.container((ReferenceType) type.type());

							JL5Call cll = (JL5Call) c.copy();
							cll = (JL5Call) cll.methodInstance(jmi);

							c = cll;
						}
					}
				}
				if (atomicContext.addCall(c)) {
					if (methodContainer.contains(c)) {
						inspectMethod(methodContainer.getMethod(c));
					}
				}
			}
		}
		else if (n instanceof JL5MethodDecl) {
			inspectMethod((JL5MethodDecl) n);
		}

		return super.enterCall(parent, n);
	}

	private void inspectMethod(JL5MethodDecl n) {
		if (atomicContext.hasMethodToResolve(n)) {

			atomicContext.inspectMethod(n);
			n.visitChildren(new NodeVisitor() {
				public Node override(Node parent, Node n) {

					if (n instanceof AtomicBlock) {
						atomicContext.enterAtomicScope();
					}

					/*if (n instanceof JL5Field
							&& PolyglotUtil.hasFieldChildren(n) && !enumContext.isFieldEnum((JL5Field)n)) {
						if (!((JL5Field)n).flags().isFinal()) {
							atomicContext.addField((JL5Field) n);
						}
						else {
							if (((JL5Field)n).type().isArray()) {
								atomicContext.addField((JL5Field) n);
							}
						}
					} else if (n instanceof JL5Field
							&& !(parent instanceof JL5Field)
							&& !(parent instanceof JL5Call) && !enumContext.isFieldEnum((JL5Field)n)) {
						if (!((JL5Field)n).flags().isFinal()) {
							atomicContext.addField((JL5Field) n);
						}
						else {
							if (((JL5Field)n).type().isArray()) {
								atomicContext.addField((JL5Field) n);
							}
						}
					}*/
					if (parent instanceof FieldAssign) {
						FieldAssign fa = (FieldAssign)parent;
						if (fa.left() instanceof JL5Field) {
							//System.out.println("Found AtomicField -> "+PolyglotUtil.fieldSignature((JL5Field)fa.left()));
							atomicContext.addField((JL5Field)fa.left());	
						}
					}
					else if (parent instanceof ArrayAccessAssign) {
						ArrayAccessAssign aaa = (ArrayAccessAssign)parent;
						ArrayAccess arr = (ArrayAccess)aaa.left();
						while(arr.array() instanceof ArrayAccess) { arr = (ArrayAccess)arr.array(); }
						if (arr.array() instanceof JL5Field) {
							atomicContext.addField((JL5Field)arr.array());
						}
					}
					if (n instanceof JL5Call) {
						JL5Call c = (JL5Call) n;
						if (c.methodInstance().flags().isAbstract()) {
							if (c.target() instanceof JL5Field) {
								JL5CanonicalTypeNode type = fieldContext
										.getFieldInstType((JL5Field) c.target());
								if (type != null) {
									JL5MethodInstance_c jmi = (JL5MethodInstance_c) c
											.methodInstance().copy();
									jmi = (JL5MethodInstance_c) jmi.flags(jmi
											.flags().clearAbstract());
									jmi = (JL5MethodInstance_c) jmi
											.container((ReferenceType) type
													.type());

									JL5Call cll = (JL5Call) c.copy();
									cll = (JL5Call) cll.methodInstance(jmi);

									c = cll;
								}
							}
						}
						if (atomicContext.addCall(c)) {
							if (methodContainer.contains(c)) {
								inspectMethod(methodContainer.getMethod(c));
							}
						}

					}

					return super.override(parent, n);
				}
			});
		}
	}

	public Node leaveCall(Node old, Node n, NodeVisitor v)
			throws SemanticException {
	
		if (n instanceof AtomicBlock) {
			atomicContext.exitAtomicScope();
		}
		return super.leaveCall(old, n, v);
	}


}
