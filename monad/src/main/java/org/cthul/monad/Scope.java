package org.cthul.monad;

import org.cthul.monad.result.NoValueFactory;
import java.util.Optional;
import org.cthul.monad.adapt.ResultWrapper;
import org.cthul.monad.adapt.UnsafeAdapter;
import org.cthul.monad.result.*;
import org.cthul.monad.switches.BasicSwitch;
import org.cthul.monad.util.*;
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

    default <X extends Exception> NoValue<X> failed(Status status, X exception) {
        return new ExceptionResult(this, status, exception);
    }

    default NoResult noValue() {
        return noResult(DefaultStatus.NO_VALUE, null, null);
    }

    default NoResult noResult(Status status, String message, Throwable cause) {
        return unchecked().noResult(status, message, cause);
    }

    default <T> Result<T> fromOptional(Optional<T> optional, String message, Object... args) {
        return optional
                .<Result<T>>map(this::value)
                .orElseGet(() -> unchecked().noValue(message, args).result());
    }

    default NoValueFactory<? extends NoResult> unchecked() {
        return new NoResultFactory(this);
    }

    default ExceptionType<? extends RuntimeException> uncheckedException() {
        return (s, m, c) -> unchecked().noResult(s, m, c).getException();
    }

    default <X extends Exception> NoValue<X> internal(X exception) {
        return failed(DefaultStatus.INTERNAL_ERROR, exception);
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

    default ResultWrapper overrideScope() {
        return new ScopedResultWrapper(this, true);
    }

    default ResultWrapper asDefaultScope() {
        return new ScopedResultWrapper(this, false);
    }

    default ResultWrapper overrideScope(UnsafeAdapter adapter) {
        return chooseResult(true).orElse().set(adapter);
    }

    default ResultWrapper asDefaultScope(UnsafeAdapter adapter) {
        return chooseResult(false).orElse().set(adapter);
    }

    default BasicSwitch<Unsafe<?,?>, Unsafe<?,?>, UnsafeAdapter, ? extends ResultWrapper> chooseResult() {
        return chooseResult(false);
    }

    default BasicSwitch<Unsafe<?,?>, Unsafe<?,?>, UnsafeAdapter, ? extends ResultWrapper> chooseResult(boolean forceScope) {
        return new ScopedResultWrapper(this, forceScope).adaptResult();
    }

    default BasicSwitch<NoValue<?>, NoValue<?>, Status, ? extends ResultWrapper> overrideScopeAndStatus() {
        return overrideScope().chooseStatus()
                .ifTrue(u -> sameScope(u.getScope())).map(Unsafe::getStatus);
    }

    default ResultWrapper.Safe safe() {
        return asDefaultScope().safe();
    }
}
