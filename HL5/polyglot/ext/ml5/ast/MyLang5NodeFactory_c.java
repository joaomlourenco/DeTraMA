package polyglot.ext.ml5.ast;

import polyglot.ast.Block;
import polyglot.ext.jl5.ast.JL5NodeFactory_c;
import polyglot.util.Position;

/**
 * NodeFactory for MyLang5 extension.
 */
public class MyLang5NodeFactory_c extends JL5NodeFactory_c implements MyLang5NodeFactory {
	
	public AtomicBlock AtomicBlock(Position pos, Block block) {
		AtomicBlock ab = new AtomicBlock_c(pos, block.statements());
		return ab;
	}
}
