package polyglot.ext.ml5.visit;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.IntLit;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Unary;
import polyglot.ast.Binary.Operator;
import polyglot.ext.jl.ast.Binary_c;
import polyglot.ext.jl.ast.FieldAssign_c;
import polyglot.ext.jl.ast.IntLit_c;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

public class UnaryOperatorExpanderVisitor extends AscriptionVisitor {

	public UnaryOperatorExpanderVisitor(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
	}

	public NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
		// System.out.println("ENTER -> "+n+" -> "+n.getClass());
		return super.enterCall(parent, n);
	}

	public Node leaveCall(Node old, Node n, NodeVisitor v)
			throws SemanticException {
		// System.out.println("LEAVE -> "+n+" -> "+n.getClass());

		TypeChecker tc = new TypeChecker(job, ts, nf);
		if (n instanceof Unary /*
								 * && (((Unary)n).operator() == Unary.PRE_INC ||
								 * ((Unary)n).operator() == Unary.PRE_DEC)
								 */) {
			if (((Unary) n).expr() instanceof Field) {
				Node res = null;

				Operator op = null;
				if (((Unary) n).operator() == Unary.PRE_INC) {
					op = Binary.ADD;
				} else if (((Unary) n).operator() == Unary.PRE_DEC) {
					op = Binary.SUB;
				} else {
					return n;
				}

				IntLit_c t = new IntLit_c(n.position(), IntLit.INT, 1);
				t = (IntLit_c) t.typeCheck(tc);
				Expr right = new Binary_c(n.position(), ((Unary) n).expr(), op,
						(Expr) t);
				right = (Expr) right.typeCheck(tc);

				res = new FieldAssign_c(n.position(), (Field) ((Unary) n)
						.expr(), FieldAssign.ASSIGN, right);

				res = res.typeCheck(tc);
				// System.out.println("RES: "+res+" -> "+((Expr)res).type());
				return res;
			}
		} else if (n instanceof FieldAssign) {
			FieldAssign fa = (FieldAssign) n;
			Node res = null;
			// if (fa.operator() == FieldAssign.ADD_ASSIGN) {
			//				
			// }

			Operator op = null;
			if (fa.operator() == FieldAssign.ADD_ASSIGN) {
				op = Binary.ADD;
			} else if (fa.operator() == FieldAssign.SUB_ASSIGN) {
				op = Binary.SUB;
			} else if (fa.operator() == FieldAssign.MUL_ASSIGN) {
				op = Binary.MUL;
			} else if (fa.operator() == FieldAssign.DIV_ASSIGN) {
				op = Binary.DIV;
			} else if (fa.operator() == FieldAssign.MOD_ASSIGN) {
				op = Binary.MOD;
			} else if (fa.operator() == FieldAssign.SHL_ASSIGN) {
				op = Binary.SHL;
			} else if (fa.operator() == FieldAssign.SHR_ASSIGN) {
				op = Binary.SHR;
			} else if (fa.operator() == FieldAssign.USHR_ASSIGN) {
				op = Binary.USHR;
			} else if (fa.operator() == FieldAssign.BIT_AND_ASSIGN) {
				op = Binary.BIT_AND;
			} else if (fa.operator() == FieldAssign.BIT_OR_ASSIGN) {
				op = Binary.BIT_OR;
			} else if (fa.operator() == FieldAssign.BIT_XOR_ASSIGN) {
				op = Binary.BIT_XOR;
			} else {
				return n;
			}

			Expr right = new Binary_c(n.position(), ((FieldAssign) n).left(),
					op, ((FieldAssign) n).right());
			right = (Expr) right.typeCheck(tc);
			res = new FieldAssign_c(n.position(), (Field) ((FieldAssign) n)
					.left(), FieldAssign.ASSIGN, right);
			res = res.typeCheck(tc);
			return res;

		}
		return super.leaveCall(old, n, v);
	}

}
