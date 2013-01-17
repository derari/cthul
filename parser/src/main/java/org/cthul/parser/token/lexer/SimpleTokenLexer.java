package org.cthul.parser.token.lexer;

import java.util.Collection;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.api.MatchEval;
import org.cthul.parser.lexer.simple.SimpleLexer;
import org.cthul.parser.token.Token;
import org.cthul.parser.token.TokenStream;

public class SimpleTokenLexer<T extends Token<?>> extends SimpleLexer<TokenStream<T>> {

    public SimpleTokenLexer(RuleSet<?, T> ruleSet) {
        super(ruleSet);
    }

    public static class RuleSet<V, T extends Token<? extends V>> extends SimpleLexer.RuleSet<T, TokenStream<T>, State<V, T>> {

        public RuleSet(Collection<Rule<? extends T, ? super State<V, T>>> rules) {
            super(rules);
        }

        @Override
        protected State<V, T> newState(Context<StringInput> context) {
            return new State<>(context, rules);
        }
    }

    public static class State<V, T extends Token<? extends V>> extends SimpleLexer.State<T, TokenStream<T>> {
        
        private final TokenStreamBuilder<V, T> ts = new TokenStreamBuilder<>();

        public State(Context<StringInput> context, List<? extends Rule<? extends T, ? super State<V, T>>> rules) {
            super(context, rules);
            context.initialize(ts);
            context.put(TokenStreamBuilder.class, ts);
        }

        public TokenStreamBuilder<V, T> getTokenStreamBuilder() {
            return ts;
        }

        @Override
        protected TokenStream<T> createInput(List<T> tokens) {
            return ts;
        }

        @Override
        protected void addToken(List<T> tokens, T t) {
            super.addToken(tokens, t);
            ts.push(t);
        }
    }
    
    public static class StringRule<V, T extends Token<? extends V>> extends SimpleLexer.StringRule<T, TokenStreamBuilder<V, T>, State<V, T>> {

        public StringRule(RuleKey key, int id, MatchEval<? extends T, ? super TokenStreamBuilder<V, T>> eval, String[] strings) {
            super(key, id, eval, strings);
        }

        @Override
        protected TokenStreamBuilder<V, T> match(Context<StringInput> context, StringInput input, State<V, T> state, int start, String match) {
            TokenStreamBuilder<V, T> ts = state.getTokenStreamBuilder();
            ts.setNextMatch(match, start);
            return ts;
        }
    }
    
    public static class RegexRule<V, T extends Token<? extends V>> extends SimpleLexer.RegexRule<T, TokenStreamBuilder<V, T>, State<V, T>> {

        public RegexRule(RuleKey key, int id, MatchEval<? extends T, ? super TokenStreamBuilder<V,T>> eval, Pattern pattern) {
            super(key, id, eval, pattern);
        }

        @Override
        protected TokenStreamBuilder<V,T> match(Context<StringInput> context, StringInput input, State<V, T> state, int start, MatchResult match) {
            TokenStreamBuilder<V, T> ts = state.getTokenStreamBuilder();
            ts.setNextMatch(match);
            return ts;
        }
    }
}
