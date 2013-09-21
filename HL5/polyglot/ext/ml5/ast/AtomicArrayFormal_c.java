package polyglot.ext.ml5.ast;

import java.util.Iterator;

import polyglot.ast.TypeNode;
import polyglot.ext.jl5.ast.AnnotationElem;
import polyglot.ext.jl5.ast.JL5Formal_c;
import polyglot.ext.jl5.types.FlagAnnotations;
import polyglot.ext.jl5.types.JL5ArrayType;
import polyglot.types.ArrayType;
import polyglot.types.PrimitiveType;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;

public class AtomicArrayFormal_c extends JL5Formal_c {

	public AtomicArrayFormal_c(Position pos, FlagAnnotations flags, TypeNode type,
			String name) {
		super(pos, flags, type, name);
	}

	public AtomicArrayFormal_c(Position pos, FlagAnnotations flags, TypeNode type,
			String name, boolean variable) {
		super(pos, flags, type, name, variable);
	}
	
	public AtomicArrayFormal_c(JL5Formal_c formal) {
		this(formal.position(), new FlagAnnotations(formal.flags(), formal.annotations()), 
				formal.type(), formal.name(), formal.isVariable());
		this.li = formal.localInstance();
	}
	
	public void prettyPrint(CodeWriter w, PrettyPrinter tr){
        if (annotations != null){
            for (Iterator it = annotations.iterator(); it.hasNext(); ){
                print((AnnotationElem)it.next(), w, tr);
            }
        }
        w.write(flags.translate());
        if (isVariable()){
            w.write(((ArrayType)type.type()).base().toString());
            w.write(" ...");
        }
        else {
            //print(type, w, tr);
        	w.write(polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".ITObject<");
        	print(type.type(), w, tr);
        	w.write(">");
        	for (int i=0; i < ((JL5ArrayType)type.type()).dims(); i++) {
        		w.write("[]");	
        	}
        }
        w.write(" ");
        w.write(name);
        
    }
	
	private void print(Type type, CodeWriter w, PrettyPrinter tr) {
		if (type.isPrimitive()) {
			w.write(((PrimitiveType)type).wrapperTypeString(this.type().type().typeSystem()));
        }
		else if (type.isArray()) {
			JL5ArrayType t = (JL5ArrayType)type;
			if (t.base().isPrimitive()) {
				w.write(((PrimitiveType)t.base()).wrapperTypeString(this.type().type().typeSystem()));
			}
			else if (t.base().isArray()) {
				print(t.base(), w, tr);
			}
			else {
				w.write(t.base().toString());
			}
		}
	}

}
