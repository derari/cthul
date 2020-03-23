package org.cthul.monad;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Result<T> extends Status.Delegate {
    
    Module getModule();
    
    Status getStatus();
    
    boolean hasValue();
    
    T getValue() throws ScopedException;
    
    ScopedException getException();
    
    ScopedRuntimeException getRuntimeException();
    
    UncheckedResult<T> unchecked();
    
    default <U> NoValue<U> noValue() {
        if (hasValue()) throw new IllegalArgumentException("has value");
        return (NoValue) this;
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
        return new ResultValue<>(getModule(), value);
    }

    default <U> Result<U> mapFlat(Function<? super T, ? extends Result<U>> mapping) {
        if (!hasValue()) {
            return noValue();
        }
        return mapping.apply(unchecked().getValue());
    }
    
    default T or(T defaultValue) {
        if (!hasValue()) {
            return defaultValue;
        }
        return unchecked().getValue();
    }
    
    default T orGet(Supplier<? extends T> defaultSupplier) {
        if (!hasValue()) {
            return defaultSupplier.get();
        }
        return unchecked().getValue();
    }
    
    default T orApply(Function<? super NoValue<? super T>, ? extends T> defaultFunction) {
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
    
    default Result<T> orElseApply(Function<? super NoValue<? super T>, ? extends Result<? extends T>> elseGet) {
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
}
