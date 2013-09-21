package polyglot.ext.ml5.bct;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.SourceFile;
import polyglot.ast.TypeNode;
import polyglot.ext.jl5.ast.JL5ClassBody;
import polyglot.ext.jl5.ast.JL5ClassDecl;
import polyglot.ext.jl5.ast.JL5FieldDecl;
import polyglot.ext.jl5.types.FlagAnnotations;
import polyglot.ext.ml5.ast.MyLang5NodeFactory;
import polyglot.types.Flags;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

public class GlobalLockGeneratorVisitor extends NodeVisitor {
	
	private String mainClass;
	private String path;
	private String lockName;
	private String fullLockName;
	private TypeSystem ts;
	private MyLang5NodeFactory nf;
	private boolean done = false;
	private String currentPackage = "";
	
	private static GlobalLockGeneratorVisitor instance = null;
	
	private GlobalLockGeneratorVisitor(String mainClass, String path, TypeSystem ts, MyLang5NodeFactory nf) {
		super();
		this.mainClass = mainClass;
		this.path = path;
		this.ts = ts;
		this.nf = nf;
	}
	
	public static GlobalLockGeneratorVisitor getInstance (String mainClass, String path, TypeSystem ts, MyLang5NodeFactory nf) {
		if (instance == null)
			instance = new GlobalLockGeneratorVisitor(mainClass, path, ts, nf);
		return instance;
	}

	public synchronized boolean isMainClass(String className) {
		String cp = currentPackage;
		String fullName;
		if (cp == null || cp.equals(""))
			fullName = className;
		else
			fullName = cp + "." + className;
		return hasMainClass() && this.mainClass.equals(fullName);
	}
	
	public synchronized boolean hasMainClass() {
		return this.mainClass != null && !this.mainClass.equals("");
	}
	
	@Override
	public synchronized NodeVisitor begin() {
		/*
		File dir = new File(path + "/chord/");
		File f = new File (path + "/chord/chord.properties");
		try {
			dir.mkdirs();
			f.createNewFile();
			FileWriter fw = new FileWriter(f);
			fw.write("chord.main.class=" + mainClass + "\n");
			//fw.write("chord.class.path=" + path + "/classes/\n");
			//fw.write("chord.src.path=" + path + "/src/\n");
			//fw.write("chord.out=" + path + "/log.txt\n");
			//fw.write("chord.err=" + path + "/log.txt\n");
			fw.write("chord.class.path=../output/\n");
			fw.write("chord.src.path=../output/\n");
			fw.write("chord.out=../output/log.txt\n");
			fw.write("chord.err=../output/log.txt\n");
			fw.write("chord.serial.file=./" + mainClass.substring(mainClass.lastIndexOf(".")+1) + ".ser\n");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		return super.begin();
	}

	protected synchronized String generateLock(List members) {
		final String prefix = "_GLOBAL_LOCK_";
		final String suffix = "_";
		String lock;
		Random rand = new Random();
		int token;
		// avoid name collision
		/*
		Set<String> names = new TreeSet<String>();
		for (Object member : members) {
			if (member instanceof FieldDecl) {
				String name = ((FieldDecl)member).name();
				String sss = name.substring(0, 11);
				if (sss.equals(prefix))
					names.add(name);
			}
		}
		lockName = prefix;
		token = 2;
		while (names.contains(lockName)) {
			lockName = prefix + token;
			token++;
		}
		*/
		token = rand.nextInt();
		token *= (token < 0) ? (-1) : 1; // ensure token is positive. haven't found a simpler way.
		lock = prefix + token + suffix;
		while (members.contains(lock)) {
			token++;
			lock = prefix + token + suffix;
		}
		return lock;
	}
	
	@Override
	public synchronized NodeVisitor enter(Node parent, Node n) {
		//System.out.println("GLGV Visiting: " + n);
		if (!done) {
			if (n instanceof SourceFile) {
				SourceFile sf = (SourceFile) n;
				PackageNode pn = sf.package_();
				if (pn == null)
					this.currentPackage = "";
				else
					this.currentPackage = sf.package_().package_().fullName();
			}
		}
		return super.enter(parent, n);
	}

	@Override
	public synchronized Node leave(Node parent, Node old, Node n, NodeVisitor v) {
		if (!done) {
			if (parent instanceof JL5ClassDecl && n instanceof JL5ClassBody) {
				JL5ClassDecl cd = (JL5ClassDecl) parent;
				JL5ClassBody cb = (JL5ClassBody) n;
				if (!hasMainClass() || isMainClass(cd.name())) {
					List list = cb.members();
					this.lockName = generateLock(list);
					this.fullLockName = this.currentPackage
							+ ((this.currentPackage == null || this.currentPackage.equals("")) ? "" : ".")
							+ cd.name() + "." + this.lockName;
					
					String name = this.lockName;
					Position pos = Position.COMPILER_GENERATED;
					FlagAnnotations flags = new FlagAnnotations(Flags.PUBLIC.set(Flags.FINAL).set(
							Flags.STATIC), new LinkedList());
					Type type = ts.Object();
					TypeNode typeNode = nf.CanonicalTypeNode(
							Position.COMPILER_GENERATED, type);
					Expr expr = nf.New(Position.COMPILER_GENERATED,
							(TypeNode) typeNode.copy(), new LinkedList());

					
					JL5FieldDecl fieldDecl = nf.JL5FieldDecl(pos, flags, typeNode,
							name, expr);
					List<Object> newList = new LinkedList<Object>();
					for (Object object : list) {
						newList.add(object);
					}
					newList.add(fieldDecl);
					done = true;
					return cb.members(newList);
				}
			} else if (n instanceof SourceFile) {
				this.currentPackage = "";
			}
		}
		return n;
	}

	public synchronized String getFullLockName() {
		return fullLockName;
	}

	public synchronized boolean isDone() {
		return done;
	}

}
