package polyglot.ext.ml5.visit.context;

import java.util.HashMap;

import polyglot.ext.jl5.ast.EnumConstantDecl;
import polyglot.ext.jl5.ast.JL5Field;
import polyglot.ext.ml5.util.PolyglotUtil;

public class EnumContext {
	
	//Key: JL5Field.fieldInstance()   Value: Instantiation Type 
	protected HashMap<String,Object> enums;
	
	public EnumContext() {
		enums = new HashMap<String,Object>();
	}
	
	public void addEnumDecl(EnumConstantDecl e) {
		enums.put(PolyglotUtil.enumSignature(e), new Object());
	}
	
	public boolean isFieldEnum(JL5Field f) {
		return enums.containsKey(PolyglotUtil.fieldSignature(f));
	}
	
	

}
