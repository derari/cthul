package org.cthul.monad;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.cthul.monad.util.IncompleteStatusSwitch;
import org.cthul.monad.util.ScopedResult;
import org.cthul.monad.util.StatusSwitch;

public interface Result<T> extends ScopedResult<T>, StatusSwitch<T> {
    
    T getValue() throws ScopedException;

    @Override
    default Step<T> ifStatus(Predicate<? super Status> status, Function<? super Result<T>, ? extends Result<T>> action) {
        return new IncompleteStatusSwitch<>(this).ifStatus(status, action);
    }
    
    default void requireOk() throws ScopedException {
        if (isOk()) return;
        throw getException();
    }
    
    default <U> Result<U> noValue() {
        if (hasValue()) throw new IllegalArgumentException("has value");
        return (Result) this;
    }
    
    default Result<T> ifPresent(Consumer<? super T> action) {
        if (hasValue()) {
            action.accept(unchecked().getValue());
        }
        return this;
    }

    default <U> Result<U> map(Function<? super T, ? extends U> mapping) {
        if (!hasValue()) {
            return noValue();
        }
        U value = mapping.apply(unchecked().getValue());
        return getModule().value(value);
    }

    default <U> Result<U> mapFlat(Function<? super T, ? extends Result<U>> mapping) {
        if (!hasValue()) {
            return noValue();
        }
        return mapping.apply(unchecked().getValue());
    }

    default T orApply(Function<? super Result<? super T>, ? extends T> defaultFunction) {
        if (!hasValue()) {
            return defaultFunction.apply(noValue());
        }
        return unchecked().getValue();
    }
    
    default Result<T> orElse(Result<? extends T> elseResult) {
        if (!hasValue()) {
            return (Result) elseResult;
        }
        return this;
    }
    
    default Result<T> orElseGet(Supplier<? extends Result<? extends T>> elseGet) {
        if (!hasValue()) {
            return (Result) elseGet.get();
        }
        return this;
    }
    
    default Result<T> orElseApply(Function<? super Result<? super T>, ? extends Result<? extends T>> elseGet) {
        if (!hasValue()) {
            return (Result) elseGet.apply(noValue());
        }
        return this;
    }
    
    default Result<T> internalize(String msg, Object... args) {
        if (hasValue() || getStatus().isOk()) {
            return this;
        }
        Object[] allArgs = Arrays.copyOf(args, args.length + 1);
        allArgs[args.length - 1] = getException();
        return getModule().internalize(msg, allArgs).noValue();
    }
    
    interface Unchecked<T> extends ScopedResult<T> {
        
        T getValue() throws ScopedRuntimeException;
        
        default void requireOk() throws ScopedRuntimeException {
            if (isOk()) return;
            throw getRuntimeException();
        }
        
        Result<T> checked();

        @Override
        default Unchecked<T> unchecked() {
            return this;
        }
    }
}
