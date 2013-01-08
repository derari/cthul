package org.cthul.parser;

import org.cthul.parser.api.Input;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.lexer.Lexer;

public interface ParserFactory<Parser> {
    
    <I extends Input<?>> Parser create(Lexer<? extends I> lexer, Grammar<? super I> grammar);
    
}
