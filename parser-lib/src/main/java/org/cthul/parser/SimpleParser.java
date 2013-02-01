package org.cthul.parser;

import java.util.Iterator;
import java.util.regex.MatchResult;
import org.cthul.parser.AbstractParser;
import org.cthul.parser.ParserFactory;
import org.cthul.parser.SimpleParser.Builder;
import org.cthul.parser.api.*;
import org.cthul.parser.grammar.Ambiguous2SimpleGrammar;
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
    
    @SuppressWarnings("unchecked")
    private static final Factory FACTORY = new Factory<>();
    
    public static <Result> Factory<Result> factory() {
        return FACTORY;
    }
    
    public static class Factory<Result> implements ParserFactory<SimpleParser<Result>> {

        @Override
        public <I extends Input<?>> SimpleParser<Result> create(Lexer<? extends I> lexer, Grammar<? super I> grammar) {
            return new SimpleParser<>(lexer, grammar);
        }
        
    }
    
    @SuppressWarnings("unchecked")
    private static final ParserFactory A2S_FACTORY = new Ambiguous2SimpleGrammar.Factory(FACTORY, true);
    
    public static <Result> ParserFactory<SimpleParser<Result>> a2sFactory() {
        return A2S_FACTORY;
    }
    
    public static class Builder<T, M> extends ParserBuilder<T, M> {

        public <I extends Input<?>> Builder(LexerBuilder<T, ? extends M, I> lexerBuilder, GrammarBuilder<I> grammarBuilder, InputEval<? extends T, ? super M> stringTokenEval) {
            super(lexerBuilder, grammarBuilder, stringTokenEval);
        }
        
        public <Result> SimpleParser<Result> createParser() {
            return createParser(SimpleParser.<Result>factory());
        }
    }
    
    protected static class A2SBuilder<T, M> extends Builder<T, M> {

        public <I extends Input<?>> A2SBuilder(LexerBuilder<T, ? extends M, I> lexerBuilder, GrammarBuilder<I> grammarBuilder, InputEval<? extends T, ? super M> stringTokenEval) {
            super(lexerBuilder, grammarBuilder, stringTokenEval);
        }

        @Override
        public <Result> SimpleParser<Result> createParser() {
            return createParser(SimpleParser.<Result>a2sFactory());
        }
    }
    
    public static <V, T extends Token<?>> Builder<T, TokenStreamBuilder<V, T>> buildEarleyWithLexer() {
        SimpleTokenLexerBuilder<V, T> lb = new SimpleTokenLexerBuilder<>();
        EarleyXItGrammarBuilder<TokenStream<T>> gb = new EarleyXItGrammarBuilder<>();
        InputEval<T, TokenBuilder<V, T>> pie = PlainTokenInputEval.instance();
        InputEval<T, Object> ie = new TokenStreamInputEval<>(pie);
        return new A2SBuilder<>(lb, gb, ie);
    }
    
    public static Builder<Object, MatchResult> buildEarleyWithoutLexer() {
        LazyLexerBuilder<Object> lb = new LazyLexerBuilder<>();
        EarleyXGrammarBuilder<StringInput> gb = new EarleyXItGrammarBuilder<>();
        InputEval<String, Object> ie = PlainStringInputEval.instance();
        return new A2SBuilder<>(lb, gb, ie);
    }
    
}
