package org.cthul.monad.error;

import java.util.function.Function;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.cthul.monad.DefaultStatus;
import org.cthul.monad.GenericScope;
import org.cthul.monad.ScopedResult;

public abstract class GeneralErrorState<This extends ErrorState<This>, X extends Exception> implements ErrorState<This> {
    
    private X exception;
    private Function<? super ErrorState<?>, ? extends @NonNull X> exceptionSource;
    private boolean handled = false;
    private final String message;

    @SuppressWarnings("LeakingThisInConstructor")
    public GeneralErrorState(@NonNull ScopedResult<?, ? extends X> result) {
        if (result == null) throw new NullPointerException("result");
        if (result.hasValue()) throw new IllegalArgumentException("cause is not an excpetion");
        this.exception = result.getScope().initializeErrorState(result.getException(), this);
        this.message = exception.getMessage();
    }

    public GeneralErrorState(@NonNull X exception) {
        if (exception == null) throw new NullPointerException("exception");
        this.exception = exception;
        this.message = exception.getMessage();
    }

    public GeneralErrorState(@NonNull Function<? super ErrorState<?>, ? extends X> exceptionSource, @NonNull String message) {
        if (exceptionSource == null) throw new NullPointerException("exception");
        if (message == null) throw new NullPointerException("message");
        this.exceptionSource = exceptionSource;
        this.message = message;
    }
    
    protected GeneralErrorState(@NonNull GeneralErrorState<?, ? extends X> source) {
        this.exception = source.exception;
        this.exceptionSource = source.exceptionSource;
        this.handled = source.handled;
        this.message = source.message;
    }
    
    public @NonNull X getException() {
        handled = true;
        if (exception == null) {
            exception = createException();
        }
        return exception;
    }

    public String getMessage() {
        return message;
    }
    
    protected @NonNull X createException() {
        return exceptionSource.apply(this);
    }

    protected void setException(@NonNull X exception) {
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
    
    public This handledOnce(@NonNull ErrorHandler handler) {
        if (isHandled()) return self();
        return handledWith(handler);
    }
    
    public This handledWith(@NonNull ErrorHandler handler) {
        try {
            ErrorState<?> state = handler.handle(this);
            return cast(state);
        } finally {
            setHandled(true);
        }
    }
    
    @Override
    public String toString() {
        return getMessage();
    }
    
    protected static <X extends Exception> Function<ErrorState<?>, X> exception(GenericScope<X> scope) {
        return state -> {
            X exception = scope.exception(DefaultStatus.UNPROCESSABLE, "%s", state);
            return scope.initializeErrorState(exception, state);
        };
    }

    protected static <X extends Exception> Function<ErrorState<?>, X> exception(ScopedResult<?, X> result) {
        return state -> result.getScope().initializeErrorState(result.getException(), state);
    }

    protected static <X extends Exception> Function<ErrorState<?>, X> exception(Supplier<X> exceptionSupplier) {
        return state -> exceptionSupplier.get();
    }

    protected static <X extends Exception> Function<ErrorState<?>, X> exception(X exception) {
        return state -> exception;
    }
}
