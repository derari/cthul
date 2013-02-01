package org.cthul.parser;

import java.util.regex.MatchResult;
import org.cthul.parser.api.*;
import org.cthul.parser.grammar.AmbiguousGrammar;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.grammar.GrammarBuilder;
import org.cthul.parser.grammar.earleyx.EarleyXGrammarBuilder;
import org.cthul.parser.grammar.earleyx.EarleyXItGrammarBuilder;
import org.cthul.parser.lexer.Lexer;
import org.cthul.parser.lexer.LexerBuilder;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.api.PlainStringInputEval;
import org.cthul.parser.lexer.lazy.LazyLexerBuilder;
import org.cthul.parser.token.Token;
import org.cthul.parser.token.TokenBuilder;
import org.cthul.parser.token.TokenStream;
import org.cthul.parser.token.lexer.*;

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
    
    public Iterable<Result> parse(String input, RuleKey startSymbol) {
        return parse(Context.forString(input), startSymbol);
    }
    
    public Iterable<Result> parse(String input, String startSymbol) {
        return parse(Context.forString(input), startSymbol);
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
    
    public static class Builder<T, M> extends ParserBuilder<T, M> {

        public <I extends Input<?>> Builder(LexerBuilder<T, ? extends M, I> lexerBuilder, GrammarBuilder<I> grammarBuilder, InputEval<? extends T, ? super M> stringTokenEval) {
            super(lexerBuilder, grammarBuilder, stringTokenEval);
        }
        
        public <Result> AmbiguousParser<Result> createParser() {
            return createParser(AmbiguousParser.<Result>factory());
        }
        
    }
    
    public static <V, T extends Token<?>> Builder<T, TokenStreamBuilder<V, T>> buildEarleyWithLexer() {
        SimpleTokenLexerBuilder<V, T> lb = new SimpleTokenLexerBuilder<>();
        EarleyXItGrammarBuilder<TokenStream<T>> gb = new EarleyXItGrammarBuilder<>();
        InputEval<T, TokenBuilder<V, T>> pie = PlainTokenInputEval.instance();
        InputEval<T, Object> ie = new TokenStreamInputEval<>(pie);
        return new Builder<>(lb, gb, ie);
    }
    
    public static Builder<Object, MatchResult> buildEarleyWithoutLexer() {
        LazyLexerBuilder<Object> lb = new LazyLexerBuilder<>();
        EarleyXGrammarBuilder<StringInput> gb = new EarleyXItGrammarBuilder<>();
        InputEval<String, Object> ie = PlainStringInputEval.instance();
        return new Builder<>(lb, gb, ie);
    }
    
}
