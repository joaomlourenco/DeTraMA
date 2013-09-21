package polyglot.ext.ml5.bct.test;

import polyglot.ast.ArrayAccess;
import polyglot.ast.ArrayAccessAssign;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Node;
import polyglot.ext.jl5.ast.JL5Call;
import polyglot.ext.jl5.ast.JL5Field;
import polyglot.ext.jl5.ast.JL5FieldDecl;
import polyglot.ext.jl5.ast.JL5MethodDecl;
import polyglot.ext.ml5.util.PolyglotUtil;
import polyglot.types.Flags;

public class Utils {
	private static String getWrite(Node n) {
		if (n instanceof FieldAssign) {
			FieldAssign fa = (FieldAssign) n;
			return "WRITE:" + fa.left();
		} else if (n instanceof ArrayAccessAssign) {
			ArrayAccessAssign aaa = (ArrayAccessAssign) n;
			return "WRITE:" + aaa.left();
		}
		return null;
	}

	private static String getRead(Node n) {
		if (n instanceof Field) {
			Field f = (Field) n;
			return "READ:" + f.name();
		} else if (n instanceof ArrayAccess) {
			ArrayAccess aa = (ArrayAccess) n;
			return "READ:" + aa.toString();
		}
		return null;
	}

	public static String getCall(Node n) {
		if (n instanceof JL5Call) {
			JL5Call call = (JL5Call) n;
			return "CALL:" + PolyglotUtil.methodSignature(call);
		}
		return null;
	}

	public static boolean isStart(Node n) {
		if (n instanceof JL5MethodDecl) {
			JL5MethodDecl md = (JL5MethodDecl) n;
			Flags f = md.flags();
			if (f.isPublic() && f.isStatic()
					&& md.returnType().toString().equals("void")
					&& md.name().equals("main")) { // &&
													// md.formals().contains...
				return true;
			} else if (f.isPublic()
					&& md.returnType().toString().equals("void")
					&& md.name().equals("run")) { // && check class name...
				return true;
			}
		}
		return false;
	}
	
	public static String fieldSignature(JL5Field node) {
		return "" + node.fieldInstance().container() + "." + node.name();
		//return node.fieldInstance()+":"+node.fieldInstance().container();
	}
	
	public static String fieldSignature(JL5FieldDecl node) {
		return "" + node.fieldInstance().container() + "." + node.name();
		//return node.fieldInstance()+":"+node.fieldInstance().container();
	}
}
