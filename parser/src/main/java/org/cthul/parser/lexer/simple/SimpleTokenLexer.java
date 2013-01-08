package org.cthul.parser.lexer.simple;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.lexer.LexerBase;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.api.TokenMatcher;
import org.cthul.parser.token.TokenBuilder.GroupProvider;
import org.cthul.parser.token.*;
import org.cthul.parser.token.simple.StringToken;

/**
 * 
 * @param <Token> tokens produced by input that is produced by this lexer
 */
public class SimpleTokenLexer<T extends Token<?>> extends LexerBase<TokenStream<T>> {

    private final List<Rule<? extends T>> rules = new ArrayList<>();

    public SimpleTokenLexer(
                        Collection<? extends TokenMatcher<? super TokenStream<T>>> matchers,
                        Collection<? extends Rule<? extends T>> rules) {
        super(matchers);
        this.rules.addAll(rules);
        Collections.sort(this.rules);
    }

    @Override
    public TokenStream<T> scan(Context<StringInput> context) {
        final Counter position = new Counter();
        final Counter defaultIndex = new Counter();
        final Counter inputIndex = new Counter();
        final String input = context.getInput().getString();
        final GroupProviderImpl gp = new GroupProviderImpl();
        final TokenBuilderImpl tb = new TokenBuilderImpl(input, StringToken.FACTORY, defaultIndex, inputIndex, gp);
        final List<T> tokens = new ArrayList<>();
        final int len = input.length();
        
        while (position.value < len) {
            for (Rule<? extends T> r: rules) {
                tb.prepareNextToken(position.value);
                T t = r.scan(context, gp, tb, position);
                if (t != null) {
                    inputIndex.value++;
                    if (t.getChannel() == TokenChannel.Default)
                        defaultIndex.value++;
                    position.value = t.getInputEnd();
                    tokens.add(t);
                    break;
                }
            }
        }
        return new TokenStream<>(tokens);
    }
    
    protected static class GroupProviderImpl implements GroupProvider {
        
        protected MatchResult match;
        protected String string;

        public void setMatch(MatchResult match) {
            this.match = match;
            string = null;
        }

        public void setString(String string) {
            this.string = string;
            match = null;
        }

        @Override
        public String group(int i) {
            if (match != null) {
                return match.group(i);
            } else if (i == 0) {
                return string;
            } else {
                throw new IndexOutOfBoundsException(String.valueOf(i));
            }
        }

        @Override
        public int count() {
            if (match != null) {
                return match.groupCount();
            } else {
                return 0;
            }
        }
        
    }
    
    protected static class TokenBuilderImpl<V, T extends Token<V>> extends AbstractTokenBuilder<V, T> {

        private final Counter index;
        private final Counter inputIndex;
        
        public TokenBuilderImpl(String input, TokenFactory<? super V, ? extends T> defaultFactory, Counter index, Counter inputIndex, GroupProvider groupProvider) {
            super(input, defaultFactory, groupProvider);
            this.index = index;
            this.inputIndex = inputIndex;
        }
        
        void prepareNextToken(int position) {
            setMatch(position, position);
            setKey(null);
        }
        
        @Override
        public int getDefaultIndex() {
            return index.value;
        }

        @Override
        public int getInputIndex() {
            return inputIndex.value;
        }

    }
    
    protected static class Counter {
        int value = 0;
    }
    
    protected static abstract class Rule<Token> implements Comparable<Rule<?>> {
        
        private final RuleKey key;
        private final int id;

        public Rule(RuleKey key, int id) {
            this.key = key;
            this.id = id;
        }

        public abstract Token scan(Context<StringInput> context, GroupProviderImpl gp, TokenBuilderImpl tb, Counter position);

        @Override
        public int compareTo(Rule<?> o) {
            int c = key.getPriority() - o.key.getPriority();
            if (c != 0) return c;
            return id - o.id;
        }
        
    }
    
    protected static abstract class MatchingRule<Token, Match> extends Rule<Token> {

        private final InputEval<? extends Token, ? super Match> eval;

        public MatchingRule(RuleKey key, int id, InputEval<? extends Token, ? super Match> eval) {
            super(key, id);
            this.eval = eval;
        }

        @Override
        public Token scan(Context<StringInput> context, GroupProviderImpl gp, TokenBuilderImpl tb, Counter position) {
            int start = position.value;
            Match m = match(context, gp, tb, position);
            if (m == null) return null;
            return eval.eval(context, m, start, position.value);
        }

        protected abstract Match match(Context<StringInput> context, GroupProviderImpl gp, TokenBuilderImpl tb, Counter p);
        
    }
    
    protected static class StringRule<Token> extends MatchingRule<Token, String> {

        private final String[] strings;

        public StringRule(RuleKey key, int id, InputEval<? extends Token, ? super String> eval, String[] strings) {
            super(key, id, eval);
            this.strings = strings;
        }
        
        @Override
        protected String match(Context<StringInput> context, GroupProviderImpl gp, TokenBuilderImpl tb, Counter p) {
            final int start = p.value;
            final String input = context.getInput().getString();
            for (String s: strings) {
                if (input.startsWith(s, start)) {
                    p.value += s.length();
                    gp.setString(s);
                    tb.setMatch(start, p.value);
                    return s;
                }
            }
            return null;
        }
    }
    
    protected static class RegexRule<Token> extends MatchingRule<Token, MatchResult> {

        private final Pattern pattern;

        public RegexRule(RuleKey key, int id, InputEval<? extends Token, ? super MatchResult> eval, Pattern pattern) {
            super(key, id, eval);
            this.pattern = pattern;
        }
        
        @Override
        protected MatchResult match(Context<StringInput> context, GroupProviderImpl gp, TokenBuilderImpl tb, Counter p) {
            final int start = p.value;
            final String input = context.getInput().getString();
            Matcher m = pattern.matcher(input);
            if (!m.find(start) || m.start() != start) return null;
            gp.setMatch(m);
            tb.setMatch(start, m.end());
            return m;
        }
    }
    
}
