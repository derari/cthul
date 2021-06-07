package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.CheckedConsumer;
import org.cthul.monad.util.ExceptionType;

public class GeneralWarning<X extends Exception> extends AbstractErrorState<GeneralWarning<X>, X> {
    
    private boolean acknowledged = false;

    public GeneralWarning(Unsafe<?, ? extends X> result) {
        super(result);
    }

    public GeneralWarning(X exception) {
        super(exception);
    }

    public GeneralWarning(Supplier<? extends X> exceptionSource) {
        super(exceptionSource);
    }

    public GeneralWarning(ExceptionType<? extends X> exceptionType, Status status, String message, Object... args) {
        super(exceptionType, status, message, args);
    }

    protected GeneralWarning(AbstractErrorState<?, ? extends X> source) {
        super(source);
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
    
    public <Y extends Exception> GeneralWarning<X> ifNotAcknowledged(CheckedConsumer<? super X, Y> consumer) throws Y {
        if (!isAcknowledged()) {
            consumer.accept(getException());
        }
        return this;
    }
    
    public <Y extends Exception> GeneralWarning<X> ifAcknowledged(CheckedConsumer<? super X, Y> consumer) throws Y {
        if (isAcknowledged()) {
            consumer.accept(getException());
        }
        return this;
    }
}
