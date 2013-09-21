package polyglot.ext.ml5.ast;

import java.util.List;

import polyglot.ast.Receiver;
import polyglot.ext.jl5.ast.JL5Call_c;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;

public class AtomicCall_c extends JL5Call_c {

	public AtomicCall_c(Position pos, Receiver target, String name, List arguments, List typeArguments) {
		super(pos, target, name, arguments, typeArguments);
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		if (arguments.size() == 0) {
			super.prettyPrint(w, tr);
		}
		else {
			if (arguments.get(0) instanceof AtomicNewArray_c) {
				w.write("{");
				super.prettyPrint(w, tr);
				w.write(";");
				w.newline();
				w.write(((AtomicNewArray_c)arguments.get(0)).getArrayInit());
				w.write("}");
			}
			else {
				super.prettyPrint(w, tr);
			}
		}
	}
	
}
