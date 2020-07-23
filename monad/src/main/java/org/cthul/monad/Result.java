package org.cthul.monad;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.cthul.monad.cache.Cached;
import org.cthul.monad.cache.CachedMeta;
import org.cthul.monad.error.Checked;
import org.cthul.monad.util.ScopedResult;
import org.cthul.monad.util.StatusSwitch;

public interface Result<T> extends ScopedResult<T, ScopedException>, StatusSwitch<Result<T>> {

    @Override
    Scope getScope();
    
    Cached<T> cacheControl(CachedMeta meta);
    
    Unchecked<T> unchecked();

    @Override
    default <X extends Exception> StatusSwitch.Step<Result<T>> ifStatus(Predicate<? super Status> status, Checked.Function<? super Result<T>, ? extends Result<T>, X> action) throws X {
        return StatusSwitch.choose(this).ifStatus(status, action);
    }
    
    default <U> StatusSwitch<U> chooseWithValue(U value) {
        return StatusSwitch.choose(this).withValue(value);
    }
    
    default <U> Result<U> noValue() {
        if (hasValue()) throw new IllegalArgumentException("has value");
        return (Result) this;
    }
    
    default Result<T> ifPresent(Consumer<? super T> action) {
        if (hasValue()) {
            action.accept(unchecked().get());
        }
        return this;
    }

    default <U> Result<U> map(Function<? super T, ? extends U> mapping) {
        if (!hasValue()) {
            return noValue();
        }
        U value = mapping.apply(unchecked().get());
        return getScope().value(value);
    }

    default <U> Result<U> mapFlat(Function<? super T, ? extends Result<U>> mapping) {
        if (!hasValue()) {
            return noValue();
        }
        return mapping.apply(unchecked().get());
    }

    default T orApply(Function<? super Result<? super T>, ? extends T> defaultFunction) {
        if (!hasValue()) {
            return defaultFunction.apply(noValue());
        }
        return unchecked().get();
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
        return internalize(DefaultStatus.INTERNAL_ERROR, msg, args);
    }
    
    default Result<T> internalize(Status status, String msg, Object... args) {
        if (hasValue() || getStatus().isOk()) {
            return this;
        }
        Object[] allArgs = Arrays.copyOf(args, args.length + 1);
        allArgs[args.length] = getException();
        return getScope().internalize(status, msg, allArgs).noValue();
    }
    
    default <X extends ScopedException> Result<T> throwIf(Class<X> type) throws X {
        if (hasValue()) {
            return this;
        }
        if (type.isInstance(getException())) {
            throw (X) getException();
        }
        return this;
    }
    
    interface Unchecked<T> extends ScopedResult<T, ScopedRuntimeException> {
        
        Result<T> checked();

        @Override
        default Scope getScope() {
            return checked().getScope();
        }
        
        @Override
        default Status getStatus() {
            return checked().getStatus();
        }
        
        default <X extends ScopedRuntimeException> Result.Unchecked<T> throwIf(Class<X> type) throws X {
            if (hasValue()) {
                return this;
            }
            if (type.isInstance(getException())) {
                throw (X) getException();
            }
            return this;
        }
    }
}
