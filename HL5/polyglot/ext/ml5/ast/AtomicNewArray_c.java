package polyglot.ext.ml5.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.ArrayInit;
import polyglot.ast.Expr;
import polyglot.ast.Precedence;
import polyglot.ast.TypeNode;
import polyglot.ext.jl5.ast.JL5NewArray_c;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;

public class AtomicNewArray_c extends JL5NewArray_c {
	private TypeSystem ts;
	private String name;
	
	private String arrayInit;
	
	public AtomicNewArray_c(String name, TypeSystem ts, Position pos, TypeNode baseType, List dims, int addDims,
			ArrayInit init) {
		super(pos, baseType, dims, addDims, init);
		this.ts = ts;
		this.name = name;
		this.arrayInit = null;
	}
	
	/** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
      w.write("new ");
      
      w.write(polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".ITObject");

      for (Iterator i = dims.iterator(); i.hasNext();) {
        Expr e = (Expr) i.next();
	      w.write("[");
	      printBlock(e, w, tr);
	      w.write("]");
      }

      for (int i = 0; i < addDims; i++) {
        w.write("[]");
      }

      if (init != null) {
    	
    	  init = new AtomicArrayInit_c(init.position(), init.elements());
    	  
        w.write(" ");
        
        print(init, w, tr);
        
        
      } 
      else {
    	  if (addDims < 1) {
    		  //w.write(";");
    		  printInit(w, tr);
    	  }
      }
      
    }
    
    private Precedence precd = Precedence.UNKNOWN;
    
    public void setPrecedence(Precedence p) {
    	precd = p;
    }
    
    public Precedence precedence() {
    	return precd;
    }
    
    protected void printInit(CodeWriter w, PrettyPrinter tr) {
    	StringBuffer sb = new StringBuffer();
    	if (init == null) {
    		//w.newline();
    		//w.write("{");
    		sb.append("{");
    		
    		if (addDims < 1) {
    			
    			/*if (dims.size() == 1) {
    				w.write("for (int i=0; i < "+dims.get(0)+"; i++) {");
    				w.write(name+"[i] = "+polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".TObjectFactory.createITObject(null);");
    				w.write("} ");
    			}
    			else {*/
    				for (int i=0; i < dims.size(); i++) {
    					//w.write("for (int i"+i+"=0; i"+i+" < "+dims.get(i)+"; i"+i+"++) {");
    					sb.append("for (int i"+i+"=0; i"+i+" < "+dims.get(i)+"; i"+i+"++) {");
    				}
    				//w.write(name);
    				sb.append(name);
    				for (int i=0; i < dims.size(); i++) {
    					//w.write("[i"+i+"]");
    					sb.append("[i"+i+"]");
    				}
    				//w.write(" = "+polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".TObjectFactory.createITObject(null);");
    				sb.append(" = "+polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".TObjectFactory.createITObject(null);");
    				for (int i=0; i < dims.size(); i++) {
    					//w.write("} ");
    					sb.append("} ");
    				}
    			//}
    		}
    		else {
    			//w.write("for (int i=0; i < "+dims.get(0)+"; i++) {");
    			sb.append("for (int i=0; i < "+dims.get(0)+"; i++) {");
    			//w.write(name+"[i] = new "+polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".ITObject");
    			sb.append(name+"[i] = new "+polyglot.ext.ml5.global.GlobalSettings.RUNTIME_PACKAGE+".ITObject");
    			for (int i=0; i < addDims; i++) {
    		        //w.write("[]");
    				sb.append("[]");
    		    }
    			//w.write(";");
    			sb.append(";");
    			//w.write("} ");
    			sb.append("} ");
    		}
    		//w.write("} ");
    		sb.append("}");
    		arrayInit = sb.toString();
    	}
    }
    
    public String getArrayInit() {
    	return arrayInit == null ? "" : arrayInit;
    }

}
