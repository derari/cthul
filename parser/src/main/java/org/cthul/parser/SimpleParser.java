package org.cthul.parser;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.lexer.Lexer;

public class SimpleParser<Result> extends AbstractParser {

    public <I extends Input<?>> SimpleParser(Lexer<? extends I> lexer, Grammar<? super I> grammar) {
        super(lexer, grammar);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result parse(Context context, RuleKey startSymbol) {
        return (Result) super.parse(context, startSymbol);
    }
    
    public Result parse(String input, RuleKey startSymbol) {
        return parse(Context.forString(input), startSymbol);
    }
    
    public Result parse(Context context, String startSymbol) {
        return parse(context, new RuleKey(startSymbol, 0));
    }
    
    public Result parse(String input, String startSymbol) {
        return parse(Context.forString(input), startSymbol);
    }
    
    private static final Factory<Object> FACTORY = new Factory<>();
    
    @SuppressWarnings("unchecked")
    public static <Result> Factory<Result> factory() {
        return (Factory) FACTORY;
    }
    
    public static class Factory<Result> implements ParserFactory<SimpleParser<Result>> {

        @Override
        public <I extends Input<?>> SimpleParser<Result> create(Lexer<? extends I> lexer, Grammar<? super I> grammar) {
            return new SimpleParser<>(lexer, grammar);
        }
        
    }
    
}