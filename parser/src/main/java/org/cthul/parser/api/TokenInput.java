package org.cthul.parser.api;

import java.util.Collection;
import org.cthul.parser.lexer.api.InputMatch;

/**
 * An input whose elements are already {@link InputMatch}es.
 */
public class TokenInput<T extends InputMatch<?>> extends Input<T> {
    
    protected final String inputString;
    private final Object[] tokens;

    public TokenInput(String inputString, Object[] tokens) {
        this.inputString = inputString;
        this.tokens = tokens;
    }
    
    public TokenInput(String inputString, Collection<? extends T> tokens) {
        this.inputString = inputString;
        this.tokens = tokens.toArray(new Object[tokens.size()]);
    }

    @Override
    public String asString() {
        return inputString;
    }

    @Override
    public int getLength() {
        return tokens.length;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int i) {
        return (T) tokens[i];
    }

    @Override
    public int startPosition(int i) {
        if (i >= getLength()) {
            return endPosition(i-1);
        }
        return get(i).getInputStart();
    }

    @Override
    public int endPosition(int i) {
        if (i >= getLength()) {
            return endPosition(i-1);
        }
        return get(i).getInputEnd();
    }
    
}
