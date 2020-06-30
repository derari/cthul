package org.cthul.monad.util;

import java.util.function.Function;
import java.util.function.Predicate;
import org.cthul.monad.Status;

public class CompleteStatusSwitch<T> implements StatusSwitch.Step<T>{
    
    private final T result;

    public CompleteStatusSwitch(T result) {
        this.result = result;
    }

    @Override
    public T otherwise(Function<? super T, ? extends T> action) {
        return result;
    }

    @Override
    public T orKeep() {
        return result;
    }

    @Override
    public Step<T> ifStatus(Predicate<? super Status> status, Function<? super T, ? extends T> action) {
        return this;
    }

    @Override
    public Step<T> ifStatus(Predicate<? super Status> status, Runnable action) {
        return this;
    }
}
