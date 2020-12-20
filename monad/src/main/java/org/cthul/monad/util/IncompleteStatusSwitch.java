package org.cthul.monad.util;

import java.util.function.Predicate;
import org.cthul.monad.Status;
import org.cthul.monad.error.Checked;

public class IncompleteStatusSwitch<T> implements StatusSwitch.Initial<T>, StatusSwitch.Step<T>, StatusSwitch.StepBuilder<T, StatusSwitch.Step<T>> {
    
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
    public T orKeep() {
        return value;
    }

    @Override
    @SuppressWarnings("Convert2Lambda")
    public StepBuilder<T, T> otherwise() {
        return new StepBuilder<T, T>() {
            @Override
            public <X extends Exception> T map(Checked.Function<? super T, ? extends T, X> action) throws X {
                return action.apply(value);
            }
        };
    }

    @Override
    @SuppressWarnings("Convert2Lambda")
    public StepBuilder<T, Step<T>> ifStatus(Predicate<? super Status> predicate) {
        if (predicate.test(status)) {
            return new StepBuilder<T, Step<T>>() {
                @Override
                public <X extends Exception> Step<T> map(Checked.Function<? super T, ? extends T, X> action) throws X {
                    T newValue = action.apply(value);
                    return new CompleteStatusSwitch<>(newValue);
                }
            };
        }
        return this;
    }

    @Override
    public <X extends Exception> Step<T> map(Checked.Function<? super T, ? extends T, X> action) throws X {
        return this;
    }

    @Override
    public <X extends Exception> Step<T> get(Checked.Supplier<? extends T, X> action) throws X {
        return this;
    }

    @Override
    public <X extends Exception> Step<T> run(Checked.Runnable<X> action) throws X {
        return this;
    }
}
