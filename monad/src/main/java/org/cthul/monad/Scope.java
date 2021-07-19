package org.cthul.monad;

import java.util.NoSuchElementException;
import org.cthul.monad.function.ResultWrapper;
import org.cthul.monad.result.*;
import org.cthul.monad.util.ScopedResultWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Scope {

    String getName();

    default <T> ValueResult<T> value(Status status, T value) {
        return new OkValue<>(this, status, value);
    }

    default <T> ValueResult<T> value(T value) {
        return value(DefaultStatus.OK, value);
    }

    default <X extends Exception> NoValue<X> noValue(Status status, X exception) {
        return new ExceptionResult<>(this, status, exception);
    }

    default <X extends Exception> NoValue<X> noValue(X exception) {
        return noValue(DefaultStatus.NO_VALUE, exception);
    }

    default NoResult noValue(Status status) {
        return new RuntimeExceptionResult(this, status, new NoSuchElementException());
    }

    default NoResult okNoValue() {
        return noValue(DefaultStatus.NO_VALUE);
    }

    default <X extends Exception> NoValue<X> internal(X exception) {
        return noValue(DefaultStatus.INTERNAL_ERROR, exception);
    }

    default NoValue<?> parseMessage(ResultMessage resultMessage) {
        return ScopedException.withScope(this).parseMessage(resultMessage);
    }

    default boolean sameScope(Scope other) {
        return getName().equals(other.getName());
    }

    default Logger getLogger() {
        return LoggerFactory.getLogger(getName());
    }

    default ResultWrapper asResultWrapper() {
        return new ScopedResultWrapper(this);
    }

    default ResultWrapper.Safe safe() {
        return asResultWrapper().safe();
    }
}
