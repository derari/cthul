package org.cthul.parser;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.AmbiguousGrammar;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.lexer.Lexer;

public class AmbiguousParser<Result> extends AbstractParser {

    public <I extends Input<?>> AmbiguousParser(Lexer<? extends I> lexer, AmbiguousGrammar<? super I> grammar) {
        super(lexer, grammar);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<Result> parse(Context context, RuleKey startSymbol) {
        return (Iterable<Result>) super.parse(context, startSymbol);
    }
    
    public Iterable<Result> parse(Context context, String startSymbol) {
        return parse(context, new RuleKey(startSymbol, 0));
    }
    
    private static final Factory<Object> FACTORY = new Factory<>();
    
    @SuppressWarnings("unchecked")
    public static <Result> Factory<Result> factory() {
        return (Factory) FACTORY;
    }
    
    public static class Factory<Result> implements ParserFactory<AmbiguousParser<Result>> {
        @Override
        @SuppressWarnings("unchecked")
        public <I extends Input<?>> AmbiguousParser<Result> create(Lexer<? extends I> lexer, Grammar<? super I> grammar) {
            return new AmbiguousParser<>(lexer, (AmbiguousGrammar) grammar);
        }
    }
    
}
