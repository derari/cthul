package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.CheckedPredicate;


public interface BasicSwitch<K, T, U, R> 
        extends Switch<K, T, U, R, 
                        BasicSwitch<K, T, U, R>,
                        Switch.Case<T, U, BasicSwitch<K, T, U, R>>,
                        Switch.Case<T, U, R>> {
    
    static <K, T, U, R> Function<Switch<K, T, U, R, ?, ?, ?>, BasicSwitch<K, T, U, R>> basicSwitch() {
        return BasicInterfaceSwitch.wrapper();
    }
    
    static <K, T, R> Function<Switch<K, T, T, R, ?, ?, ?>, BasicSwitch.Identity<K, T, R>> identitySwitch() {
        return BasicIdentitySwitch.wrapper();
    }
    
    static <T, U, R> Function<Switch<org.cthul.monad.Status, T, U, R, ?, ?, ?>, BasicSwitch.Status<T, U, R>> statusSwitch() {
        return BasicStatusSwitch.wrapper();
    }
    
    static <T, R> Function<Switch<org.cthul.monad.Status, T, T, R, ?, ?, ?>, BasicSwitch.StatusIdentity<T, R>> statusIdentitySwitch() {
        return BasicStatusSwitch.identityWrapper();
    }

    static <K, T, U, R> BasicSwitch<K, T, U, R> asBasicSwitch(Switch<K, T, U, R, ?, ?, ?> instance) {
        return instance.asBasicSwitch();
    }

    static <K, T, R> BasicSwitch.Identity<K, T, R> asIdentitySwitch(Switch<K, T, T, R, ?, ?, ?> instance) {
        return instance.asIdentitySwitch(identitySwitch());
    }
    
    @Override
    default BasicSwitch<K, T, U, R> asBasicSwitch() {
        return this;
    }

    @Override
    default <A> A as(Function<? super Switch<K, T, U, R, ?, ?, ?>, ? extends A> adapter) {
        if (adapter == BasicSwitch.<K, T, U, R>basicSwitch()) {
            return (A) this;
        }
        return Switch.super.as(adapter);
    }

    @Override
    default <K2> BasicSwitch<K2, T, U, R> mapKey(CheckedPredicate.Adapter<? super K, ? extends K2> conditionAdapter) {
        return Switch.super.<K2>mapKey(conditionAdapter).asBasicSwitch();
    }
    
    interface Identity<K, T, R>
            extends Switch.Identity<K, T, R, 
                            BasicSwitch.Identity<K, T, R>, 
                            Switch.IdentityCase<T, BasicSwitch.Identity<K, T, R>>, 
                            Switch.IdentityCase<T, R>> {

        @Override
        default BasicSwitch.Identity<K, T, R> asIdentitySwitch(Function<? super Switch<K, T, T, R, ?, ?, ?>, ? extends BasicSwitch.Identity<K, T, R>> adapter) {
            return this;
        }
        
        @Override
        default <A> A as(Function<? super Switch<K, T, T, R, ?, ?, ?>, ? extends A> adapter) {
            if (adapter == BasicSwitch.<K, T, R>identitySwitch()) {
                return (A) this;
            }
            return Switch.Identity.super.as(adapter);
        }
    }
    
    interface Direct<K, T, U> 
            extends Switch.Direct<K, T, U,
                            BasicSwitch<K, T, U, U>,
                            Switch.Case<T, U, BasicSwitch<K, T, U, U>>,
                            Switch.Case<T, U, U>> {
    }
    
    interface DirectIdentity<K, T> 
            extends Switch.DirectIdentity<K, T,
                            BasicSwitch.Identity<K, T, T>,
                            Switch.IdentityCase<T, BasicSwitch.Identity<K, T, T>>,
                            Switch.IdentityCase<T, T>> {
    }
    
    interface Status<T, U, R>
            extends StatusSwitch<T, U, R, 
                            BasicSwitch.Status<T, U, R>, 
                            Switch.Case<T, U, BasicSwitch.Status<T, U, R>>, 
                            Switch.Case<T, U, R>> {

        @Override
        default Status<T, U, R> asStatusSwitch(Function<? super Switch<org.cthul.monad.Status, T, U, R, ?, ?, ?>, ? extends Status<T, U, R>> adapter) {
            return this;
        }
        
        @Override
        default <A> A as(Function<? super Switch<org.cthul.monad.Status, T, U, R, ?, ?, ?>, ? extends A> adapter) {
            if (adapter == BasicSwitch.<T, U, R>statusSwitch()) {
                return (A) this;
            }
            return StatusSwitch.super.as(adapter);
        }
    }
    
    interface StatusIdentity<T, R>
            extends StatusSwitch.Identity<T, R, 
                            BasicSwitch.StatusIdentity<T, R>, 
                            Switch.IdentityCase<T, BasicSwitch.StatusIdentity<T, R>>, 
                            Switch.IdentityCase<T, R>> {

        @Override
        default StatusIdentity<T, R> asStatusIdentitySwitch(Function<? super Switch<org.cthul.monad.Status, T, T, R, ?, ?, ?>, ? extends StatusIdentity<T, R>> adapter) {
            return this;
        }
        
        @Override
        default <A> A as(Function<? super Switch<org.cthul.monad.Status, T, T, R, ?, ?, ?>, ? extends A> adapter) {
            if (adapter == BasicSwitch.<T, R>statusIdentitySwitch()) {
                return (A) this;
            }
            return StatusSwitch.Identity.super.as(adapter);
        }
    }
    
    interface Result<K, T, U, X extends Exception, R>
            extends ResultSwitch<K, T, U, X, R, 
                            BasicSwitch.Result<K, T, U, X, R>, 
                            ResultSwitch.Case<T, U, X, BasicSwitch.Result<K, T, U, X, R>>, 
                            ResultSwitch.Case<T, U, X, R>> {
    }
    
    interface ResultIdentity<K, T, X extends Exception, R>
            extends ResultSwitch.Identity<K, T, X, R, 
                            BasicSwitch.ResultIdentity<K, T, X, R>, 
                            ResultSwitch.IdentityCase<T, X, BasicSwitch.ResultIdentity<K, T, X, R>>, 
                            ResultSwitch.IdentityCase<T, X, R>> {
    }
    
    interface DirectResult<K, T, U, X extends Exception>
            extends ResultSwitch.Direct<K, T, U, X, 
                            BasicSwitch.Result<K, T, U, X, Unsafe<U, X>>, 
                            ResultSwitch.Case<T, U, X, BasicSwitch.Result<K, T, U, X, Unsafe<U, X>>>, 
                            ResultSwitch.Case<T, U, X, Unsafe<U, X>>> {
    }
    
    interface DirectResultIdentity<K, T, X extends Exception>
            extends ResultSwitch.DirectIdentity<K, T, X, 
                            BasicSwitch.ResultIdentity<K, T, X, Unsafe<T, X>>, 
                            ResultSwitch.IdentityCase<T, X, BasicSwitch.ResultIdentity<K, T, X, Unsafe<T, X>>>, 
                            ResultSwitch.IdentityCase<T, X, Unsafe<T, X>>> {
    }
    
    interface ResultStatus<T, U, X extends Exception, R>
            extends ResultStatusSwitch<T, U, X, R, 
                            BasicSwitch.ResultStatus<T, U, X, R>, 
                            ResultSwitch.Case<T, U, X, BasicSwitch.ResultStatus<T, U, X, R>>, 
                            ResultSwitch.Case<T, U, X, R>> {
    }
    
    interface ResultStatusIdentity<T, X extends Exception, R>
            extends ResultStatusSwitch.Identity<T, X, R, 
                            BasicSwitch.ResultStatusIdentity<T, X, R>, 
                            ResultSwitch.IdentityCase<T, X, BasicSwitch.ResultStatusIdentity<T, X, R>>, 
                            ResultSwitch.IdentityCase<T, X, R>> {
    }
    
    interface DirectResultStatus<T, U, X extends Exception>
            extends ResultSwitch.Direct<org.cthul.monad.Status, T, U, X, 
                            BasicSwitch.ResultStatus<T, U, X, Unsafe<U, X>>, 
                            ResultSwitch.Case<T, U, X, BasicSwitch.ResultStatus<T, U, X, Unsafe<U, X>>>, 
                            ResultSwitch.Case<T, U, X, Unsafe<U, X>>>,
                    StatusSwitch.Direct<Unsafe<T, X>, Unsafe<U, X>,
                            BasicSwitch.ResultStatus<T, U, X, Unsafe<U, X>>, 
                            ResultSwitch.Case<T, U, X, BasicSwitch.ResultStatus<T, U, X, Unsafe<U, X>>>, 
                            ResultSwitch.Case<T, U, X, Unsafe<U, X>>> {
    }
    
    interface DirectResultStatusIdentity<T, X extends Exception>
            extends ResultStatusSwitch.DirectIdentity<T, X, 
                            BasicSwitch.ResultStatusIdentity<T, X, Unsafe<T, X>>, 
                            ResultSwitch.IdentityCase<T, X, BasicSwitch.ResultStatusIdentity<T, X, Unsafe<T, X>>>, 
                            ResultSwitch.IdentityCase<T, X, Unsafe<T, X>>>,
                    StatusSwitch.DirectIdentity<Unsafe<T, X>, 
                            BasicSwitch.ResultStatusIdentity<T, X, Unsafe<T, X>>, 
                            ResultSwitch.IdentityCase<T, X, BasicSwitch.ResultStatusIdentity<T, X, Unsafe<T, X>>>, 
                            ResultSwitch.IdentityCase<T, X, Unsafe<T, X>>> {
    }
}
