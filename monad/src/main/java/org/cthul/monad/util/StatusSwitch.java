package org.cthul.monad.util;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.cthul.monad.Status;

public interface StatusSwitch<T> {
    
    static <T extends Status> StatusSwitch<T> choose(T status) {
        return new IncompleteStatusSwitch<>(status, status);
    }
    
    StatusSwitch.Step<T> ifStatus(Predicate<? super Status> status, Function<? super T, ? extends T> action);
    
    default StatusSwitch.Step<T> ifStatus(Status status, Function<? super T, ? extends T> action) {
        return ifStatus(status::equals, action);
    }
    
    default StatusSwitch.Step<T> ifCode(int code, Function<? super T, ? extends T> action) {
        return ifStatus(status -> status.getCode() == code, action);
    }
    
    default StatusSwitch.Step<T> ifOk(Function<? super T, ? extends T> action) {
        return ifStatus(Status::isOk, action);
    }
    
    default StatusSwitch.Step<T> ifIllegal(Function<? super T, ? extends T> action) {
        return ifStatus(Status::isIllegal, action);
    }
    
    default StatusSwitch.Step<T> ifNotFound(Function<? super T, ? extends T> action) {
        return ifStatus(Status::isNotFound, action);
    }
    
    default StatusSwitch.Step<T> ifInternal(Function<? super T, ? extends T> action) {
        return ifStatus(Status::isInternal, action);
    }
    
    default StatusSwitch.Step<T> ifStatus(Predicate<? super Status> status, Supplier<? extends T> action) {
        return ifStatus(status, r -> action.get());
    }
    
    default StatusSwitch.Step<T> ifStatus(Status status, Supplier<? extends T> action) {
        return ifStatus(status::equals, action);
    }
    
    default StatusSwitch.Step<T> ifCode(int code, Supplier<? extends T> action) {
        return ifStatus(status -> status.getCode() == code, action);
    }
    
    default StatusSwitch.Step<T> ifOk(Supplier<? extends T> action) {
        return ifStatus(Status::isOk, action);
    }
    
    default StatusSwitch.Step<T> ifIllegal(Supplier<? extends T> action) {
        return ifStatus(Status::isIllegal, action);
    }
    
    default StatusSwitch.Step<T> ifNotFound(Supplier<? extends T> action) {
        return ifStatus(Status::isNotFound, action);
    }
    
    default StatusSwitch.Step<T> ifInternal(Supplier<? extends T> action) {
        return ifStatus(Status::isInternal, action);
    }
    
    default StatusSwitch.Step<T> ifStatus(Predicate<? super Status> status, Runnable action) {
        return ifStatus(status, r -> { action.run(); return r; });
    }
    
    default StatusSwitch.Step<T> ifStatus(Status status, Runnable action) {
        return ifStatus(status::equals, action);
    }
    
    default StatusSwitch.Step<T> ifCode(int code, Runnable action) {
        return ifStatus(status -> status.getCode() == code, action);
    }
    
    default StatusSwitch.Step<T> ifOk(Runnable action) {
        return ifStatus(Status::isOk, action);
    }
    
    default StatusSwitch.Step<T> ifIllegal(Runnable action) {
        return ifStatus(Status::isIllegal, action);
    }
    
    default StatusSwitch.Step<T> ifNotFound(Runnable action) {
        return ifStatus(Status::isNotFound, action);
    }
    
    default StatusSwitch.Step<T> ifInternal(Runnable action) {
        return ifStatus(Status::isInternal, action);
    }
    
    interface Step<T> extends StatusSwitch<T> {

        T otherwise(Function<? super T, ? extends T> action);
        
        T orKeep();

        default T otherwise(Supplier<? extends T> action) {
            return otherwise(r -> action.get());
        }

        default T otherwise(Runnable action) {
            return otherwise(r -> { action.run(); return r; });
        }
    }
}
