package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.util.ExceptionType;
import org.cthul.monad.util.SafeStrings;

public abstract class AbstractErrorState<This extends ErrorState<This>, X extends Exception> implements ErrorState<This> {

    private X exception;
    private Supplier<? extends X> exceptionSource;
    private boolean handled = false;

    @SuppressWarnings("LeakingThisInConstructor")
    public AbstractErrorState(Unsafe<?, ? extends X> result) {
        if (result == null) throw new NullPointerException("result");
        this.exception = result.getException();
    }

    public AbstractErrorState(X exception) {
        if (exception == null) throw new NullPointerException("exception");
        this.exception = exception;
    }

    public AbstractErrorState(Supplier<? extends X> exceptionSource) {
        if (exceptionSource == null) throw new NullPointerException("exception");
        this.exceptionSource = exceptionSource;
    }

    public AbstractErrorState(ExceptionType<? extends X> exceptionType, Status status, String message, Object... args) {
        if (exceptionType == null) throw new NullPointerException("exceptionType");
        if (status == null) throw new NullPointerException("status");
        if (message == null) throw new NullPointerException("message");
        this.exceptionSource = () -> exceptionType.exception(status, message, args);
    }

    protected AbstractErrorState(AbstractErrorState<?, ? extends X> source) {
        this.exception = source.exception;
        this.exceptionSource = source.exceptionSource;
        this.handled = source.handled;
    }

    public X getException() {
        handled = true;
        if (exception == null) {
            exception = createException();
        }
        return exception;
    }

    protected X createException() {
        return exceptionSource.get();
    }

    protected void setException(X exception) {
        if (exception == null) throw new NullPointerException("exception");
        this.exception = exception;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    protected This self() {
        return (This) this;
    }

    public This handledOnce(ErrorHandler handler) {
        if (isHandled()) return self();
        return handledWith(handler);
    }

    public This handledWith(ErrorHandler handler) {
        try {
            ErrorState<?> state = handler.handle(this);
            return cast(state);
        } finally {
            setHandled(true);
        }
    }

    @Override
    public String toString() {
        return SafeStrings.toString(exception);
    }
}
