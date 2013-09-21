package polyglot.ext.ml5.ast;

import polyglot.ast.Block;
import polyglot.util.Position;


/**
 * NodeFactory for MyLang5 extension.
 */
public interface MyLang5NodeFactory extends polyglot.ext.jl5.ast.JL5NodeFactory {
	AtomicBlock AtomicBlock(Position pos, Block block);
}
