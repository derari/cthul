package org.cthul.monad.util;

import java.util.function.Predicate;
import org.cthul.monad.DefaultStatus;
import org.cthul.monad.Status;
import org.cthul.monad.error.Checked;

public interface StatusSwitch<T> {
    
    static <T> StatusSwitch.Initial<T> choose(T status, Status defaultStatus) {
        if (status instanceof Status) {
            return new IncompleteStatusSwitch<>((Status) status, status);
        }
        if (status instanceof Status.Delegate) {
            return new IncompleteStatusSwitch<>(((Status.Delegate) status).getStatus(), status);
        }
        return new IncompleteStatusSwitch<>(defaultStatus, status);
    }
    
    static <T extends Status> StatusSwitch.Initial<T> choose(T status) {
        return new IncompleteStatusSwitch<>(status, status);
    }
    
    StepBuilder<T, StatusSwitch.Step<T>> ifStatus(Predicate<? super Status> status);
    
    default StepBuilder<T, StatusSwitch.Step<T>> ifStatus(Status status) {
        return ifStatus(status::equals);
    }
    
    default StepBuilder<T, StatusSwitch.Step<T>> ifCode(int code) {
        return ifStatus(status -> status.getCode() == code);
    }
    
    default StepBuilder<T, StatusSwitch.Step<T>> ifOk() {
        return ifStatus(Status::isOk);
    }
    
    default StepBuilder<T, StatusSwitch.Step<T>> ifIllegal() {
        return ifStatus(Status::isIllegal);
    }
    
    default StepBuilder<T, StatusSwitch.Step<T>> ifNotFound() {
        return ifStatus(Status::isNotFound);
    }
    
    default StepBuilder<T, StatusSwitch.Step<T>> ifInternal() {
        return ifStatus(Status::isInternal);
    }
    
    interface Initial<T> extends StatusSwitch<T> {
        
        <U> StatusSwitch<U> withValue(U value);
    }
    
    interface StepBuilder<T, Result> {
        
        <X extends Exception> Result map(Checked.Function<? super T, ? extends T, X> action) throws X;
        
        default <X extends Exception> Result get(Checked.Supplier<? extends T, X> action) throws X {
            return map(t -> action.get());
        }
        
        default <X extends Exception> Result run(Checked.Runnable<X> action) throws X {
            return map(t -> { action.run(); return t; });
        }
    }
    
    interface Step<T> extends StatusSwitch<T> {
        
        StepBuilder<T, T> otherwise();
        
        T orKeep();
    }
}
