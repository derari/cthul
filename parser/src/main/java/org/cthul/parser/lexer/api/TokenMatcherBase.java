package org.cthul.parser.lexer.api;

import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;

public abstract class TokenMatcherBase<I extends Input<?>> implements TokenMatcher<I> {

    protected final RuleKey key;

    public TokenMatcherBase(RuleKey key) {
        this.key = key;
    }

    @Override
    public RuleKey getKey() {
        return key;
    }
    
}
