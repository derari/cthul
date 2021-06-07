package org.cthul.monad.switches;

import org.cthul.monad.Scope;
import org.cthul.monad.Unsafe;

public interface ResultChoices<T, U, X extends Exception, R> {
    
    ResultSwitch<Unsafe<T, X>, T, U, X, R, ?, ?, ?> choose();
    
    default ResultSwitch<Scope, T, U, X, R, ?, ?, ?> chooseByScope() {
        return choose().mapKey(Unsafe::getScope);
    }
    
    default ResultStatusSwitch<T, U, X, R, ?, ?, ?> chooseByStatus() {
        return new BasicResultStatusSwitch<>(choose().mapKey(Unsafe::getStatus));
    }
    
    default ResultSwitch<T, T, U, X, R, ?, ?, ?> chooseByValue() {
        return choose().mapKey((u, c) -> u.isPresent() && c.test(u.get()));
    }
    
    default ResultSwitch<X, T, U, X, R, ?, ?, ?> chooseByException() {
        return choose().mapKey((u, c) -> !u.isPresent() && c.test(u.getException()));
    }
    
    interface Direct<T, X extends Exception> extends ResultChoices<T, T, X, Unsafe<T, X>> {
        
        BasicSwitch.DirectResultIdentity<Unsafe<T, X>, T, X> choose();

        default BasicSwitch.DirectResultIdentity<Scope, T, X> chooseByScope() {
            return new BasicResultSwitch.DirectIdentity<>(ResultChoices.super.chooseByScope());
        }

        default BasicSwitch.DirectResultStatusIdentity<T, X> chooseByStatus() {
            return new BasicResultStatusSwitch.DirectIdentity<>(choose().mapKey(Unsafe::getStatus));
        }

        default BasicSwitch.DirectResultIdentity<T, T, X> chooseByValue() {
            return new BasicResultSwitch.DirectIdentity<>(ResultChoices.super.chooseByValue());
        }

        default BasicSwitch.DirectResultIdentity<X, T, X> chooseByException() {
            return new BasicResultSwitch.DirectIdentity<>(ResultChoices.super.chooseByException());
        }
    }
}
