package org.cthul.monad;

public interface UncheckedResult<T> extends Result<T> {

    T getValue();

    @Override
    default UncheckedResult<T> unchecked() {
        return this;
    }
    
    Result<T> checked();
}
