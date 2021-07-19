package org.cthul.monad.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import org.cthul.monad.DefaultStatus;
import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.ExceptionWrapper;
import org.cthul.monad.result.*;

public abstract class AbstractExceptionType<X extends Exception>
        extends AbstractExceptionWrapper<X>
        implements ExceptionType<X> {

    private final Class<X> exceptionClass;
    private final Scope scope;
    private final Status defaultStatus;
    private final Internalizer internalizer = new Internalizer(this::getInternalStatus);

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

    public X parseMessage(ResultMessage resultMessage) {
        Status status = Status.withDescription(resultMessage.getCode(), resultMessage.getStatus());
        String messageScope = resultMessage.getScope();
        if (messageScope == null || messageScope.isEmpty() || messageScope.equals(scope.getName())) {
            return exception(status, resultMessage.getMessage());
        }
        Scope adhocScope = resultMessage::getScope;
        return exception(adhocScope, status, resultMessage.getMessage(), null);
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

    @Override
    public X exception(Status status, String message, Throwable cause) {
        return exception(scope, status, message, cause);
    }

    protected abstract X exception(Scope scope, Status status, String message, Throwable cause);

    public Scope getScope() {
        return scope;
    }

    private Status getInternalStatus(Unsafe<?, ?> unsafe) {
        if (unsafe.getStatus().isInternal() && !getScope().sameScope(unsafe.getScope())) {
            return DefaultStatus.BAD_GATEWAY;
        }
        return DefaultStatus.INTERNAL_ERROR;
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

    public Internalizer internalize(Status status) {
        return new Internalizer(u -> status);
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
        if (unsafe.isPresent()) return false;
        Exception wrapped = unsafe.getException();
        return exceptionClass.isInstance(wrapped);
    }

    @Override
    public String toString() {
        return scope.toString() + "/" + exceptionClass.getSimpleName();
    }

    public class Internalizer extends AbstractExceptionWrapper<X> implements ExceptionType<X>, ExceptionWrapper<X> {

        private final Function<? super Unsafe<?,?>, ? extends Status> statusMapping;

        public Internalizer(Function<? super Unsafe<?, ?>, ? extends Status> statusMapping) {
            this.statusMapping = statusMapping;
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
            if (exception instanceof NoValue) {
                return wrapUnsafe((NoValue) exception);
            }
            return wrapUnsafe(new ExceptionResult<>(scope, DefaultStatus.BAD_REQUEST, exception));
        }

        @Override
        protected NoValue<X> wrapUnsafe(NoValue<?> unsafe) {
            Status status = statusMapping.apply(unsafe);
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
