package polyglot.ext.ml5;

import java.io.Reader;
import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.ext.ml5.ast.MyLang5NodeFactory_c;
import polyglot.ext.ml5.bct.test.MethodListContext;
import polyglot.ext.ml5.bct.test.MethodLister;
import polyglot.ext.ml5.parse.Grm;
import polyglot.ext.ml5.parse.Lexer_c;
import polyglot.ext.ml5.types.MyLang5TypeSystem_c;
import polyglot.ext.ml5.visit.context.ArrayMethodContext;
import polyglot.ext.ml5.visit.context.AtomicFieldContext;
import polyglot.ext.ml5.visit.context.EnumContext;
import polyglot.ext.ml5.visit.context.FieldContext;
import polyglot.frontend.CupParser;
import polyglot.frontend.FileSource;
import polyglot.frontend.GlobalBarrierPass;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.Pass;
import polyglot.frontend.VisitorPass;
import polyglot.frontend.Pass.ID;
import polyglot.lex.Lexer;
import polyglot.main.Options;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;

/**
 * Extension information for MyLang5 extension.
 */
public class ExtensionInfo extends polyglot.ext.jl5.ExtensionInfo {
	static {
		// force Topics to load
		new Topics();
	}

	AtomicFieldContext context = new AtomicFieldContext();

	FieldContext fieldContext = new FieldContext();

	EnumContext enumContext = new EnumContext();

	ArrayMethodContext arrayContext = new ArrayMethodContext();
	
	// BCT
	MethodListContext mlContext = new MethodListContext();
     
    
    public static final polyglot.frontend.Pass.ID UNARY_OP_EXPANDER = new polyglot.frontend.Pass.ID("unary-op-expander");
    public static final polyglot.frontend.Pass.ID FIELD_INST_TYPE = new polyglot.frontend.Pass.ID("field-inst-type");
    public static final polyglot.frontend.Pass.ID ENUM_FINDER = new polyglot.frontend.Pass.ID("enum-finder");
    public static final polyglot.frontend.Pass.ID ATOMIC_FIELD_FINDER = new polyglot.frontend.Pass.ID("atomic-field-finder");
    public static final polyglot.frontend.Pass.ID ATOMIC_FIELD_REWRITER = new polyglot.frontend.Pass.ID("atomic-field-rewriter");
    public static final polyglot.frontend.Pass.ID FIELD_BARRIER = new polyglot.frontend.Pass.ID("field-barrier");
    public static final polyglot.frontend.Pass.ID ARRAY_BARRIER = new polyglot.frontend.Pass.ID("array-barrier");
    public static final polyglot.frontend.Pass.ID ARRAY_METHOD_FINDER = new polyglot.frontend.Pass.ID("array-method-finder");
    public static final polyglot.frontend.Pass.ID ARRAY_METHOD_REPL = new polyglot.frontend.Pass.ID("array-method-repl");
    
    public static final polyglot.frontend.Pass.ID BCT_METHOD_LISTER_ID = new polyglot.frontend.Pass.ID("bct_method_lister");
    public static final polyglot.frontend.Pass.ID BCT_THREAD_TRACER_ID = new polyglot.frontend.Pass.ID("bct_tracer");
    public static final polyglot.frontend.Pass.ID BCT_TRACER_BARRIER = new polyglot.frontend.Pass.ID("bct_barrier_tracer");
    public static final polyglot.frontend.Pass.ID BCT_LISTER_BARRIER = new polyglot.frontend.Pass.ID("bct_barrier_lister");
    
    public static final polyglot.frontend.Pass.ID GLG_PASS_ID = new polyglot.frontend.Pass.ID("global-lock-generator");
    public static final polyglot.frontend.Pass.ID GL_BARRIER = new polyglot.frontend.Pass.ID("global-lock-barrier");
    public static final polyglot.frontend.Pass.ID ATL_PASS_ID = new polyglot.frontend.Pass.ID("atomic-to-lock");
    
    
    public String defaultFileExtension() {
        //return "ml5";
    	return "atom.java";
    }

    @Override
	public boolean runToCompletion() {
		boolean result = super.runToCompletion();
		
		mlContext.trace();
		
		return result;
	}

	public String compilerName() {
        return "MyLang5c";
    }

