package org.cthul.parser.lexer.simple;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cthul.parser.api.*;
import org.cthul.parser.lexer.LexerBase;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.api.TokenMatcher;

/**
 * 
 * @param <Token> tokens produced by input that is produced by this lexer
 */
public class SimpleLexer<Token> extends LexerBase<TokenInput<Token>> {

    private final List<Rule<? extends Token>> rules = new ArrayList<>();

    public SimpleLexer(Collection<? extends TokenMatcher<? super TokenInput<Token>>> matchers,
                       Collection<? extends Rule<? extends Token>> rules) {
        super(matchers);
        this.rules.addAll(rules);
        Collections.sort(this.rules);
    }

    @Override
    public TokenInput<Token> scan(Context<StringInput> context) {
        final List<Token> tokens = new ArrayList<>();
        final String input = context.getInput().getString();
        final int len = input.length();
        final InputPosition pos = new InputPosition();
        while (pos.i < len) {
            for (Rule<? extends Token> r: rules) {
                Token t = r.scan(context, pos);
                if (t != null) {
                    tokens.add(t);
                    break;
                }
            }
        }
        return new TokenInput<>(tokens);
    }
    
    protected static class InputPosition {
        int i = 0;
    }
    
    protected static abstract class Rule<Token> implements Comparable<Rule<?>> {
        
        private final RuleKey key;
        private final int id;

        public Rule(RuleKey key, int id) {
            this.key = key;
            this.id = id;
        }

        public abstract Token scan(Context<StringInput> context, InputPosition p);

        @Override
        public int compareTo(Rule<?> o) {
            int c = key.getPriority() - o.key.getPriority();
            if (c != 0) return c;
            return id - o.id;
        }
        
    }
    
    protected static abstract class MatchingRule<Token, Match> extends Rule<Token> {

        private final InputEval<Token, Match> eval;

        public MatchingRule(RuleKey key, int id, InputEval<Token, Match> eval) {
            super(key, id);
            this.eval = eval;
        }

        @Override
        public Token scan(Context<StringInput> context, InputPosition p) {
            int start = p.i;
            Match m = match(context, p);
            if (m == null) return null;
            return eval.eval(context, m, start, p.i);
        }

        protected abstract Match match(Context<StringInput> context, InputPosition p);
        
    }
    
    protected static class StringRule<Token> extends MatchingRule<Token, String> {

        private final String[] strings;

        public StringRule(RuleKey key, int id, InputEval<Token, String> eval, String[] strings) {
            super(key, id, eval);
            this.strings = strings;
        }
        
        @Override
        protected String match(Context<StringInput> context, InputPosition p) {
            final int start = p.i;
            final String input = context.getInput().getString();
            for (String s: strings) {
                if (input.startsWith(s, start)) {
                    p.i += s.length();
                    return s;
                }
            }
            return null;
        }
    }
    
    protected static class RegexRule<Token> extends MatchingRule<Token, MatchResult> {

        private final Pattern pattern;

        public RegexRule(RuleKey key, int id, InputEval<Token, MatchResult> eval, Pattern pattern) {
            super(key, id, eval);
            this.pattern = pattern;
        }
        
        @Override
        protected MatchResult match(Context<StringInput> context, InputPosition p) {
            final int start = p.i;
            final String input = context.getInput().getString();
            Matcher m = pattern.matcher(input);
            if (!m.find(start)) return null;
            return m;
        }
    }
    
}
