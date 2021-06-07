package org.cthul.monad.switches;

import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;

public interface ResultStatusSwitch<T, U, 
                    X extends Exception, R,
                    S extends ResultStatusSwitch<T, U, X, R, S, ?, ?>, 
                    C1 extends ResultSwitch.Case<T, U, X, S>, 
                    C2 extends ResultSwitch.Case<T, U, X, R>> 
        extends ResultSwitch<Status, T, U, X, R, S, C1, C2>,
                StatusSwitch<Unsafe<T, X>, Unsafe<U, X>, R, S, C1, C2> {
    
    interface Identity<T, X extends Exception, R, 
                        S extends Identity<T, X, R, S, ?, ?>, 
                        C1 extends ResultSwitch.IdentityCase<T, X, S>, 
                        C2 extends ResultSwitch.IdentityCase<T, X, R>> 
            extends ResultStatusSwitch<T, T, X, R, S, C1, C2>,
                    ResultSwitch.Identity<Status, T, X, R, S, C1, C2>,
                    StatusSwitch.Identity<Unsafe<T, X>, R, S, C1, C2> {
    }
    
    interface Direct<T, U, X extends Exception,
                        S extends ResultStatusSwitch<T, U, X, Unsafe<U, X>, S, ?, ?>,
                        C1 extends ResultSwitch.Case<T, U, X, S>,
                        C2 extends ResultSwitch.Case<T, U, X, Unsafe<U, X>>>
            extends ResultStatusSwitch<T, U, X, Unsafe<U, X>, S, C1, C2>,
                    ResultSwitch.Direct<Status, T, U, X, S, C1, C2>,
                    StatusSwitch.Direct<Unsafe<T, X>, Unsafe<U, X>, S, C1, C2> {
    }
    
    interface DirectIdentity<T, X extends Exception,
                        S extends ResultStatusSwitch.Identity<T, X, Unsafe<T, X>, S, ?, ?>,
                        C1 extends ResultSwitch.IdentityCase<T, X, S>,
                        C2 extends ResultSwitch.IdentityCase<T, X, Unsafe<T, X>>>
            extends ResultStatusSwitch.Identity<T, X, Unsafe<T, X>, S, C1, C2>,
                    ResultStatusSwitch.Direct<T, T, X, S, C1, C2>,
                    ResultSwitch.DirectIdentity<Status, T, X, S, C1, C2>,
                    StatusSwitch.DirectIdentity<Unsafe<T, X>, S, C1, C2> {
    }
}
