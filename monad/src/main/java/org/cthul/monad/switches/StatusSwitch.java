package org.cthul.monad.switches;

import org.cthul.monad.Status;

public interface StatusSwitch<T, U, R, S extends StatusSwitch<T, U, R, S, ?, ?>, 
                    C1 extends Switch.Case<T, U, S>, 
                    C2 extends Switch.Case<T, U, R>>
        extends Switch<Status, T, U, R, S, C1, C2> {

    default C1 ifCode(int code) {
        return ifTrue(status -> status.getCode() == code);
    }
    
    default C1 ifOk() {
        return ifTrue(Status::isOk);
    }
    
    default C1 ifIllegal() {
        return ifTrue(Status::isIllegal);
    }
    
    default C1 ifNotFound() {
        return ifTrue(Status::isNotFound);
    }
    
    default C1 ifInternal() {
        return ifTrue(Status::isInternal);
    }
    
    interface Identity<T, R, S extends Identity<T, R, S, ?, ?>, 
                        C1 extends Switch.IdentityCase<T, S>, 
                        C2 extends Switch.IdentityCase<T, R>> 
            extends StatusSwitch<T, T, R, S, C1, C2>, 
                    Switch.Identity<Status, T, R, S, C1, C2> {
    }
    
    interface Direct<T, U, S extends StatusSwitch<T, U, U, S, ?, ?>,
                        C1 extends Switch.Case<T, U, S>,
                        C2 extends Switch.Case<T, U, U>>
            extends StatusSwitch<T, U, U, S, C1, C2>,
                    Switch.Direct<Status, T, U, S, C1, C2> {
    }
    
    interface DirectIdentity<T, S extends StatusSwitch.Identity<T, T, S, ?, ?>,
                        C1 extends Switch.IdentityCase<T, S>,
                        C2 extends Switch.IdentityCase<T, T>>
            extends StatusSwitch.Identity<T, T, S, C1, C2>,
                    StatusSwitch.Direct<T, T, S, C1, C2>,
                    Switch.DirectIdentity<Status, T, S, C1, C2> {
    }
}
