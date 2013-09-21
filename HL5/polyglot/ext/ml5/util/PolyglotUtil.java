package polyglot.ext.ml5.util;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Field;
import polyglot.ast.Node;
import polyglot.ext.jl5.ast.EnumConstantDecl;
import polyglot.ext.jl5.ast.JL5Call;
import polyglot.ext.jl5.ast.JL5Field;
import polyglot.ext.jl5.ast.JL5FieldDecl;
import polyglot.ext.jl5.ast.JL5MethodDecl;
import polyglot.visit.NodeVisitor;

public class PolyglotUtil {
	public static List<Node> getNodeChildren(Node n) {
		final List<Node> children = new ArrayList<Node>();
		n.visitChildren(new NodeVisitor() {
		    public Node override(Node n) {
		        children.add(n);
		        return n;
		    }
		});
		return children;
	}
	
	public static boolean hasFieldChildren(Node n) {
		List<Node> children = getNodeChildren(n);
		for (Node child : children) {
			if (child instanceof Field)
				return true;
		}
		return false;
	}
	
	public static String methodSignature(JL5MethodDecl node) {
		return node.methodInstance().toString()+":"+node.methodInstance().container();
	}
	
	public static String methodSignature(JL5Call node) {
		return node.methodInstance().toString()+":"+node.methodInstance().container();
	}
	
	public static String fieldSignature(JL5Field node) {
		return node.fieldInstance()+":"+node.fieldInstance().container();
	}
	
	public static String fieldSignature(JL5FieldDecl node) {
		return node.fieldInstance()+":"+node.fieldInstance().container();
	}

	public static String enumSignature(EnumConstantDecl e) {
		return e.enumInstance().toString()+":"+e.enumInstance().container();
	}
}
