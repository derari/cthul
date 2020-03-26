package org.cthul.monad.util;

import java.util.function.Function;
import java.util.function.Predicate;
import org.cthul.monad.Result;
import org.cthul.monad.Status;

public interface StatusSwitch<T> {
    
    StatusSwitch.Step<T> ifStatus(Predicate<? super Status> status, Function<? super Result<T>, ? extends Result<T>> action);
    
    default StatusSwitch.Step<T> ifStatus(Status status, Function<? super Result<T>, ? extends Result<T>> action) {
        return ifStatus(status::equals, action);
    }
    
    default StatusSwitch.Step<T> ifCode(int code, Function<? super Result<T>, ? extends Result<T>> action) {
        return ifStatus(status -> status.getCode() == code, action);
    }
    
    default StatusSwitch.Step<T> ifOk(Function<? super Result<T>, ? extends Result<T>> action) {
        return ifStatus(Status::isOk, action);
    }
    
    default StatusSwitch.Step<T> ifIllegal(Function<? super Result<T>, ? extends Result<T>> action) {
        return ifStatus(Status::isIllegal, action);
    }
    
    default StatusSwitch.Step<T> ifNotFound(Function<? super Result<T>, ? extends Result<T>> action) {
        return ifStatus(Status::isNotFound, action);
    }
    
    default StatusSwitch.Step<T> ifInternal(Function<? super Result<T>, ? extends Result<T>> action) {
        return ifStatus(Status::isInternal, action);
    }
    
    interface Step<T> extends StatusSwitch<T> {

        Result<T> otherwise(Function<? super Result<T>, ? extends Result<T>> action);
        
        Result<T> orKeep();
    }
}
