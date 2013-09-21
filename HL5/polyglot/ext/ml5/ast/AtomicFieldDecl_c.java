package polyglot.ext.ml5.ast;

import java.util.ArrayList;

import polyglot.ast.ArrayInit;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.ArrayInit_c;
import polyglot.ext.jl5.ast.JL5FieldDecl_c;
import polyglot.ext.jl5.ast.JL5NewArray_c;
import polyglot.ext.jl5.types.FlagAnnotations;
import polyglot.ext.jl5.types.JL5ArrayType;
import polyglot.types.Flags;
import polyglot.types.PrimitiveType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;

public class AtomicFieldDecl_c extends JL5FieldDecl_c {

	private TypeSystem ts;
	String arrayType = null;
	
	public AtomicFieldDecl_c(Position pos, FlagAnnotations flags, TypeNode type, String name,
			Expr init, TypeSystem ts) {
		super(pos, flags, type, name, init);
		this.ts = ts;
	}
	
	public void print(TypeNode type, CodeWriter w, PrettyPrinter tr) {
		if (type.type().isPrimitive()) {
			w.write(((PrimitiveType)type.type()).wrapperTypeString(ts));
			if (arrayType != null) {arrayType = arrayType + ((PrimitiveType)type.type()).wrapperTypeString(ts);}
        }
		else if (type.type().isArray()) {
			print(type.type(), w, tr);
		}
        else {
        	print((Node)type, w, tr);
        }
	}
	
	public void print(Type type, CodeWriter w, PrettyPrinter tr) {
		if (type.isPrimitive()) {
			w.write(((PrimitiveType)type).wrapperTypeString(ts));
			if (arrayType != null) {arrayType = arrayType + ((PrimitiveType)type).wrapperTypeString(ts);}
        }
		else if (type.isArray()) {
			JL5ArrayType t = (JL5ArrayType)type;
			if (t.base().isPrimitive()) {
				w.write(((PrimitiveType)t.base()).wrapperTypeString(ts));
				if (arrayType != null) {arrayType = arrayType + ((PrimitiveType)t.base()).wrapperTypeString(ts);}
			}
			else if (t.base().isArray()) {
				print(t.base(), w, tr);
			}
			else {
				w.write(t.base().toString());
				if (arrayType != null) {arrayType = arrayType + t.base().toString();}
			}
		}
	}
	
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        boolean isInterface = fi != null && fi.container() != null &&
                              fi.container().toClass().flags().isInterface();

        Flags f = flags;

        if (isInterface) {
            f = f.clearPublic();
            f = f.clearStatic();
            f = f.clearFinal();
        }

        w.write(f.translate());
        w.write(polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".ITObject<");
        
        if (type.type().isArray()) {
        	arrayType = new String(polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".ITObject<");
        	w.write(polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".ITObject<");
        }
      
        print(type, w, tr);
        
        if (type.type().isArray()) {
        	w.write(">");
        	arrayType = arrayType + ">";
        	for (int i=0; i < ((JL5ArrayType)type.type()).dims(); i++) {
        		w.write("[]");	
        		arrayType = arrayType + "[]";
        	}
        }
        
        w.write(">");
        
//        if (type.type().isArray()) {
//        	for (int i=0; i < ((JL5ArrayType)type.type()).dims(); i++) {
//        		w.write("[]");	
//        	}
//        }
        
        w.allowBreak(2, 2, " ", 1);
        w.write(name);

        if (init != null) {
        	/*if (init instanceof JL5NewArray_c) {
        		init = new AtomicNewArray_c(name, ts, init.position(), ((JL5NewArray_c) init).baseType(), ((JL5NewArray_c) init).dims(),
        				((JL5NewArray_c) init).additionalDims(), ((JL5NewArray_c) init).init());
        		w.write(" = ");
        		print(init, w, tr);
        		w.write(";");
        		w.newline();
        		
        	}
        	else if (init instanceof ArrayInit_c) {
        		
        		init = new AtomicArrayInit_c(init.position(), ((ArrayInit_c)init).elements());
        		
        		init = new AtomicNewArray_c(name, ts, init.position(), null, new ArrayList(),
        				((AtomicArrayInit_c)init).elements().size(), (ArrayInit)init);
        		
        		w.write(" = ");
        		print(init, w, tr);
        		w.write(";");
        		w.newline();
        	}
        	else {*/
        	
        	
        	if (init instanceof JL5NewArray_c) {
        		init = new AtomicNewArray_c(name+".get()", ts, init.position(), ((JL5NewArray_c) init).baseType(), ((JL5NewArray_c) init).dims(),
        				((JL5NewArray_c) init).additionalDims(), ((JL5NewArray_c) init).init());
        	//	w.write(" = ");
        	//	print(init, w, tr);
        	//	w.write(";");
        	//	w.newline();
        		
        	}
        	else if (init instanceof ArrayInit_c) {
        		
        		init = new AtomicArrayInit_c(init.position(), ((ArrayInit_c)init).elements());
        		
        		int asize = 1;
        		ArrayInit_c ai = (ArrayInit_c)init;
        		while(ai.elements().size() > 0 && ai.elements().get(0) instanceof ArrayInit_c) {
        			ai = (ArrayInit_c)ai.elements().get(0);
        			asize++;
        		}
        		
        		init = new AtomicNewArray_c(name+".get()", ts, init.position(), null, new ArrayList(),
        				/*((AtomicArrayInit_c)init).elements().size(),*/
        				asize, (ArrayInit)init);
        		
        	//	w.write(" = ");
        	//	print(init, w, tr);
        	//	w.write(";");
        	//	w.newline();
        	}
        	
        		w.write(" = "+polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".TObjectFactory.createITObject(");
        		w.allowBreak(2, "");
        		if (arrayType != null) {
        			w.write("("+arrayType+")");
        		}
        		print(init, w, tr);
        		w.write(")");
        		w.write(";");
        		
        		if (init instanceof AtomicNewArray_c) {
        			w.newline();
        			w.write(((AtomicNewArray_c)init).getArrayInit());
        		}
        	//}
        }
        else {
        	//if (!type.type().isArray()) {
        		w.write(" = "+polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".TObjectFactory.createITObject(null)");
        	//}
        	w.write(";");
        }

        
    }

}
