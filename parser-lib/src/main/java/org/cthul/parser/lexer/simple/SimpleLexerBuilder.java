package org.cthul.parser.lexer.simple;

import java.util.*;
import java.util.regex.Pattern;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.lexer.LexerBuilderBase;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.api.InputMatcher;
import org.cthul.parser.lexer.simple.SimpleLexer.Rule;
import org.cthul.parser.lexer.simple.SimpleLexer.RuleSet;
import org.cthul.parser.lexer.simple.SimpleLexer.State;

/**
 * 
 * @param <Token> tokens produced by {@link InputEval}s
 * @param <Match> matching information provided by lexer
 * @param <I> input produced by lexer
 */
public abstract class SimpleLexerBuilder<Token, Match, I extends Input<?>> extends LexerBuilderBase<Token, Match, I> {
    
    protected final RuleSetBuilder<Token, Match, I, ?> ruleSetBuilder;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public SimpleLexerBuilder() {
        ruleSetBuilder = newRuleSetBuilder();
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    protected SimpleLexerBuilder(SimpleLexerBuilder<Token, Match, I> source) {
        super(source);
        ruleSetBuilder = newRuleSetBuilder();
        ruleSetBuilder.copyFrom(source.ruleSetBuilder);
    }

    @Override
    public SimpleLexer<? extends I> createLexer() {
        return new SimpleLexer<>(ruleSetBuilder.createRuleSet());
    }
    
    @Override
    public abstract SimpleLexerBuilder<Token, Match, I> copy();
     
    protected abstract RuleSetBuilder<Token, Match, I, ?> newRuleSetBuilder();
    
    protected static abstract class RuleSetBuilder<Token, Match, I extends Input<?>, S extends State<Token, I>> {
        
        protected final SimpleLexerBuilder<?, ?, I> lexerBuilder;
        protected final SortedSet<Rule<? extends Token, ? super S>> rules = new TreeSet<>();
        protected final Set<RuleKey> registeredMatchers = new HashSet<>();
        protected int nextRuleId = 0;

        public RuleSetBuilder(SimpleLexerBuilder<?, ?, I> lexerBuilder) {
            this.lexerBuilder = lexerBuilder;
        }
        
        @SuppressWarnings("unchecked")
        private void copyFrom(RuleSetBuilder<Token, Match, I, ?> source) {
            this.nextRuleId = source.nextRuleId;
            this.rules.addAll((List) source.rules);
            this.registeredMatchers.addAll(source.registeredMatchers);
        }
        
        public abstract RuleSet<Token, I, S> createRuleSet();
        
        protected void addRule(RuleKey key, Rule<? extends Token, ? super S> rule) {
            rules.add(rule);
            registerMatcher(key);
        }
        
        protected void registerMatcher(RuleKey key) {
            if (registeredMatchers.add(key)) {
                InputMatcher<? super I> im = createMatcher(key);
                if (im != null) {
                    lexerBuilder.addMatcher(im);
                }
            }
        }
        
        protected abstract InputMatcher<? super I> createMatcher(RuleKey key);
        
        protected int nextId() {
            return nextRuleId++;
        }

        public void addStringToken(RuleKey key, InputEval<? extends Token, ? super Match> eval, String string) {
            addRule(key, newStringRule(key, nextId(), eval, string));
        }
        
        public void addStringToken(RuleKey key, InputEval<? extends Token, ? super Match> eval, String... strings) {
            addRule(key, newStringRule(key, nextId(), eval, strings));
        }

        public void addRegexToken(RuleKey key, InputEval<? extends Token, ? super Match> eval, Pattern pattern) {
            addRule(key, newRegexRule(key, nextId(), eval, pattern));
        }
        
        protected abstract Rule<? extends Token, ? super S> newStringRule(RuleKey key, int id, InputEval<? extends Token, ? super Match> eval, String string);
        
        protected abstract Rule<? extends Token, ? super S> newStringRule(RuleKey key, int id, InputEval<? extends Token, ? super Match> eval, String... strings);
        
        protected abstract Rule<? extends Token, ? super S> newRegexRule(RuleKey key, int id, InputEval<? extends Token, ? super Match> eval, Pattern pattern);
    }
    
    @Override
    protected void addMatcher(InputMatcher<? super I> m) {
        super.addMatcher(m);
    }

    @Override
    public void addRegexToken(RuleKey key, InputEval<? extends Token, ? super Match> eval, Pattern pattern) {
        ruleSetBuilder.addRegexToken(key, eval, pattern);
    }

    @Override
    public void addStringToken(RuleKey key, InputEval<? extends Token, ? super Match> eval, String string) {
        ruleSetBuilder.addStringToken(key, eval, string);
    }

    @Override
    public void addStringToken(RuleKey key, InputEval<? extends Token, ? super Match> eval, String... strings) {
        ruleSetBuilder.addStringToken(key, eval, strings);
    }
 
}
