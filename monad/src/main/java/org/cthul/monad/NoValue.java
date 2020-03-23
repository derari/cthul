package org.cthul.monad;

public interface NoValue<T> extends Result<T> {
    
    @Override
    default boolean hasValue() {
        return false;
    }
    
    @Override
    default T getValue() throws ScopedException {
        throw getException();
    }
}
