package org.cthul.monad.util;

import java.util.function.Predicate;
import org.cthul.monad.Status;
import org.cthul.monad.error.Checked;

public class IncompleteStatusSwitch<T> implements StatusSwitch.Initial<T>, StatusSwitch.Step<T> {
    
    private final Status status;
    private final T value;

    public IncompleteStatusSwitch(Status status, T value) {
        this.status = status;
        this.value = value;
    }

    @Override
    public <U> StatusSwitch<U> withValue(U value) {
        return new IncompleteStatusSwitch<>(status, value);
    }

    @Override
    public <X extends Exception> T otherwise(Checked.Function<? super T, ? extends T, X> action) throws X {
        return action.apply(value);
    }

    @Override
    public T orKeep() {
        return value;
    }

    @Override
    public <X extends Exception> Step<T> ifStatus(Predicate<? super Status> status, Checked.Function<? super T, ? extends T, X> action) throws X {
        if (status.test(this.status)) {
            T next = action.apply(value);
            return new CompleteStatusSwitch<>(next);
        }
        return this;
    }
}
