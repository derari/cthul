package org.cthul.parser.lexer.simple;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.lexer.LexerBuilderBase;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.api.TokenMatcher;
import org.cthul.parser.lexer.simple.SimpleTokenLexer.Rule;
import org.cthul.parser.token.Token;
import org.cthul.parser.token.TokenStream;

/**
 * 
 * @param <Token> tokens produced by I, and by {@link InputEval}s
 * @param <I> input produced by lexer
 */
public class SimpleTokenLexerBuilder<T extends Token<?>> extends LexerBuilderBase<T, TokenStream<T>> {
    
    protected int nextRuleId = 0;
    protected final List<Rule<? extends T>> rules = new ArrayList<>();
    protected final Set<RuleKey> registeredMatchers = new HashSet<>();

    public SimpleTokenLexerBuilder() {
    }

    public SimpleTokenLexerBuilder(SimpleTokenLexerBuilder<T> source) {
        super(source);
        this.nextRuleId = source.nextRuleId;
        this.rules.addAll(source.rules);
        this.registeredMatchers.addAll(source.registeredMatchers);
    }
    
    protected int nextId() {
        return nextRuleId++;
    }

    @Override
    public void addStringToken(RuleKey key, InputEval<? extends T, ? super String> eval, String string) {
        addRule(key, newStringRule(key, nextId(), eval, string));
    }
    
    @Override
    public void addStringToken(RuleKey key, InputEval<? extends T, ? super String> eval, String... strings) {
        addRule(key, newStringRule(key, nextId(), eval, strings));
    }
    
    @Override
    public void addRegexToken(RuleKey key, InputEval<? extends T, ? super MatchResult> eval, Pattern pattern) {
        addRule(key, newRegexRule(key, nextId(), eval, pattern));
    }
    
    protected List<Rule<? extends T>> getRules() {
        return Collections.unmodifiableList(rules);
    }
    
    protected void addRule(RuleKey key, Rule rule) {
        rules.add(rule);
        registerMatcher(key);
    }

    protected void registerMatcher(RuleKey key) {
        if (registeredMatchers.add(key)) {
            addMatcher(createMatcher(key));
        }
    }

    @Override
    public SimpleTokenLexer<T> createLexer() {
        return new SimpleTokenLexer<>(getTokenMatchers(), rules);
    }

    protected Rule<? extends T> newStringRule(RuleKey key, int id, InputEval<? extends T, ? super String> eval, String string) {
        return newStringRule(key, id, eval, new String[]{string});
    }

    protected Rule<? extends T> newStringRule(RuleKey key, int id, InputEval<? extends T, ? super String> eval, String... strings) {
        return new SimpleTokenLexer.StringRule<>(key, id, eval, strings);
    }

    protected Rule<? extends T> newRegexRule(RuleKey key, int id, InputEval<? extends T, ? super MatchResult> eval, Pattern pattern) {
        return new SimpleTokenLexer.RegexRule<>(key, id, eval, pattern);
    }

    protected TokenMatcher<? super TokenStream<T>> createMatcher(RuleKey key) {
        return new org.cthul.parser.token.TokenMatcher(key);
    }

    @Override
    public SimpleTokenLexerBuilder<T> copy() {
        return new SimpleTokenLexerBuilder<>(this);
    }
     
}
