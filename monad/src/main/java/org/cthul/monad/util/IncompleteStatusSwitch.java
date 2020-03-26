package org.cthul.monad.util;

import java.util.function.Function;
import java.util.function.Predicate;
import org.cthul.monad.Result;
import org.cthul.monad.Status;

public class IncompleteStatusSwitch<T> implements StatusSwitch.Step<T>{
    
    private final Result<T> result;

    public IncompleteStatusSwitch(Result<T> result) {
        this.result = result;
    }

    @Override
    public Result<T> otherwise(Function<? super Result<T>, ? extends Result<T>> action) {
        return action.apply(result);
    }

    @Override
    public Result<T> orKeep() {
        return result;
    }

    @Override
    public Step<T> ifStatus(Predicate<? super Status> status, Function<? super Result<T>, ? extends Result<T>> action) {
        if (status.test(result)) {
            Result<T> next = action.apply(result);
            return new CompleteStatusSwitch<>(next);
        }
        return this;
    }
}
