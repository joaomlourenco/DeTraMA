package polyglot.ext.ml5.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.ArrayInit;
import polyglot.ast.Expr;
import polyglot.ext.jl.ast.ArrayInit_c;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;

public class AtomicArrayInit_c extends ArrayInit_c {

	public AtomicArrayInit_c(Position pos, List elements) {
		super(pos, elements);
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("{ ");

		for (Iterator i = elements.iterator(); i.hasNext();) {
			Expr e = (Expr) i.next();
			
			printElement(e, w, tr);

			if (i.hasNext()) {
				w.write(", ");
			}
		}

		w.write(" }");
	}
	
	public void printElement(Expr e, CodeWriter w, PrettyPrinter tr) {
		if (e instanceof ArrayInit) {
			ArrayInit ai = (ArrayInit)e;
			w.write("{ ");
			for (Iterator i = ai.elements().iterator(); i.hasNext();) {
				printElement((Expr)i.next(),w, tr);
				if (i.hasNext()) {
					w.write(", ");
				}
			}
			w.write(" }");
		}
		else {
			w.write(polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".TObjectFactory.createITObject(");
			print(e, w, tr);
			w.write(")");
		}
	}

}
