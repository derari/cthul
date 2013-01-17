package org.cthul.parser.lexer.simple;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.TokenInput;
import org.cthul.parser.api.TokenInputMatcher;
import org.cthul.parser.grammar.api.InputMatch;
import org.cthul.parser.lexer.api.MatchEval;
import org.cthul.parser.lexer.simple.SimpleMatchLexer.RegexRule;
import org.cthul.parser.lexer.simple.SimpleMatchLexer.RuleSet;
import org.cthul.parser.lexer.simple.SimpleMatchLexer.State;
import org.cthul.parser.lexer.simple.SimpleMatchLexer.StringRule;

public class SimpleMatchLexerBuilder<T extends InputMatch<?>> extends SimpleLexerBuilder<T, MatchResult, TokenInput<T>> {

    public SimpleMatchLexerBuilder() {
    }

    public SimpleMatchLexerBuilder(SimpleMatchLexerBuilder<T> source) {
        super(source);
    }

    @Override
    protected RuleSetBuilder<T> newRuleSetBuilder() {
        return new RuleSetBuilder<>(this);
    }

    @Override
    public SimpleMatchLexerBuilder<T> copy() {
        return new SimpleMatchLexerBuilder<>(this);
    }

    protected static class RuleSetBuilder<T extends InputMatch<?>> extends SimpleLexerBuilder.RuleSetBuilder<T, MatchResult, TokenInput<T>, State<T>> {

        public RuleSetBuilder(SimpleMatchLexerBuilder<T> lexerBuilder) {
            super(lexerBuilder);
        }

        @Override
        public RuleSet<T> createRuleSet() {
            return new RuleSet<>(rules);
        }

        @Override
        protected TokenInputMatcher createMatcher(RuleKey key) {
            return new TokenInputMatcher(key);
        }

        @Override
        protected StringRule<T> newStringRule(RuleKey key, int id, MatchEval<? extends T, ? super MatchResult> eval, String string) {
            return new StringRule<>(key, id, eval, new String[]{string});
        }

        @Override
        protected StringRule<T> newStringRule(RuleKey key, int id, MatchEval<? extends T, ? super MatchResult> eval, String... strings) {
            return new StringRule<>(key, id, eval, strings);
        }

        @Override
        protected RegexRule<T> newRegexRule(RuleKey key, int id, MatchEval<? extends T, ? super MatchResult> eval, Pattern pattern) {
            return new RegexRule<>(key, id, eval, pattern);
        }
    }
}
