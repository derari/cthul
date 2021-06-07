package org.cthul.monad.function;

public interface CheckedAutoClosable<X extends Exception> extends AutoCloseable {

    @Override
    void close() throws X;
}
