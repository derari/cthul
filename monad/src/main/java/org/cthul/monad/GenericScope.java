package org.cthul.monad;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.DefaultStatus;
import org.cthul.monad.Status;
import org.cthul.monad.error.Checked;
import org.cthul.monad.error.ErrorState;

public interface GenericScope<X extends Exception> {
    
    <T> ScopedResult<T, X> value(T value);
    
    <T> ScopedResult<T, X> value(Status okStatus, T value);
    
    <T> ScopedResult<T, X> noValue(X exception);
    
    default <T> ScopedResult<T, X> noValue(Status status, String message) {
        return noValue(exception(status, message));
    }
    
    default <T> ScopedResult<T, X> noValue(Status status, String message, Object... args) {
        return noValue(exception(status, message, args));
    }
    
    default <T> ScopedResult<T, X> result(T value, Exception exception) {
        if (exception != null) {
            return noValue(wrapAsInternal(exception));
        }
        return value(value);
    }
    
    X exception(Status status, String message, Object... args);
    
    default <X2> X2 initializeErrorState(X2 exception, ErrorState<?> state) {
        return exception;
    }
    
    X exception(Status status, String message);
    
    X exception(Status status, String message, Throwable cause);
    
    X exception(Status status, Throwable cause);
    
    default X exception(int code, String message, Object... args) {
        return exception(DefaultStatus.fromCode(code), message, args);
    }
    
    default X exception(int code, String message) {
        return exception(DefaultStatus.fromCode(code), message);
    }
    
    default X internalLoggedException(String msg, Object... args) {
        return internalLoggedException(DefaultStatus.INTERNAL_ERROR, msg, args);
    }
    
    X internalLoggedException(Status status, String msg, Object... args);
    
    default X wrapAsInternal(Throwable throwable) {
        return wrap(DefaultStatus.INTERNAL_ERROR, throwable);
    }
    
    default X wrapAsGateway(Throwable throwable) {
        return wrap(DefaultStatus.BAD_GATEWAY, throwable);
    }
    
    default X wrap(Status defaultStatus, Throwable throwable) {
        if (throwable instanceof ScopedResult) {
            ScopedResult<?,?> result = (ScopedResult) throwable;
            if (!result.hasValue() && isSameScope(result.getScope())) {
                return asException(result);
            }
        }
        return exception(defaultStatus, throwable);
    }
    
    default Function<Throwable, X> wrapper(Status defaultStatus) {
        return throwable -> wrap(defaultStatus, throwable);
    }
    
    default boolean isSameScope(GenericScope<?> other) {
        return equals(other);
    }
    
    default X asException(ScopedResult<?,?> noValue) {
        return exception(noValue, noValue.getException());
    }
    
    X parseMessage(ResultMessage message);
    
    default Supplier<ScopedResult<Void, X>> safe(Checked.Runnable<?> runnable) {
        BiFunction<Void, Exception, ScopedResult<Void, X>> resultHandler = this::result;
        return runnable.safe(resultHandler);
    }
    
    default <T> Supplier<ScopedResult<T, X>> safe(Checked.Supplier<T, ?> supplier) {
        return supplier.safe(this::result);
    }
    
    default <T> Function<T, ScopedResult<Void, X>> safe(Checked.Consumer<T, ?> consumer) {
        BiFunction<Void, Exception, ScopedResult<Void, X>> resultHandler = this::result;
        return consumer.safe(resultHandler);
    }
}
