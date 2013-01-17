package org.cthul.parser;

import org.cthul.parser.api.*;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.lexer.Lexer;

public class AbstractParser {
    
    public static final String ORIGINAL_INPUT = "org.cthul.parser#OriginalInput";
    
    private final Lexer<?> lexer;
    private final Grammar<?> grammar;

    public <I extends Input<?>> AbstractParser(Lexer<? extends I> lexer, Grammar<? super I> grammar) {
        this.lexer = lexer;
        this.grammar = grammar;
    }
    
    protected Object parse(Context<StringInput> context, RuleKey startSymbol) {
        return parse(context, startSymbol, null);
    }
    
    @SuppressWarnings("unchecked") // generics are checked in constructor
    protected Object parse(Context<StringInput> context, RuleKey startSymbol, Object arg) {
        Input tokens = lexer.scan(context);
        Context c = context.forInput(tokens);
        c.put(ORIGINAL_INPUT, context.getInput());
        return grammar.parse(c, startSymbol, arg);
    }
    
}
