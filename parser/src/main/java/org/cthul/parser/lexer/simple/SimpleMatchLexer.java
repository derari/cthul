package org.cthul.parser.lexer.simple;

import java.util.Collection;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.api.*;
import org.cthul.parser.grammar.api.InputMatch;
import org.cthul.parser.lexer.api.MatchEval;
import org.cthul.parser.util.StringMatchResult;

public class SimpleMatchLexer<T extends InputMatch<?>> extends SimpleLexer<TokenInput<T>> {

    public SimpleMatchLexer(RuleSet<T> ruleSet) {
        super(ruleSet);
    }

    public static class RuleSet<T extends InputMatch<?>> extends SimpleLexer.RuleSet<T, TokenInput<T>, State<T>> {

        public RuleSet(Collection<Rule<? extends T, ? super State<T>>> rules) {
            super(rules);
        }

        @Override
        protected State<T> newState(Context<StringInput> context) {
            return new State<>(context, rules);
        }
    }

    public static class State<T extends InputMatch<?>> extends SimpleLexer.State<T, TokenInput<T>> {

        public State(Context<StringInput> context, List<? extends Rule<? extends T, ? super State<T>>> rules) {
            super(context, rules);
        }

        @Override
        protected TokenInput<T> createInput(List<T> tokens) {
            @SuppressWarnings("unchecked")
            TokenInput<T> result = context.get(TokenInput.class);
            if (result == null) {
                result = new TokenInput<>(tokens);
            }
            return result;
        }
    }
    
    public static class StringRule<T extends InputMatch<?>> extends SimpleLexer.StringRule<T, MatchResult, State<T>> {

        public StringRule(RuleKey key, int id, MatchEval<? extends T, ? super MatchResult> eval, String[] strings) {
            super(key, id, eval, strings);
        }

        @Override
        protected MatchResult match(Context<StringInput> context, StringInput input, State<T> state, int start, String match) {
            return new StringMatchResult(match, start);
        }
    }
    
    public static class RegexRule<T extends InputMatch<?>> extends SimpleLexer.RegexRule<T, MatchResult, State<T>> {

        public RegexRule(RuleKey key, int id, MatchEval<? extends T, ? super MatchResult> eval, Pattern pattern) {
            super(key, id, eval, pattern);
        }

        @Override
        protected MatchResult match(Context<StringInput> context, StringInput input, State<T> state, int start, MatchResult match) {
            return match;
        }
    }
}
