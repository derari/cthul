package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.util.ScopedResult;

public abstract class AbstractErrorState<This extends ErrorState<This>, X extends Exception> implements ErrorState<This> {
    
    private X exception;
    private Supplier<? extends X> exceptionSource;
    private boolean handled = false;

    @SuppressWarnings("LeakingThisInConstructor")
    public AbstractErrorState(ScopedResult<?, ? extends X> result) {
        if (result == null) throw new NullPointerException("result");
        if (result.hasValue()) throw new IllegalArgumentException("cause is not an excpetion");
        this.exception = result.getScope().initializeErrorState(result.getException(), this);
    }

    public AbstractErrorState(X exception) {
        if (exception == null) throw new NullPointerException("exception");
        this.exception = exception;
    }

    public AbstractErrorState(Supplier<? extends X> exceptionSource) {
        if (exceptionSource == null) throw new NullPointerException("exception");
        this.exceptionSource = exceptionSource;
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
    
    @Override
    public String toString() {
        return ""+ exception;
    }
}
