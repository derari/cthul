package org.cthul.parser.lexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cthul.parser.api.Input;
import org.cthul.parser.lexer.api.TokenMatcher;

public abstract class LexerBuilderBase<Token, I extends Input<?>> implements LexerBuilder<Token, I> {
    
    private final List<TokenMatcher<? super I>> matchers = new ArrayList<>();
    private final List<TokenMatcher<? super I>> publicMatchers = Collections.unmodifiableList(matchers);

    public LexerBuilderBase() {
    }
    
    public LexerBuilderBase(LexerBuilderBase<Token, I> source) {
        this.matchers.addAll(source.matchers);
    }
    
    protected void addMatcher(TokenMatcher<? super I> m) {
        matchers.add(m);
    }

    public List<TokenMatcher<? super I>> getTokenMatchers() {
        return publicMatchers;
    }
    
}
