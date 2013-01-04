package org.cthul.parser.lexer.simple;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.TokenInput;
import org.cthul.parser.lexer.LexerBuilderBase;
import org.cthul.parser.lexer.api.InputEval;
import org.cthul.parser.lexer.api.TokenMatcher;
import org.cthul.parser.lexer.simple.SimpleLexer.Rule;

/**
 * 
 * @param <Token> tokens produced by I, and by {@link InputEval}s
 * @param <I> input produced by lexer
 */
public abstract class SimpleLexerBuilderBase<Token> extends LexerBuilderBase<Token, TokenInput<Token>> {
    
    private int ruleId = 0;
    private final List<SimpleLexer.Rule<? extends Token>> rules = new ArrayList<>();
    protected final Set<RuleKey> registeredMatchers = new HashSet<>();

    public SimpleLexerBuilderBase() {
    }

    public SimpleLexerBuilderBase(SimpleLexerBuilderBase<Token> source) {
        super(source);
        this.ruleId = source.ruleId;
        this.rules.addAll(source.rules);
        this.registeredMatchers.addAll(source.registeredMatchers);
    }

    @Override
    public SimpleLexer<Token> createLexer() {
        return new SimpleLexer<>(getTokenMatchers(), rules);
    }
    
    protected int nextId() {
        return ruleId++;
    }

    @Override
    public void addStringToken(RuleKey key, InputEval<? extends Token, String> eval, String string) {
        addRule(key, new SimpleLexer.StringRule<>(key, nextId(), eval, new String[]{string}));
    }

    @Override
    public void addStringToken(RuleKey key, InputEval<? extends Token, String> eval, String... strings) {
        addRule(key, new SimpleLexer.StringRule<>(key, nextId(), eval, strings));
    }

    @Override
    public void addRegexToken(RuleKey key, InputEval<? extends Token, MatchResult> eval, Pattern pattern) {
        addRule(key, new SimpleLexer.RegexRule<>(key, nextId(), eval, pattern));
    }

    protected List<Rule<? extends Token>> getRules() {
        return Collections.unmodifiableList(rules);
    }
    
    protected void addRule(RuleKey key, SimpleLexer.Rule<? extends Token> rule) {
        rules.add(rule);
        registerMatcher(key);
    }

    protected void registerMatcher(RuleKey key) {
        if (registeredMatchers.add(key)) {
            addMatcher(createMatcher(key));
        }
    }
    
    protected abstract TokenMatcher<? super TokenInput<Token>> createMatcher(RuleKey key);
    
}
