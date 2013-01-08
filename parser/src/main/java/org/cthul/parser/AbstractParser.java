package org.cthul.parser;

import org.cthul.parser.api.*;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.lexer.Lexer;

public class AbstractParser {
    
    private final Lexer<?> lexer;
    private final Grammar<?> grammar;

    public <I extends Input<?>> AbstractParser(Lexer<? extends I> lexer, Grammar<? super I> grammar) {
        this.lexer = lexer;
        this.grammar = grammar;
    }
    
    @SuppressWarnings("unchecked") // generics are checked in constructor
    protected Object parse(Context<StringInput> context, RuleKey startSymbol) {
        Input tokens = lexer.scan(context);
        Context c = context.forInput(tokens);
        return grammar.parse(c, startSymbol);
    }
    
}
