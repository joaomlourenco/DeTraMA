package polyglot.ext.ml5.visit.context;

import java.util.HashMap;

import polyglot.ext.jl5.ast.JL5CanonicalTypeNode;
import polyglot.ext.jl5.ast.JL5Field;
import polyglot.ext.jl5.ast.JL5FieldDecl;
import polyglot.ext.ml5.util.PolyglotUtil;

public class FieldContext {
	
	//Key: JL5Field.fieldInstance()   Value: Instantiation Type 
	protected HashMap<String,JL5CanonicalTypeNode> instFields;
	
	public FieldContext() {
		instFields = new HashMap<String,JL5CanonicalTypeNode>();
	}
	
	public void addField(JL5FieldDecl field) {
		instFields.put(PolyglotUtil.fieldSignature(field), null);
	}
	
	public void setFieldInstType(JL5Field field, JL5CanonicalTypeNode type) {
		if (instFields.containsKey(PolyglotUtil.fieldSignature(field))) {
			instFields.put(PolyglotUtil.fieldSignature(field), type);
		}
	}
	
	public JL5CanonicalTypeNode getFieldInstType(JL5Field field) {
		return instFields.get(PolyglotUtil.fieldSignature(field));
	}
	
	

}
