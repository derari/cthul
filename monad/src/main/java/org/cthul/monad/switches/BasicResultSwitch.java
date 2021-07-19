package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.Unsafe;

public class BasicResultSwitch<K, T, U, X extends Exception, R>
        extends SwitchExtension<K, Unsafe<T, X>, Unsafe<U, X>, R, 
                        BasicSwitch.Result<K, T, U, X, R>, 
                        ResultSwitch.Case<T, U, X, BasicSwitch.Result<K, T, U, X, R>>, 
                        ResultSwitch.Case<T, U, X, R>> 
        implements BasicSwitch.Result<K, T, U, X, R> {
    
    @SuppressWarnings("null")
    public static <K, T, U, X extends Exception, R> BasicResultSwitch<K, T, U, X, R> wrap(Switch<K, Unsafe<T, X>, Unsafe<U, X>, R, ?, ?, ?> delegate) {
        if (delegate instanceof BasicResultSwitch) {
            return (BasicResultSwitch) delegate;
        }
        return delegate.as(BasicResultSwitch::new);
    }
    
    @SuppressWarnings("null")
    public static <K, T, X extends Exception, R> BasicSwitch.ResultIdentity<K, T, X, R> wrapIdentity(Switch<K, Unsafe<T, X>, Unsafe<T, X>, R, ?, ?, ?> delegate) {
        if (delegate instanceof BasicSwitch.ResultIdentity) {
            return (BasicSwitch.ResultIdentity) delegate;
        }
        return delegate.as(BasicResultSwitch.Identity::new);
    }
    
    public static <K, T, X extends Exception> BasicSwitch.DirectResultIdentity<K, T, X> wrapDirectIdentity(Switch.Direct<K, Unsafe<T, X>, Unsafe<T, X>, ?, ?, ?> delegate) {
        return overrideResultOfUnmatchedIdentity(delegate);
    }
    
    @SuppressWarnings("null")
    public static <K, T, X extends Exception> BasicSwitch.DirectResultIdentity<K, T, X> overrideResultOfUnmatchedIdentity(Switch<K, Unsafe<T, X>, Unsafe<T, X>, Unsafe<T, X>, ?, ?, ?> delegate) {
        if (delegate instanceof BasicSwitch.DirectResultIdentity) {
            return (BasicSwitch.DirectResultIdentity) delegate;
        }
        return delegate.as(BasicResultSwitch.DirectIdentity::new);
    }

    public BasicResultSwitch(Switch<K, Unsafe<T, X>, Unsafe<U, X>, R, ?, ?, ?> delegate) {
        super(delegate);
    }

    @Override
    protected <R0, R1> ResultSwitch.Case<T, U, X, R1> newCaseImplementation(Switch.Case<Unsafe<T, X>, Unsafe<U, X>, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
        return new ResultCaseDelegator<>(caseDelegate, resultMapping);
    }

    @Override
    protected BasicSwitch.Result<K, T, U, X, R> wrapNextStep(Switch<K, Unsafe<T, X>, Unsafe<U, X>, R, ?, ?, ?> newDelegate) {
        return new BasicResultSwitch<>(newDelegate);
    }
    
    protected static class ResultCaseDelegator<T, U, X extends Exception, R0, R1> 
            extends IdentityDelegator<Unsafe<T, X>, Unsafe<U, X>, R0, R1>
            implements ResultSwitch.Case<T, U, X, R1> {

        public ResultCaseDelegator(Switch.Case<Unsafe<T, X>, Unsafe<U, X>, R0> delegate, Function<? super R0, ? extends R1> resultMapping) {
            super(delegate, resultMapping);
        }
    }
    
    public static class Identity<K, T, X extends Exception, R>
            extends SwitchExtension<K, Unsafe<T, X>, Unsafe<T, X>, R, 
                            BasicSwitch.ResultIdentity<K, T, X, R>, 
                            ResultSwitch.IdentityCase<T, X, BasicSwitch.ResultIdentity<K, T, X, R>>, 
                            ResultSwitch.IdentityCase<T, X, R>> 
            implements BasicSwitch.ResultIdentity<K, T, X, R> {

        protected Identity(Switch<K, Unsafe<T, X>, Unsafe<T, X>, R, ?, ?, ?> delegate) {
            super(delegate);
        }

        @Override
        protected <R0, R1> ResultSwitch.IdentityCase<T, X, R1> newCaseImplementation(Case<Unsafe<T, X>, Unsafe<T, X>, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
            return new ResultIdentityCaseDelegator<>(caseDelegate, resultMapping);
        }

        @Override
        protected BasicSwitch.ResultIdentity<K, T, X, R> wrapNextStep(Switch<K, Unsafe<T, X>, Unsafe<T, X>, R, ?, ?, ?> newDelegate) {
            return wrapIdentity(newDelegate);
        }
    }
    
    protected static class ResultIdentityCaseDelegator<T, X extends Exception, R0, R1> 
            extends IdentityDelegator<Unsafe<T, X>, Unsafe<T, X>, R0, R1>
            implements ResultSwitch.IdentityCase<T, X, R1> {

        public ResultIdentityCaseDelegator(Switch.Case<Unsafe<T, X>, Unsafe<T, X>, R0> delegate, Function<? super R0, ? extends R1> resultMapping) {
            super(delegate, resultMapping);
        }
    }
    
    public static class Direct<K, T, U, X extends Exception>
            extends SwitchExtension.Direct<K, Unsafe<T, X>, Unsafe<U, X>, Unsafe<U, X>, 
                            BasicSwitch.Result<K, T, U, X, Unsafe<U, X>>, 
                            ResultSwitch.Case<T, U, X, BasicSwitch.Result<K, T, U, X, Unsafe<U, X>>>, 
                            ResultSwitch.Case<T, U, X, Unsafe<U, X>>>
            implements BasicSwitch.DirectResult<K, T, U, X> {

        public Direct(Switch<K, Unsafe<T, X>, Unsafe<U, X>, Unsafe<U, X>, ?, ?, ?> delegate) {
            super(delegate);
        }

        @Override
        protected <R0, R1> Case<Unsafe<T, X>, Unsafe<U, X>, R1> newCaseImplementation(Case<Unsafe<T, X>, Unsafe<U, X>, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
            return new ResultCaseDelegator<>(caseDelegate, resultMapping);
        }

        @Override
        protected BasicSwitch.Result<K, T, U, X, Unsafe<U, X>> wrapNextStep(Switch<K, Unsafe<T, X>, Unsafe<U, X>, Unsafe<U, X>, ?, ?, ?> newDelegate) {
            return new BasicResultSwitch<>(newDelegate);
        }
    }

    public static class DirectIdentity<K, T, X extends Exception>
            extends SwitchExtension.Direct<K, Unsafe<T, X>, Unsafe<T, X>, Unsafe<T, X>,
                            BasicSwitch.ResultIdentity<K, T, X, Unsafe<T, X>>, 
                            ResultSwitch.IdentityCase<T, X, BasicSwitch.ResultIdentity<K, T, X, Unsafe<T, X>>>, 
                            ResultSwitch.IdentityCase<T, X, Unsafe<T, X>>>
            implements BasicSwitch.DirectResultIdentity<K, T, X> {

        public DirectIdentity(Switch<K, Unsafe<T, X>, Unsafe<T, X>, Unsafe<T, X>, ?, ?, ?> delegate) {
            super(delegate);
        }

        @Override
        protected <R0, R1> Case<Unsafe<T, X>, Unsafe<T, X>, R1> newCaseImplementation(Case<Unsafe<T, X>, Unsafe<T, X>, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping) {
            return new ResultIdentityCaseDelegator<>(caseDelegate, resultMapping);
        }

        @Override
        protected BasicSwitch.ResultIdentity<K, T, X, Unsafe<T, X>> wrapNextStep(Switch<K, Unsafe<T, X>, Unsafe<T, X>, Unsafe<T, X>, ?, ?, ?> newDelegate) {
            return wrapIdentity(newDelegate);
        }
    }
}
