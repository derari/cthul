package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.ScopedResult;

public class GeneralWarning<X extends Exception> extends GeneralErrorState<GeneralWarning<X>, X> {
    
    private boolean acknowledged = false;

    public GeneralWarning(ScopedResult<?, ? extends X> result) {
        super(result);
    }

    public GeneralWarning(X exception) {
        super(exception);
    }

    public GeneralWarning(Supplier<? extends X> exceptionSupplier, String message) {
        super(exception(exceptionSupplier), message);
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }
    
    public GeneralWarning<X> acknowledge() {
        setAcknowledged(true);
        return this;
    }
    
    public void requireAcknowledgement() throws X {
        if (!isAcknowledged()) {
            throw getException();
        }
    }
    
    public <Y extends Exception> GeneralWarning<X> ifNotAcknowledged(Checked.Consumer<? super X, Y> consumer) throws Y {
        if (!isAcknowledged()) {
            consumer.accept(getException());
        }
        return this;
    }
    
    public <Y extends Exception> GeneralWarning<X> ifAcknowledged(Checked.Consumer<? super X, Y> consumer) throws Y {
        if (isAcknowledged()) {
            consumer.accept(getException());
        }
        return this;
    }
}
