package org.cthul.parser.lexer.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cthul.parser.api.*;
import org.cthul.parser.lexer.LexerBase;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.ScanException;
import org.cthul.parser.util.Format;

/**
 * 
 * @param <I> input produced by this lexer
 */
public class SimpleLexer<I extends Input<?>> extends LexerBase<I> {

    protected final RuleSet<?, ? extends I, ?> ruleSet;

    public SimpleLexer(RuleSet<?, ? extends I, ?> ruleSet) {
        this.ruleSet = ruleSet;
    }

    @Override
    public I scan(Context<StringInput> context) {
        return ruleSet.scan(context);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ruleSet;
    }
    
    /**
     * Helper class to hide the generic parameters
     * @param <Token> tokens created by rules and consumed by {@code S}
     * @param <I> input created by {@code S}
     * @param <S> the lexer state used by rules
     */
    public static abstract class RuleSet<Token, I extends Input<?>, S extends State<? extends Token, ? extends I>> {
        
        protected final List<Rule<? extends Token, ? super S>> rules = new ArrayList<>();

        public RuleSet(Collection<Rule<? extends Token, ? super S>> rules) {
            this.rules.addAll(rules);
        }
        
        public I scan(Context<StringInput> context) {
            return newState(context).scan();
        }
        
        protected abstract S newState(Context<StringInput> context);

        @Override
        public String toString() {
            return Format.join("{", ", ", "}", 512, "...}", rules);
        }
        
    }
    
    /**
     * 
     * @param <Token> tokens created by matching rules
     * @param <I> input that is created
     */
    public static abstract class State<Token, I extends Input<?>> {
        
        protected final Context<StringInput> context;
        private final List<? extends Rule<? extends Token, ?>> rules;
        private int position = 0;

        public State(Context<StringInput> context, 
                     List<? extends Rule<? extends Token, ?>> rules) {
            this.context = context;
            this.rules = rules;
        }
        
        // workaround so State doesn't need a third generic parameter
        @SuppressWarnings("unchecked")
        protected <S extends State<?,?>> List<? extends Rule<? extends Token, S>> rules() {
            return (List) rules;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
        
        protected I scan() {
            final StringInput input = context.getInput();
            final List<Token> tokens = new ArrayList<>();
            final int len = input.getLength();
            Token lastMatch = null;
            position = 0;
            while (position < len) {
                Token t = null;
                for (Rule<? extends Token, State<?,?>> r: rules()) {
                    t = r.scan(context, input, this);
                    if (t != null) break;
                }
                if (t != null) {
                    lastMatch = t;
                    addToken(tokens, t);
                } else {
                    throw new ScanException(
                            lastMatch, input.getString(), 
                            position, null, null);
                }
            }
            return createInput(input.getString(), tokens);
        }

        protected abstract I createInput(String input, List<Token> tokens);

        protected void addToken(final List<Token> tokens, Token t) {
            tokens.add(t);
        }
        
    }
    
    /**
     * 
     * @param <Token> tokens created
     * @param <S> additional state
     */
    protected static abstract class Rule<Token, S extends State<?,?>> implements Comparable<Rule<?,?>> {
        
        protected final RuleKey key;
        private final int id;

        public Rule(RuleKey key, int id) {
            this.key = key;
            this.id = id;
        }

        public abstract Token scan(Context<StringInput> context, StringInput input, S state);

        @Override
        public int compareTo(Rule<?,?> o) {
            int c = o.key.getPriority() - key.getPriority();
            if (c != 0) return c;
            return id - o.id;
        }

        @Override
        public String toString() {
            String s = Format.productionKey(key.getSymbol(), key.getPriority());
            String m = matchString();
            if (m != null) s = s + " ::= " + m;
            return s;
        }
        
        protected String matchString() { return null; }
        
    }
    
    protected static abstract class MatchingRule<Token, Match, S extends State<?,?>> extends Rule<Token, S> {

        private final InputEval<? extends Token, ? super Match> eval;

        public MatchingRule(RuleKey key, int id, InputEval<? extends Token, ? super Match> eval) {
            super(key, id);
            this.eval = eval;
        }

        @Override
        public Token scan(Context<StringInput> context, StringInput input, S state) {
            int start = state.getPosition();
            Match m = match(context, input, state, start);
            if (m == null) return null;
            return eval.eval(context, m, start, state.getPosition());
        }

        protected abstract Match match(Context<StringInput> context, StringInput input, S state, int start);
        
    }
    
    protected static abstract class StringRule<Token, Match, S extends State<?,?>> extends MatchingRule<Token, Match, S> {

        private final String[] strings;

        public StringRule(RuleKey key, int id, InputEval<? extends Token, ? super Match> eval, String[] strings) {
            super(key, id, eval);
            this.strings = strings;
        }
        
        @Override
        protected Match match(Context<StringInput> context, StringInput input, S state, int start) {
            final String str = input.getString();
            for (String s: strings) {
                if (str.startsWith(s, start)) {
                    state.setPosition(start + s.length());
                    return match(context, input, state, start, s);
                }
            }
            return null;
        }

        @Override
        protected String matchString() {
            return Format.join("(", "|", ")", 64, "...)", strings);
        }
        
        protected abstract Match match(Context<StringInput> context, StringInput input, S state, int start, String match);
    }
    
    protected static abstract class RegexRule<Token, Match, S extends State<?,?>> extends MatchingRule<Token, Match, S> {

        private final Pattern pattern;

        public RegexRule(RuleKey key, int id, InputEval<? extends Token, ? super Match> eval, Pattern pattern) {
            super(key, id, eval);
            this.pattern = pattern;
        }
        
        @Override
        protected Match match(Context<StringInput> context, StringInput input, S state, int start) {
            final String str = input.getString();
            Matcher m = pattern.matcher(str);
            if (!m.find(start) || m.start() != start) return null;
            state.setPosition(m.end());
            return match(context, input, state, start, m);
        }

        @Override
        protected String matchString() {
            return pattern.pattern();
        }
        
        protected abstract Match match(Context<StringInput> context, StringInput input, S state, int start, MatchResult match);
    }
    
}
