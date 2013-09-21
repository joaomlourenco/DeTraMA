package polyglot.ext.ml5.visit.context;

import java.util.HashMap;

import polyglot.ext.jl5.ast.JL5Call;
import polyglot.ext.jl5.ast.JL5MethodDecl;
import polyglot.ext.ml5.util.PolyglotUtil;

public class MethodTracker extends AtomicContext {
	protected HashMap<String,JL5MethodDecl> methods;
	
	public MethodTracker() {
		methods = new HashMap<String,JL5MethodDecl>();
	}
	
	public void addMethod(JL5MethodDecl node) {
		methods.put(PolyglotUtil.methodSignature(node), node);
	}
	
	public JL5MethodDecl getMethod(JL5Call node) {
		return methods.get(PolyglotUtil.methodSignature(node));
	}
	
	public boolean contains(JL5MethodDecl node) {
		return methods.containsKey(PolyglotUtil.methodSignature(node));
	}
	
	public boolean contains(JL5Call node) {
		return methods.containsKey(PolyglotUtil.methodSignature(node));
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("[ ");
		for (String s : methods.keySet())
			sb.append(s+", ");
		sb.append("]");
		return sb.toString();
	}
}
