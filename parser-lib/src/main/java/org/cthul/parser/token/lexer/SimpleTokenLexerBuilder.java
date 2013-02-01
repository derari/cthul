package org.cthul.parser.token.lexer;

import java.util.regex.Pattern;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.api.TokenInputMatcher;
import org.cthul.parser.lexer.simple.SimpleLexerBuilder;
import org.cthul.parser.token.Token;
import org.cthul.parser.token.TokenStream;
import org.cthul.parser.token.lexer.SimpleTokenLexer.RegexRule;
import org.cthul.parser.token.lexer.SimpleTokenLexer.RuleSet;
import org.cthul.parser.token.lexer.SimpleTokenLexer.State;
import org.cthul.parser.token.lexer.SimpleTokenLexer.StringRule;

public class SimpleTokenLexerBuilder<V, T extends Token<?>> extends SimpleLexerBuilder<T, TokenStreamBuilder<V, T>, TokenStream<T>> {

    private RuleSetBuilder<V, T> myRSB;
    
    public SimpleTokenLexerBuilder() {
        addMatcher(TokenInputMatcher.MATCH_ANY);
    }

    protected SimpleTokenLexerBuilder(SimpleTokenLexerBuilder<V, T> source) {
        super(source);
    }

    @Override
    public SimpleTokenLexer<T> createLexer() {
        return new SimpleTokenLexer<>(myRSB.createRuleSet());
    }
    
    @Override
    protected RuleSetBuilder<V, T> newRuleSetBuilder() {
        myRSB = new RuleSetBuilder<>(this);
        return myRSB;
    }

    @Override
    public SimpleTokenLexerBuilder<V, T> copy() {
        return new SimpleTokenLexerBuilder<>(this);
    }

    protected static class RuleSetBuilder<V, T extends Token<?>> extends SimpleLexerBuilder.RuleSetBuilder<T, TokenStreamBuilder<V, T>, TokenStream<T>, State<V, T>> {

        public RuleSetBuilder(SimpleTokenLexerBuilder<V, T> lexerBuilder) {
            super(lexerBuilder);
        }

        @Override
        public RuleSet<V, T> createRuleSet() {
            return new RuleSet<>(rules);
        }

        @Override
        protected TokenInputMatcher createMatcher(RuleKey key) {
            return null;
        }

        @Override
        protected StringRule<V, T> newStringRule(RuleKey key, int id, InputEval<? extends T, ? super TokenStreamBuilder<V, T>> eval, String string) {
            return new StringRule<>(key, id, eval, new String[]{string});
        }

        @Override
        protected StringRule<V, T> newStringRule(RuleKey key, int id, InputEval<? extends T, ? super TokenStreamBuilder<V, T>> eval, String... strings) {
            return new StringRule<>(key, id, eval, strings);
        }

        @Override
        protected RegexRule<V, T> newRegexRule(RuleKey key, int id, InputEval<? extends T, ? super TokenStreamBuilder<V, T>> eval, Pattern pattern) {
            return new RegexRule<>(key, id, eval, pattern);
        }
    }
}
