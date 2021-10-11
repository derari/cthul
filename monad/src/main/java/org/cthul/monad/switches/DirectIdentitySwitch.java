package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.CheckedPredicate;

public class DirectIdentitySwitch<K, T>
        implements BasicSwitch.DirectIdentity<K, T> {

    public static <K, T, U> BasicSwitch.DirectIdentity<K, T> overrideResultOfUnmatchedSwitch(Switch<K, T, T, T, ?, ?, ?> delegate) {
        return new DirectIdentitySwitch<>(delegate);
    }

    public static <K, T, U> BasicSwitch.DirectIdentity<K, T> wrap(Switch.Direct<K, T, T, ?, ?, ?> delegate) {
        return new DirectIdentitySwitch<>(delegate);
    }

    private final Switch<K, T, T, T, ?, ?, ?> delegate;

    public DirectIdentitySwitch(Direct<K, T, T, ?, ?, ?> delegate) {
        this.delegate = delegate;
    }

    private DirectIdentitySwitch(Switch<K, T, T, T, ?, ?, ?> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <X extends Exception> Switch.IdentityCase<T, BasicSwitch.Identity<K, T, T>> ifTrue(CheckedPredicate<? super K, X> condition) throws X {
        Case<T, T, ? extends Switch<K, T, T, T, ?, ?, ?>> case1 = delegate.ifTrue(condition);
        return new IdentityCase<>(case1, BasicSwitch.identitySwitch());
    }

    @Override
    public <X extends Exception> Switch.IdentityCase<T, BasicSwitch.Identity<K, T, T>> ifFalse(CheckedPredicate<? super K, X> condition) throws X {
        Case<T, T, ? extends Switch<K, T, T, T, ?, ?, ?>> case1 = delegate.ifFalse(condition);
        return new IdentityCase<>(case1, BasicSwitch.identitySwitch());
    }

    @Override
    public Switch.IdentityCase<T, T> orElse() {
        Case<T, T, T> case2 = delegate.orElse();
        return new IdentityCase<>(case2, SwitchDelegator.identity());
    }

    @Override
    public <A> A wrapWith(Function<? super Switch<K, T, T, T, ?, ?, ?>, ? extends A> adapter) {
        return delegate.as(adapter);
    }

    protected static class IdentityCase<T, S0, S1> implements Switch.IdentityCase<T, S1> {

        private final Switch.Case<T, T, S0> delegate;
        private final Function<? super S0, ? extends S1> stepMapping;

        public IdentityCase(Case<T, T, S0> delegate, Function<? super S0, ? extends S1> stepMapping) {
            this.delegate = delegate;
            this.stepMapping = stepMapping;
        }

        @Override
        public <X extends Exception> S1 map(CheckedFunction<T, ? extends T, X> mapping) throws X {
            S0 delegateResult = delegate.map(mapping);
            return stepMapping.apply(delegateResult);
        }
    }
}