  public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
		Lexer lexer = new Lexer_c(reader, source.name(), eq);
		Grm grm = new Grm(lexer, ts, nf, eq);
		return new CupParser(grm, source, eq);
	}

	protected NodeFactory createNodeFactory() {
		return new MyLang5NodeFactory_c();
	}

	protected TypeSystem createTypeSystem() {
		return new MyLang5TypeSystem_c();
	}

	@Override
	protected Options createOptions() {
		return new polyglot.ext.ml5.bct.Options(this);
	}

	@SuppressWarnings("unchecked")
	public List passes(Job job) {
        getOptions().serialize_type_info = false;
        List passes = super.passes(job);
        //String mainClass = ((polyglot.ext.ml5.bct.Options)getOptions()).getMainClass();
        //String path = ((polyglot.ext.ml5.bct.Options)getOptions()).getPath();
        //System.out.println("-- ExtensionInfo job -> "+passes);
        
        
        /**/
        MethodLister lister = new MethodLister(mlContext); //MethodLister.getInstance();
        afterPass(passes, Pass.FWD_REF_CHECK, new VisitorPass(BCT_METHOD_LISTER_ID, job, lister));
        afterPass(passes, BCT_METHOD_LISTER_ID, new GlobalBarrierPass(BCT_LISTER_BARRIER, job));
        //afterPass(passes, BCT_LISTER_BARRIER, new VisitorPass(BCT_THREAD_TRACER_ID, job, new ThreadTracer(lister)));
        //afterPass(passes, BCT_THREAD_TRACER_ID, new GlobalBarrierPass(BCT_TRACER_BARRIER, job));
        /**/
        
        /**/
        //GlobalLockGeneratorVisitor glgv = GlobalLockGeneratorVisitor.getInstance(mainClass, path, this.typeSystem(), (MyLang5NodeFactory)this.nodeFactory());
        //afterPass(passes, BCT_TRACER_BARRIER, new VisitorPass(GLG_PASS_ID, job, glgv));
        //afterPass(passes, GLG_PASS_ID, new GlobalBarrierPass(GL_BARRIER, job));
        //AtomicToLockVisitor atlv = new AtomicToLockVisitor(glgv, this.typeSystem(), (MyLang5NodeFactory)this.nodeFactory());
        //afterPass(passes, GL_BARRIER, new VisitorPass(ATL_PASS_ID, job, atlv));
        /**/
        
        
        //RJFD
//        afterPass(passes, Pass.FWD_REF_CHECK, new VisitorPass(UNARY_OP_EXPANDER, job, new UnaryOperatorExpanderVisitor(job, ts, nf)));
//        afterPass(passes, UNARY_OP_EXPANDER, new VisitorPass(FIELD_INST_TYPE, job, new FieldInstTypeVisitor(fieldContext, job, ts, nf)));
//        afterPass(passes, FIELD_INST_TYPE, new VisitorPass(ENUM_FINDER, job, new FieldEnumFinderVisitor(enumContext, job, ts, nf)));
//        afterPass(passes, ENUM_FINDER, new VisitorPass(ATOMIC_FIELD_FINDER, job, new AtomicFieldFinderVisitor(enumContext, fieldContext, context, job, ts, nf)));
//        
//        	afterPass(passes, ATOMIC_FIELD_FINDER, new GlobalBarrierPass(ARRAY_BARRIER, job));
//        afterPass(passes, ARRAY_BARRIER, new VisitorPass(ARRAY_METHOD_FINDER, job, new AtomicArrayMethodFinderVisitor(context, arrayContext, job, ts, nf)));
//        afterPass(passes, ARRAY_METHOD_FINDER, new VisitorPass(ARRAY_METHOD_REPL, job, new ArrayMethodReplication(arrayContext, context, job, ts, nf)));
//        
//        	afterPass(passes, ARRAY_METHOD_REPL, new GlobalBarrierPass(FIELD_BARRIER, job));
//        afterPass(passes, FIELD_BARRIER, new VisitorPass(ATOMIC_FIELD_REWRITER, job, new AtomicFieldRewriterVisitor(context, job, ts, nf)));
//        
        
        
        //BCT
        Pass pass = null;
        for (Object object : passes) {
        	pass = (Pass) object;
        	if (pass.id().toString().equals("output")) {
        		break;
        	}
		}
        if (pass != null)
        	this.removePass(passes, pass.id());
        
        return passes;
    }

}
