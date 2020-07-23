package org.cthul.monad;

import java.util.Optional;
import org.cthul.monad.error.ErrorState;
import org.cthul.monad.util.*;
import org.slf4j.Logger;

public class Scope extends GenericScopeBase<ScopedException> {

    private final Unchecked unchecked;
    
    public Scope(String name) {
        super(name);
        this.unchecked = new Unchecked();
    }

    public Scope(String name, Logger log) {
        super(name, log);
        this.unchecked = new Unchecked();
    }
    
    public Unchecked unchecked() {
        return unchecked;
    }
    
    @Override
    public <T> Result<T> value(T value) {
        return value(DefaultStatus.OK, value);
    }
    
    @Override
    public <T> Result<T> value(Status okStatus, T value) {
        return new OkValue<>(this, okStatus, value);
    }

    @Override
    public <T> Result<T> noValue(ScopedException exception) {
        return exception.noValue();
    }
    
    @Override
    public <T> Result<T> result(T value, Exception exception) {
        if (exception != null) {
            return wrapAsInternal(exception).noValue();
        }
        return value(value);
    }
    
    @Override
    public ScopedException exception(Status status, String message) {
        return newException(status, message);
    }
    
    @Override
    public ScopedException exception(Status status, String message, Throwable cause) {
        return newException(status, message, cause);
    }
    
    @Override
    public ScopedException exception(Status status, Throwable cause) {
        return newException(status, cause);
    }
    
    public ScopedException newException(Status status, String message) {
        return new ScopedException(this, status, message);
    }
    
    public ScopedException newException(Status status, String message, Throwable cause) {
        return new ScopedException(this, status, message, cause);
    }
    
    public ScopedException newException(Status status, Throwable cause) {
        return new ScopedException(this, status, cause);
    }
    
    public <T> Result<T> noValue(Status status, String message, Object... args) {
        return exception(status, message, args).noValue();
    }
    
    public <T> Result<T> noValue(Status status, String message) {
        return exception(status, message).noValue();
    }
    
    public <T> Result<T> noValue(Status status, String message, Throwable cause) {
        return exception(status, message, cause).noValue();
    }
    
    public <T> Result<T> noValue(Status status, Throwable cause) {
        return exception(status, cause).noValue();
    }
    
    public <T> Result<T> noValue(int code, String message, Object... args) {
        return exception(code, message, args).noValue();
    }
    
    public <T> Result<T> noValue(int code, String message) {
        return exception(code, message).noValue();
    }
    
    public <T> Result<T> notFound(String message) {
        return noValue(DefaultStatus.NOT_FOUND, message);
    }
    
    public <T> Result<T> notFound(String message, Object... args) {
        return noValue(DefaultStatus.NOT_FOUND, message, args);
    }
    
    public <T> Result<T> forbidden(String message) {
        return noValue(DefaultStatus.FORBIDDEN, message);
    }
    
    public <T> Result<T> forbidden(String message, Object... args) {
        return noValue(DefaultStatus.FORBIDDEN, message, args);
    }
    
    public <T> Result<T> internal(String message, Object... args) {
        return noValue(DefaultStatus.INTERNAL_ERROR, message, args);
    }
    
    public <T> Result<T> internal(Throwable cause) {
        return noValue(DefaultStatus.INTERNAL_ERROR, cause);
    }
    
    public <T> Result<T> internalize(String msg, Object... args) {
        return internalLoggedException(msg, args).noValue();
    }
    
    public <T> Result<T> internalize(Status status, String msg, Object... args) {
        return internalLoggedException(status, msg, args).noValue();
    }

    @Override
    public <X2> X2 initializeErrorState(X2 exception, ErrorState<?> state) {
        if (exception instanceof ScopedException) {
            ((ScopedException) exception).setErrorState(state);
        } else if (exception instanceof ScopedRuntimeException) {
            ((ScopedRuntimeException) exception).setErrorState(state);
        }
        return super.initializeErrorState(exception, state);
    }

    @Override
    public ScopedException asException(ScopedResult<?, ?> noValue) {
        if (noValue instanceof Result) {
            return ((Result<?>) noValue).getException();
        }
        if (noValue instanceof Result.Unchecked) {
            return ((Result.Unchecked<?>) noValue).checked().getException();
        }
        return super.asException(noValue);
    }

    public <T> Result<T> fromOptional(Optional<T> opt, String message, Object... args) {
        if (opt.isPresent()) {
            return value(opt.get());
        }
        return notFound(message, args).noValue();
    }
    
    public <T> Result<T> fromOptional(Optional<T> opt, String message) {
        if (opt.isPresent()) {
            return value(opt.get());
        }
        return notFound(message).noValue();
    }
    
    public <T> Result<T> parse(ResultMessage message) {
        return parseMessage(message).noValue();
    }
    
    @Override
    public boolean isSameScope(GenericScope<?> other) {
        return equals(other) || unchecked().equals(other);
    }
    
    public class Unchecked extends GenericScopeBase<ScopedRuntimeException> implements UncheckedScope<ScopedRuntimeException> {

        public Unchecked() {
            super(Scope.this.name, Scope.this.log);
        }

        @Override
        public <T> Result.Unchecked<T> value(T value) {
            return Scope.this.value(value).unchecked();
        }

        @Override
        public <T> Result.Unchecked<T> value(Status okStatus, T value) {
            return Scope.this.value(okStatus, value).unchecked();
        }

        @Override
        public <T> Result.Unchecked<T> noValue(ScopedRuntimeException exception) {
            return exception.checked().<T>noValue().unchecked();
        }

        @Override
        public <T> Result.Unchecked<T> result(T value, Exception exception) {
            return Scope.this.result(value, exception).unchecked();
        }

        @Override
        public ScopedRuntimeException exception(Status status, String message) {
            return Scope.this.exception(status, message).unchecked();
        }

        @Override
        public ScopedRuntimeException exception(Status status, String message, Throwable cause) {
            return Scope.this.exception(status, message, cause).unchecked();
        }

        @Override
        public ScopedRuntimeException exception(Status status, Throwable cause) {
            return Scope.this.exception(status, cause).unchecked();
        }

        @Override
        public boolean isSameScope(GenericScope<?> other) {
            return Scope.this.isSameScope(other);
        }
    }
}
