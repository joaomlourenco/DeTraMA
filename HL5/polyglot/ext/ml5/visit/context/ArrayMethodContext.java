package polyglot.ext.ml5.visit.context;


import java.util.HashMap;

import polyglot.ext.jl5.ast.JL5Call;
import polyglot.ext.jl5.ast.JL5ClassBody_c;
import polyglot.ext.jl5.ast.JL5MethodDecl;
import polyglot.ext.ml5.util.PolyglotUtil;
import polyglot.ext.ml5.util.Stack;

public class ArrayMethodContext {
	protected HashMap<String,JL5Call> methods;
	
	public class ClassBodyStack {
		public JL5ClassBody_c classBody = null;
		public JL5ClassBody_c origBody = null;
		
		public ClassBodyStack(JL5ClassBody_c classBody, JL5ClassBody_c origBody) {
			this.classBody = classBody;
			this.origBody = origBody;
		}
	}
	
	private Stack<ClassBodyStack> body;
	
	public ArrayMethodContext() {
		methods = new HashMap<String,JL5Call>();
		body = new Stack<ClassBodyStack>();
	}
	
	public void addMethodCall(JL5Call call) {
		methods.put(PolyglotUtil.methodSignature(call), call);
		//System.out.println("ArrayMethodContext: added call -> "+call);
	}
	
	public boolean isArrayMethod(JL5MethodDecl md) {
		return methods.containsKey(PolyglotUtil.methodSignature(md));
	}
	
	public void addClassBody(ClassBodyStack cbs) {
		body = body.push();
		body.setValue(cbs);
	}
	
	public ClassBodyStack peekClassBody() {
		return body.getValue();
	}
	
	public ClassBodyStack getClassBody() {
		ClassBodyStack cbs = body.getValue();
		body = body.pop();
		return cbs;
	}
}
