package org.cthul.parser.api;

import java.util.Collection;
import org.cthul.parser.grammar.api.InputMatch;

/**
 * An input whose elements are already {@link InputMatch}es.
 */
public class TokenInput<T extends InputMatch<?>> extends Input<T> {
    
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
    @SuppressWarnings("unchecked")
    public T get(int i) {
        return (T) tokens[i];
    }

    @Override
    public int startPosition(int i) {
        return get(i).getInputStart();
    }

    @Override
    public int endPosition(int i) {
        return get(i).getInputEnd();
    }
    
}
