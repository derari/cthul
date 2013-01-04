package org.cthul.parser;

import org.cthul.parser.api.*;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.lexer.Lexer;

public class Parser<I extends Input<?>> {
    
    private final Lexer<I> lexer;
    private final Grammar<I> grammar;

    public Parser(Lexer<I> lexer, Grammar<I> grammar) {
        this.lexer = lexer;
        this.grammar = grammar;
    }
    
    protected Object parse(Context<StringInput> context, RuleKey startSymbol) {
        I tokens = lexer.scan(context);
        Context<I> c = context.forInput(tokens);
        return grammar.parse(c, startSymbol);
    }
    
}
