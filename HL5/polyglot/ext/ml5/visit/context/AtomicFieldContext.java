/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package polyglot.ext.ml5.visit.context;

import java.util.HashMap;
import java.util.Map;

import polyglot.ext.jl5.ast.JL5Call;
import polyglot.ext.jl5.ast.JL5Field;
import polyglot.ext.jl5.ast.JL5FieldDecl;
import polyglot.ext.jl5.ast.JL5MethodDecl;
import polyglot.ext.ml5.util.PolyglotUtil;


/**
 *
 * @author rjfd
 */
public class AtomicFieldContext extends MethodTracker {

    private HashMap<String,JL5Call> tempMethods;
    private HashMap<String,Object> inspectedMethods;
    private HashMap<String,JL5Field> fields;
    
    private class AtomicStack {
		public AtomicStack prev;
		public boolean atomic2;
		
		public AtomicStack(AtomicStack prev, boolean atomic) {
			this.prev = prev;
			this.atomic2 = atomic;
		}
		
		public AtomicStack beginScope() {
			return new AtomicStack(this, atomic2);
		}
		
		public AtomicStack endScope() {
			return prev;
		}
		
		public boolean inAtomic() {
			return atomic2;
		}
		
		public int getLevel() {
			return prev == null ? 0 : prev.getLevel()+1;
		}
	}
    
    private AtomicStack stack;
    
    public void enterAtomicScope() {
    	stack = stack.beginScope();
    	stack.atomic2 = true;
    }
    
    public void exitAtomicScope() {
    	stack.atomic2 = false;
    	stack = stack.endScope();
    }
    
    public boolean inAtomicScope() {
    	return stack.inAtomic();
    }
    
    public int getAtomicLevel() {
    	return stack.getLevel();
    }
    
    public AtomicFieldContext() {
    	super();
        tempMethods = new HashMap<String,JL5Call>();
        inspectedMethods = new HashMap<String,Object>();
        fields = new HashMap<String,JL5Field>();
        stack = new AtomicStack(null, false);
    }
    
    public void addField(JL5Field f) {
    	//System.out.println("Field -> "+f.fieldInstance());
        fields.put(PolyglotUtil.fieldSignature(f), f);
    }
    
    public boolean addCall(JL5Call c) {
    	if (!inspectedMethods.containsKey(PolyglotUtil.methodSignature(c))) {
    		//System.out.println("Call -> "+c.methodInstance());
    		tempMethods.put(PolyglotUtil.methodSignature(c), c);
    		return true;
    	}
    	return false;
    }
    
    public boolean hasMethodToResolve(JL5MethodDecl method) {
    	//return tempMethods.containsKey(method.memberInstance().toString());
    	return tempMethods.containsKey(PolyglotUtil.methodSignature(method));
    }
    
    public void inspectMethod(JL5MethodDecl method) {
    	//tempMethods.remove(method.memberInstance().toString());
    	tempMethods.remove(PolyglotUtil.methodSignature(method));
    	inspectedMethods.put(PolyglotUtil.methodSignature(method), new Object());
    }
    
    public boolean containsField(JL5FieldDecl n) {
    	return fields.containsKey(PolyglotUtil.fieldSignature(n));
    }
    
    public boolean containsField(JL5Field n) {
    	return fields.containsKey(PolyglotUtil.fieldSignature(n));
    }
    
    public boolean isComplete() {
        for (Map.Entry<String,JL5Call> e : tempMethods.entrySet()) {
        	if (this.contains(e.getValue()))
        		return false;
        }
      
        return true;
    }
    
    public void printTempMethods() {
    	StringBuffer sb = new StringBuffer("[ ");
    	for (Map.Entry<String,JL5Call> e : tempMethods.entrySet()) {
			sb.append(e.getKey()+", ");
    	}
		sb.append("]");
		System.out.println(sb.toString());
    }
    
    public void printAtomicFields() {
        System.out.println("ATOMIC FIELDS LIST:");
        for (Map.Entry<String,JL5Field> e : fields.entrySet()) {
            System.out.println(e.getKey());
        }
        //System.out.println("Methods Visited: "+visitedMethods);
    }
    
}
