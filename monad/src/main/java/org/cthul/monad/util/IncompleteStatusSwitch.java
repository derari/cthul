package org.cthul.monad.util;

import java.util.function.Function;
import java.util.function.Predicate;
import org.cthul.monad.Status;

public class IncompleteStatusSwitch<T> implements StatusSwitch.Step<T>{
    
    private final Status status;
    private final T value;

    public IncompleteStatusSwitch(Status status, T value) {
        this.status = status;
        this.value = value;
    }

    @Override
    public T otherwise(Function<? super T, ? extends T> action) {
        return action.apply(value);
    }

    @Override
    public T orKeep() {
        return value;
    }

    @Override
    public Step<T> ifStatus(Predicate<? super Status> status, Function<? super T, ? extends T> action) {
        if (status.test(this.status)) {
            T next = action.apply(value);
            return new CompleteStatusSwitch<>(next);
        }
        return this;
    }

    @Override
    public Step<T> ifStatus(Predicate<? super Status> status, Runnable action) {
        if (status.test(this.status)) {
            action.run();
            return new CompleteStatusSwitch<>(orKeep());
        }
        return this;
    }
}
