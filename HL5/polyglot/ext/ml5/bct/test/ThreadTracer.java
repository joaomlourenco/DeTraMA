package polyglot.ext.ml5.bct.test;

import polyglot.visit.NodeVisitor;

public class ThreadTracer extends NodeVisitor {

	protected MethodListContext context;
	
	public ThreadTracer (MethodListContext context) {
		this.context = context;
	}
	
	@Override
	public NodeVisitor begin() {

		
		
		return super.begin();
	}
	
	/*
	@Override
	public NodeVisitor enter(Node n) {
		if (Utils.isStart(n)) {
			JL5MethodDecl decl = (JL5MethodDecl) n;
			String sig = PolyglotUtil.methodSignature(decl);
			System.out.println("\n\nSTART: " + sig + " (" + n.position() + ")");
			trace(sig, 0);
		}
		return super.enter(n);
	}
	
	
	public int trace(String method, int level) {
		int l = level;
		MyNode node = lister.map.get(method);
		if (node != null) { //if null it means we haven't parsed it, belongs to JRE or other 3rd party
			for (AccessEntry entry : me.list) {
				String str = entry.str;
				Node n = entry.node;
				if ((str.startsWith("WRITE") || str.startsWith("READ"))) {
					System.out.println("\t" + str + " (" + n.position() + ")");
				} else if (str.startsWith("CALL")) {
					String sig = str.substring(5, str.length()-1);
					System.out.println("--->"+sig);
					trace(sig, l);
				} else if (str.startsWith("ATOM ENTER")) {
					if (l == 0) {
						System.out.println("[ATOM] (" + n.position() + ")");
					}
					l++;
				} else if (str.startsWith("ATOM LEAVE")) {
					l--;
				}
			}
		}
		return l;
	}
	*/
}
