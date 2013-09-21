package polyglot.ext.ml5.ast;

import polyglot.ast.ArrayAccess;
import polyglot.ext.jl.ast.ArrayAccessAssign_c;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;

public class AtomicNewArrayAssign_c extends ArrayAccessAssign_c {

	public AtomicNewArrayAssign_c(Position pos, ArrayAccess left, Operator op,
			AtomicNewArray_c right) {
		super(pos, left, op, right);
		// TODO Auto-generated constructor stub
	}
	
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("{");
		super.prettyPrint(w, tr);
		w.write(";");
		w.newline();
		w.write(((AtomicNewArray_c)right).getArrayInit());
		w.write("}");
	}

}
