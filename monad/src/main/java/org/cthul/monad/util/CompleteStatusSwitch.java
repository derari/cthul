package org.cthul.monad.util;

import java.util.function.Predicate;
import org.cthul.monad.Status;
import org.cthul.monad.error.Checked;

public class CompleteStatusSwitch<T> implements StatusSwitch.Step<T>{
    
    private final T result;

    public CompleteStatusSwitch(T result) {
        this.result = result;
    }

    @Override
    public <X extends Exception> T otherwise(Checked.Function<? super T, ? extends T, X> action) throws X {
        return result;
    }

    @Override
    public T orKeep() {
        return result;
    }

    @Override
    public <X extends Exception> Step<T> ifStatus(Predicate<? super Status> status, Checked.Function<? super T, ? extends T, X> action) throws X {
        return this;
    }
}