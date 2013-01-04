package org.cthul.parser.api;

import java.util.Collection;

public class TokenInput<T> extends Input<T> {
    
    private final Object[] tokens;

    public TokenInput(T[] tokens) {
        this.tokens = tokens;
    }
    
    public TokenInput(Collection<? extends T> tokens) {
        this.tokens = tokens.toArray(new Object[tokens.size()]);
    }

    @Override
    public int getLength() {
        return tokens.length;
    }

    @Override
    public T get(int i) {
        return (T) tokens[i];
    }
    
}
