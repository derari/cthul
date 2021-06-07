package org.cthul.monad.util;

import java.util.concurrent.ThreadLocalRandom;
import org.cthul.monad.DefaultStatus;
import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.ExceptionWrapper;
import org.cthul.monad.result.ExceptionResult;
import org.cthul.monad.result.NoResult;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.result.ValueResult;

public abstract class AbstractExceptionType<X extends Exception> 
        extends AbstractExceptionWrapper<X>
        implements ExceptionType<X> {
    
    private final Class<X> exceptionClass;
    protected final Scope scope;
    private final Status defaultStatus;
    private final Internalizer internalizer = new Internalizer(DefaultStatus.INTERNAL_ERROR);
    private final Internalizer internalizerGateway = new Internalizer(DefaultStatus.BAD_GATEWAY);

    public AbstractExceptionType(Class<X> exceptionClass, Scope scope, Status defaultStatus) {
        this.exceptionClass = exceptionClass;
        this.scope = scope;
        this.defaultStatus = defaultStatus;
    }

    public <T> ValueResult<T> value(T value) {
        return scope.value(value);
    }
    
    public NoResult okNoValue() {
        return scope.okNoValue();
    }

    @Override
    protected NoValue<X> wrapException(Exception exception) {
        if (isValidWrapper(exception)) {
            return (NoValue<X>) exception;
        }
        X x = cast(exception);
        if (isValidWrapper(x)) {
            return (NoValue<X>) x;
        }
        return new ExceptionResult<>(scope, defaultStatus, x);
    }

    @Override
    protected NoValue<X> wrapUnsafe(NoValue<?> unsafe) {
        Exception exception = unsafe.getException();
        if (isValidWrapper(exception)) {
            return (NoValue<X>) exception;
        }
        X x = cast(exception);
        if (isValidWrapper(x)) {
            return (NoValue<X>) x;
        }
        return new ExceptionResult<>(scope, unsafe.getStatus(), x);
    }
    
    public <T> Unsafe<T, X> internalize(Unsafe<T, ?> unsafe) {
        if (unsafe.isPresent()) {
            return unsafe.unchecked().value();
        }
        return internalize(unsafe.noValue()).asUnsafe();
    }
    
    public NoValue<X> internalize(NoValue<?> unsafe) {
        int statusCode = scope.sameScope(unsafe.getScope()) ? 500 : 502;
        Status status = Status.withCode(statusCode);
        return internalize(status, unsafe, unsafe.getException());
    }
    
    public NoValue<X> internalize(Exception exception) {
        return internalize(DefaultStatus.INTERNAL_ERROR, exception);
    }
    
    public NoValue<X> internalize(Status status, Exception exception) {
        String message = exception.getMessage();
        return internalize(status, message != null ? message : exception, exception);
    }

    public Internalizer internalize() {
        return internalizer;
    }

    public Internalizer internalizeGateway() {
        return internalizerGateway;
    }

    public Internalizer internalize(Status status) {
        if (status == DefaultStatus.INTERNAL_ERROR) return internalize();
        if (status == DefaultStatus.BAD_GATEWAY) return internalizeGateway();
        return new Internalizer(status);
    }
    
    public NoValue<X> internalize(Status status, Object message, Exception exception) {
        long randomId = ThreadLocalRandom.current().nextLong();
        String randomCode = Long.toHexString(randomId);
        scope.getLogger().info("Internal error ({}): {}", randomCode, message, exception);
        return wrapException(exception(status, "Internal error (%s)", randomCode));
    }
    
    private X cast(Exception exception) {
        if (exceptionClass.isInstance(exception)) {
            return (X) exception;
        }
        return exception(defaultStatus, exception);
    }
    
    protected boolean isValidWrapper(Exception exception) {
        if (!(exception instanceof Unsafe)) return false;
        Unsafe<?,?> unsafe = (Unsafe) exception;
        if (!unsafe.isPresent()) return false;
        Exception wrapped = unsafe.getException();
        return exceptionClass.isInstance(wrapped);
    }

    @Override
    public String toString() {
        return scope.toString() + "/" + exceptionClass.getSimpleName();
    }
    
    public class Internalizer extends AbstractExceptionWrapper<X> implements ExceptionType<X>, ExceptionWrapper<X> {

        private final Status status;

        public Internalizer(Status status) {
            this.status = status;
        }
        
        @Override
        public <T> ValueResult<T> value(T value) {
            return AbstractExceptionType.this.value(value);
        }

        @Override
        public NoResult okNoValue() {
            return AbstractExceptionType.this.okNoValue();
        }

        @Override
        protected NoValue<X> wrapException(Exception exception) {
            return AbstractExceptionType.this.internalize(status, exception);
        }

        @Override
        protected NoValue<X> wrapUnsafe(NoValue<?> unsafe) {
            return AbstractExceptionType.this.internalize(status, unsafe.getException());
        }

        @Override
        public X exception(Status status, String message, Throwable cause) {
            return wrapException(AbstractExceptionType.this.exception(status, message, cause)).getException();
        }

        @Override
        public NoValue<X> failed(Exception exception) {
            return wrapException(exception);
        }
    }
}
