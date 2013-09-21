package polyglot.ext.ml5.bct.test;

import java.util.Stack;

import polyglot.ast.ArrayAccess;
import polyglot.ast.ArrayAccessAssign;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.If;
import polyglot.ast.Node;
import polyglot.ext.jl5.ast.JL5MethodDecl;
import polyglot.ext.ml5.ast.AtomicBlock;
import polyglot.ext.ml5.bct.tree.MyNode;
import polyglot.ext.ml5.bct.tree.NodeAccessSingleRead;
import polyglot.ext.ml5.bct.tree.NodeAccessSingleWrite;
import polyglot.ext.ml5.bct.tree.NodeAtom;
import polyglot.ext.ml5.bct.tree.NodeCall;
import polyglot.ext.ml5.bct.tree.NodeIf;
import polyglot.ext.ml5.bct.tree.NodeSequence;
import polyglot.ext.ml5.util.PolyglotUtil;
import polyglot.visit.HaltingVisitor;
import polyglot.visit.NodeVisitor;

public class MethodLister extends HaltingVisitor {

	protected Stack<MyNode> stack;
	protected NodeSequence currentScope;
	protected MethodListContext context;
	
	public MethodLister(MethodListContext context) {
		this.context = context;
	}
	
	@Override
	public synchronized NodeVisitor begin() {
		currentScope = null;
		stack = new Stack<MyNode>();
		return super.begin();
	}
	
	@Override
	public NodeVisitor enter(Node parent, Node n) {
		if (currentScope == null) { // not currently inside a method, so we only care if new node is method declaration
			if (n instanceof JL5MethodDecl) {
				currentScope = new NodeSequence((Node)n.copy());
			} // else, class body or other, we don't care
		} else { // current != null
			// IF (MUST COME FIRST BECAUSE IT USES PARENT)
			if (parent instanceof If && n == ((If)parent).consequent()) { // this is the "then" node of an "if" node, the condition evaluation should have been processed and integrated in the previous scope
				NodeIf nif = new NodeIf((Node)parent.copy());
				currentScope.append(nif);
				NodeSequence consequent = new NodeSequence((Node)n.copy());
				nif.branchTrue = consequent;
				stack.push(currentScope);
				stack.push(nif);
				currentScope = consequent;
			}
			if (parent instanceof If && n == ((If)parent).alternative()) { // this is the "else" node, we assume the current scope is the "then" node, otherwise something is very wrong
				NodeIf nif = (NodeIf) stack.peek();
				NodeSequence alternative = new NodeSequence((Node)n.copy());
				nif.branchFalse = alternative;
				currentScope = alternative;
			}
			// WRITE VAR
			if (n instanceof FieldAssign) {
				FieldAssign fa = (FieldAssign) n;
				Field target = (Field) fa.left();
				NodeAccessSingleWrite write = new NodeAccessSingleWrite((Node)n.copy(), (Field) target.copy());
				currentScope.append(write);
				return this.bypass(target);
			}
			// WRITE ARRAY
			if (n instanceof ArrayAccessAssign) {
				ArrayAccessAssign aaa = (ArrayAccessAssign) n;
				ArrayAccess target = (ArrayAccess) aaa.left();
				Expr array = target.array();
				if (array instanceof Field) {
					Field field = (Field) target.array();
					NodeAccessSingleWrite write = new NodeAccessSingleWrite((Node)n.copy(), (Field)field.copy());
					currentScope.append(write);
				}
				return this.bypass(target);
			}
			// READ
			//if (Utils.getRead(n) != null) {
			if (n instanceof Field) {
				Field field = (Field) n.copy();
				NodeAccessSingleRead nar = new NodeAccessSingleRead(field, field);
				currentScope.append(nar);
			}
			// CALL
			if (Utils.getCall(n) != null) {
				NodeCall nc = new NodeCall((Node)n.copy());
				currentScope.append(nc);
			}
			// ATOMIC
			if (n instanceof AtomicBlock) {
				Node node = (Node) n.copy();
				NodeSequence seq = new NodeSequence(node);
				NodeAtom atom = new NodeAtom(node, seq);
				currentScope.append(atom);
				stack.push(currentScope);
				currentScope = seq;
			}
			
			//TODO:
			// SWITCH
			// LOOP
			// EXCEPTION
			// RETURN
		}
		return super.enter(n);
	}
	
	@Override
	public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
		if (currentScope != null) {
			if (n instanceof JL5MethodDecl) {
				//if (currentScope.getNode() != n)
					//throw new RuntimeException("Something went wrong while traversing the AST");
				JL5MethodDecl md = (JL5MethodDecl) n;
				//map.put(PolyglotUtil.methodSignature(md), currentScope);
				String sig = PolyglotUtil.methodSignature(md);
				context.put(sig, currentScope);
				currentScope = null;
				if (!stack.empty())
					throw new RuntimeException("Something went wrong while keeping track of scopes");
			}
			if (n instanceof AtomicBlock) {
				//if (currentScope.getNode() != n)
					//throw new RuntimeException("Something went wrong while traversing the AST");
				currentScope = (NodeSequence) stack.pop();
			}
			if (parent instanceof If && n == ((If)parent).consequent()) {
				//if (currentScope.getNode() != n)
					//throw new RuntimeException("Something went wrong while traversing the AST");
				//
			}
			if (parent instanceof If && n == ((If)parent).alternative()) {
				//if (currentScope.getNode() != n)
					//throw new RuntimeException("Something went wrong while traversing the AST");
				//
			}
			if (n instanceof If) {
				//MyNode nif = stack.pop();
				stack.pop();
				//if (nif.getNode() != n)
					//throw new RuntimeException("Something went wrong while traversing the AST");
				currentScope = (NodeSequence) stack.pop();
			}
		}
		return super.leave(old, n, v);
	}
	
	@Override
	public void finish() {
		
		super.finish();
	}
	
}
