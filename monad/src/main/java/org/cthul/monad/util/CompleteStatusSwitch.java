package org.cthul.monad.util;

import java.util.function.Function;
import java.util.function.Predicate;
import org.cthul.monad.Result;
import org.cthul.monad.Status;

public class CompleteStatusSwitch<T> implements StatusSwitch.Step<T>{
    
    private final Result<T> result;

    public CompleteStatusSwitch(Result<T> result) {
        this.result = result;
    }

    @Override
    public Result<T> otherwise(Function<? super Result<T>, ? extends Result<T>> action) {
        return result;
    }

    @Override
    public Result<T> orKeep() {
        return result;
    }

    @Override
    public Step<T> ifStatus(Predicate<? super Status> status, Function<? super Result<T>, ? extends Result<T>> action) {
        return this;
    }
}
