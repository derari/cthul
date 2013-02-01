package org.cthul.parser.lexer.api;

import org.cthul.parser.api.Input;
import org.cthul.parser.api.RuleKey;

public abstract class InputMatcherBase<I extends Input<?>> implements InputMatcher<I> {

    protected final RuleKey key;

    public InputMatcherBase(RuleKey key) {
        this.key = key;
    }

    @Override
    public RuleKey getKey() {
        return key;
    }
    
}
