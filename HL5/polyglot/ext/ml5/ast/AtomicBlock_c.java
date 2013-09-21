package polyglot.ext.ml5.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Stmt;
import polyglot.ext.jl.ast.AbstractBlock_c;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;

public class AtomicBlock_c extends AbstractBlock_c implements AtomicBlock {

	private int level = 0;
	private static int atomic_counter = 0;
	private int id;
	
	public AtomicBlock_c(Position pos, List<Stmt> statements) {
		super(pos, statements);
		this.id = atomic_counter++;
	}
	
	public void setLevel(int l) {
		this.level = l;
	}
	
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		if (level == 0) { // to flat subtransactions
		w.write("boolean ___atomic_tx___done___"+level+id+" = false;");
		w.unifiedBreak(0, 1, " ", 1);
		w.write("while(!___atomic_tx___done___"+level+id+") {");
		
		w.unifiedBreak(4, 1, " ", 1);
		w.begin(0);
		w.write(polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".TransactionManager.begin();");
		w.newline();
		w.write("try {");
		w.unifiedBreak(4, 1, " ", 1);
		w.begin(0);
		}
		super.prettyPrint(w, tr);
		
		if (level == 0) {
		w.newline();
		w.write(polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".TransactionManager.commit();");
		w.newline();
		w.write("___atomic_tx___done___"+level+id+" = true;");
		w.end();
		w.unifiedBreak(0, 1, " ", 1);
		w.write("}");
		
		w.write("catch ("+polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".RollbackException e) {");
		w.unifiedBreak(4, 1, " ", 1);
		w.begin(0);
		w.write("___atomic_tx___done___"+level+id+" = false;");
		w.newline();
		w.write(polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".TransactionManager.clean();");
		w.end();
		w.unifiedBreak(0, 1, " ", 1);
		w.write("}");
		w.end();
		w.unifiedBreak(0, 1, " ", 1);
		w.write("}");
		}
	}

	public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("atomic {");

        int count = 0;

        for (Iterator i = statements.iterator(); i.hasNext(); ) {
            if (count++ > 2) {
                sb.append(" ...");
                break;
            }

            Stmt n = (Stmt) i.next();
            sb.append(" ");
            sb.append(n.toString());
        }

        sb.append(" }");
        return sb.toString();
    }
}
