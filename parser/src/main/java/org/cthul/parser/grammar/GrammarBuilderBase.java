package org.cthul.parser.grammar;

import java.util.List;
import org.cthul.parser.api.Input;
import org.cthul.parser.lexer.api.TokenMatcher;

public abstract class GrammarBuilderBase<I extends Input<?>> implements GrammarBuilder<I> {
    
    private boolean tokenMatchersSet = false;

    public GrammarBuilderBase() {
    }
    
    public GrammarBuilderBase(GrammarBuilderBase<I> source) {
        this.tokenMatchersSet = source.tokenMatchersSet;
    }

    @Override
    public void setTokenMatchers(List<TokenMatcher<? super I>> tokens) {
        if (tokenMatchersSet) {
            throw new IllegalStateException("Token matchers already set.");
        }
        tokenMatchersSet = true;
        generateTokenRules(tokens);
    }
    
    protected abstract void generateTokenRules(List<TokenMatcher<? super I>> tokenMatchers);
    
}
