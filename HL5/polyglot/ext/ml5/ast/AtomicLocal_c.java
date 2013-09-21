package polyglot.ext.ml5.ast;


import polyglot.ext.jl.ast.Local_c;
import polyglot.ext.jl.ast.Special_c;
import polyglot.ext.jl.types.FieldInstance_c;
import polyglot.ext.jl.types.ParsedClassType_c;
import polyglot.ext.jl5.ast.JL5Field_c;
import polyglot.ext.jl5.ast.JL5MethodDecl_c;
import polyglot.ext.ml5.util.PolyglotUtil;
import polyglot.types.FieldInstance;
import polyglot.util.Position;

public class AtomicLocal_c extends JL5Field_c {
	
	private JL5MethodDecl_c method;
	private Local_c local;

	public AtomicLocal_c(Local_c local, Position pos,  
			String name, JL5MethodDecl_c method) {
		super(pos, new Special_c(pos, Special_c.THIS, null), name);
		this.method = method;
		this.local = local;
		this.targetImplicit = true;
	}
	
	@SuppressWarnings("serial")
	private class DummyType extends ParsedClassType_c {
		private String name;
		public DummyType(String name) {
			super();
			this.name = name;
		}
		public String toString() {
			return name;
		}
	}
	
	public FieldInstance fieldInstance() {
		
		return new FieldInstance_c(local.localInstance().typeSystem(), 
				local.localInstance().position(), 
				new DummyType(PolyglotUtil.methodSignature(method)),
				local.localInstance().flags(), local.localInstance().type(),
				local.localInstance().name());
	}

}
