package org.cthul.monad.util;

import org.cthul.monad.*;
import org.cthul.monad.adapt.ExceptionAdapter;
import org.cthul.monad.adapt.ExceptionWrapper;
import org.cthul.monad.result.*;
import org.cthul.monad.switches.BasicSwitch;

public abstract class AbstractExceptionType<X extends Exception> implements ExceptionType<X> {

    private final Class<X> exceptionClass;
    private final Scope scope;
    private final Status defaultStatus;
    private final ExceptionWrapper.ResultFactory<X> resultFactory;

    public AbstractExceptionType(Class<X> exceptionClass, Scope scope, Status defaultStatus) {
        this.exceptionClass = exceptionClass;
        this.scope = scope;
        this.defaultStatus = defaultStatus;
        this.resultFactory = new ResultFactory(new ResultAdapter(this::exceptionToNoValue));
    }

    public Class<X> getExceptionClass() {
        return exceptionClass;
    }

    public ExceptionWrapper.ResultFactory<X> resultFactory() {
        return resultFactory;
    }

    public X parseMessage(ResultMessage resultMessage) {
        Status status = Status.withDescription(resultMessage.getCode(), resultMessage.getStatus());
        String messageScope = resultMessage.getScope();
        if (messageScope == null || messageScope.isEmpty() || messageScope.equals(scope.getName())) {
            return exception(status, resultMessage.getMessage());
        }
        Scope adhocScope = resultMessage::getScope;
        return newException(adhocScope, status, resultMessage.getMessage(), null);
    }

    @Override
    public X exception(Status status, String message, Throwable cause) {
        return newException(scope, status, message, cause);
    }

    private NoValue<?> exceptionToNoValue(Exception exception) {
        if (exception instanceof NoValue) {
            return (NoValue) exception;
        }
        return scope.failed(defaultStatus, exception);
    }

    protected <T> Unsafe<T, X> wrap(Unsafe<T, ?> unsafe) {
        if (unsafe.isPresent()) return unsafe.unchecked().value().unsafe();
        Exception exception = unsafe.getException();
        if (exceptionClass.isInstance(exception)) {
            return (Unsafe) unsafe;
        }
        return noValue(scope, unsafe.noValue()).unsafe();
    }

    protected NoValue<X> noValue(Scope scope, NoValue<?> noValue) {
        Exception exception = noValue.getException();
        X x = newException(scope, noValue.getStatus(), exception.getMessage(), exception);
        if (x instanceof NoValue) {
            return (NoValue) x;
        }
        return new ExceptionResult<>(scope, noValue.getStatus(), x);
    }

    protected abstract X newException(Scope scope, Status status, String message, Throwable cause);

    public Scope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return scope.toString() + "/" + exceptionClass.getSimpleName();
    }

    protected ExceptionWrapper.ResultFactory<RuntimeException> unchecked(ResultFactory resultFactory) {
        return new ExceptionWrapper.ResultFactory<RuntimeException>() {
            @Override
            public NoValue<RuntimeException> failed(Exception exception) {
                return resultFactory.failed(exception).unchecked();
            }
            @Override
            public <T> ValueResult<T> value(T value) {
                return resultFactory.value(value);
            }
            @Override
            public NoValue<RuntimeException> noValue() {
                return resultFactory.noValue().unchecked();
            }
            @Override
            public BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, ExceptionAdapter, ExceptionWrapper.ResultFactory<RuntimeException>> adaptUnsafe() {
                return resultFactory.adaptUnsafe().mapResult(rf -> rf.unchecked()).asBasicSwitch();
            }
            @Override
            public ExceptionWrapper.ResultFactory<RuntimeException> unchecked() {
                return this;
            }
        };
    }

    public ExceptionAdapter internalize(Status status, String message) {
        return noValue -> exceptionToNoValue(internalize(status, message, noValue));
    }

    protected class ResultFactory implements ExceptionWrapper.ResultFactory<X> {

        protected final ResultAdapter resultAdapter;

        public ResultFactory(ResultAdapter resultAdapter) {
            this.resultAdapter = resultAdapter;
        }

        protected <T> Unsafe<T,X> adapt(Unsafe<T,?> unsafe) {
            unsafe = (Unsafe) resultAdapter.adaptUnsafe(unsafe);
            return wrap(unsafe);
        }

        @Override
        public NoValue<X> failed(Exception exception) {
            return wrap(resultAdapter.adaptException(exception)).noValue();
        }

        @Override
        public <T> ValueResult<T> value(T value) {
            return adapt(scope.value(value)).unchecked().value();
        }

        @Override
        public NoValue<X> noValue() {
            return failed(exception(DefaultStatus.NO_VALUE));
        }

        @Override
        public BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, ExceptionAdapter, ExceptionWrapper.ResultFactory<X>> adaptUnsafe() {
            return resultAdapter.chooseResult()
                    .<ExceptionWrapper.ResultFactory<X>>mapResult(ResultFactory::new)
                    .<ExceptionAdapter>mapTarget((unsafe, adapter) -> adapter.unsafe(unsafe))
                    .asBasicSwitch();
        }

        @Override
        public ExceptionWrapper.ResultFactory<RuntimeException> unchecked() {
            return AbstractExceptionType.this.unchecked(this);
        }
    }

    protected static <X> X noValue(Scope scope, Factory<X> factory) {
        return factory.newException(scope, DefaultStatus.NO_VALUE, null, null);
    }

    protected static <X> X noValue(ScopedFactory<X> factory) {
        return factory.newException(DefaultStatus.NO_VALUE, null, null);
    }

    public static interface Factory<X> {
        X newException(Scope scope, Status status, String message, Throwable cause);

        default ScopedFactory<X> withScope(Scope scope) {
            return (s, m, c) -> newException(scope, s, m, c);
        }
    }

    public static interface ScopedFactory<X> extends Factory<X> {
        X newException(Status status, String message, Throwable cause);

        @Override
        default X newException(Scope scope, Status status, String message, Throwable cause) {
            return newException(status, message, cause);
        }
    }
}
