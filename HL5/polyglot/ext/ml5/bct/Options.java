/**
 * 
 */
package polyglot.ext.ml5.bct;

import java.io.File;
import java.util.Set;

import polyglot.frontend.ExtensionInfo;
import polyglot.main.UsageError;
import polyglot.main.Main.TerminationException;

/**
 * @author bruno
 *
 */
public class Options extends polyglot.main.Options {

	/**
	 * @param arg0
	 */
	public Options(ExtensionInfo arg0) {
		super(arg0);
		this.mainClass = "";
	}
	
	protected String mainClass;
	protected String path;

	@Override
	protected int parseCommand(String[] args, int index, Set source) throws UsageError, TerminationException {
		int i = index;
		String arg = args[i];
		if (arg.equals("-main")) {
			if (mainClass != null && !mainClass.equals(""))
				throw new UsageError("Used \"-main\" more than once");
			i++;
			if (args.length == i)
				throw new UsageError("Used \"-main\" without indicating main class");
			mainClass = args[i];
			System.out.println("MAIN CLASS IS: " + mainClass);
			i++;
			return i;
		} else if (!arg.startsWith("-")) { //this is so we can add all files in a dir
			File file = new File(arg);
			if (file.isDirectory()) {
				System.out.println("[ADDING FILES FROM DIR " + file + "]");
				addAll(file, source);
				source_path.add(file); //useless, source_path is not used
			} else {
				System.out.println("[ADDING FILE " + arg + " ]");
				source.add(arg);
				File parent = new File(args[i]).getParentFile(); //useless too
	            if (parent != null && !source_path.contains(parent))
	                source_path.add(parent);
			}
			i++;
			return i;
		} else
			return super.parseCommand(args, i, source);
	}
	
	protected void addAll(File dir, Set source) {
		//System.out.println("[ENTERING DIR " + dir + " ]");
		for (File child : dir.listFiles()) {
			if (child.isDirectory()) {
				addAll(child, source);
			} else {
				String extension = this.extension.defaultFileExtension();
				String fileName = child.getPath();
				if (fileName.endsWith(extension)) {
					System.out.println("[ADDING FILE " + fileName + " ]");
					source.add(fileName);
				} else {
					//System.out.println("[SKIPPING FILE " + child + " ]");
				}
			}
		}
	}

	public String getMainClass() {
		return mainClass;
	}
	
	public boolean hasMainClass() {
		return mainClass != null && !mainClass.equals("");
	}

	public String getPath() {
		return path;
	}

}
