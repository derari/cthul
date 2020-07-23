package org.cthul.monad.util;

import java.util.function.Predicate;
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
    
    <X extends Exception> StatusSwitch.Step<T> ifStatus(Predicate<? super Status> status, Checked.Function<? super T, ? extends T, X> action) throws X;
    
    default <X extends Exception> StatusSwitch.Step<T> ifStatus(Status status, Checked.Function<? super T, ? extends T, X> action) throws X {
        return ifStatus(status::equals, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifCode(int code, Checked.Function<? super T, ? extends T, X> action) throws X {
        return ifStatus(status -> status.getCode() == code, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifOk(Checked.Function<? super T, ? extends T, X> action) throws X {
        return ifStatus(Status::isOk, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifIllegal(Checked.Function<? super T, ? extends T, X> action) throws X {
        return ifStatus(Status::isIllegal, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifNotFound(Checked.Function<? super T, ? extends T, X> action) throws X {
        return ifStatus(Status::isNotFound, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifInternal(Checked.Function<? super T, ? extends T, X> action) throws X {
        return ifStatus(Status::isInternal, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifStatus(Predicate<? super Status> status, Checked.Supplier<? extends T, X> action) throws X {
        return ifStatus(status, r -> action.get());
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifStatus(Status status, Checked.Supplier<? extends T, X> action) throws X {
        return ifStatus(status::equals, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifCode(int code, Checked.Supplier<? extends T, X> action) throws X {
        return ifStatus(status -> status.getCode() == code, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifOk(Checked.Supplier<? extends T, X> action) throws X {
        return ifStatus(Status::isOk, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifIllegal(Checked.Supplier<? extends T, X> action) throws X {
        return ifStatus(Status::isIllegal, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifNotFound(Checked.Supplier<? extends T, X> action) throws X {
        return ifStatus(Status::isNotFound, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifInternal(Checked.Supplier<? extends T, X> action) throws X {
        return ifStatus(Status::isInternal, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifStatus(Predicate<? super Status> status, Checked.Runnable<X> action) throws X {
        return ifStatus(status, r -> { action.run(); return r; });
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifStatus(Status status, Checked.Runnable<X> action) throws X {
        return ifStatus(status::equals, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifCode(int code, Checked.Runnable<X> action) throws X {
        return ifStatus(status -> status.getCode() == code, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifOk(Checked.Runnable<X> action) throws X {
        return ifStatus(Status::isOk, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifIllegal(Checked.Runnable<X> action) throws X {
        return ifStatus(Status::isIllegal, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifNotFound(Checked.Runnable<X> action) throws X {
        return ifStatus(Status::isNotFound, action);
    }
    
    default <X extends Exception> StatusSwitch.Step<T> ifInternal(Checked.Runnable<X> action) throws X {
        return ifStatus(Status::isInternal, action);
    }
    
    interface Initial<T> extends StatusSwitch<T> {
        
        <U> StatusSwitch<U> withValue(U value);
    }
    
    interface Step<T> extends StatusSwitch<T> {

        <X extends Exception> T otherwise(Checked.Function<? super T, ? extends T, X> action) throws X;
        
        T orKeep();

        default <X extends Exception> T otherwise(Checked.Supplier<? extends T, X> action) throws X {
            return otherwise(r -> action.get());
        }

        default <X extends Exception> T otherwise(Checked.Runnable<X> action) throws X {
            return otherwise(r -> { action.run(); return r; });
        }

        default <X extends Exception> void or(Checked.Consumer<? super T, X> action) throws X {
            otherwise(r -> { action.accept(r); return r; });
        }
    }
}
