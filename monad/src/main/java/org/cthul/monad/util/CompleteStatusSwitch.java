package org.cthul.monad.util;

import java.util.function.Predicate;
import org.cthul.monad.Status;
import org.cthul.monad.error.Checked;

public class CompleteStatusSwitch<T> implements StatusSwitch.Step<T>, StatusSwitch.StepBuilder<T, StatusSwitch.Step<T>> {
    
    private final T result;

    public CompleteStatusSwitch(T result) {
        this.result = result;
    }

    @Override
    public T orKeep() {
        return result;
    }

    @Override
    @SuppressWarnings("Convert2Lambda")
    public StepBuilder<T, T> otherwise() {
        return new StepBuilder<T, T>() {
            @Override
            public <X extends Exception> T map(Checked.Function<? super T, ? extends T, X> action) throws X {
                return result;
            }
        };
    }

    @Override
    public StepBuilder<T, Step<T>> ifStatus(Predicate<? super Status> status) {
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