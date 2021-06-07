package org.cthul.monad.switches;

import org.cthul.monad.function.CheckedPredicate;

public class ConditionAdaptingSwitch<K0, K1, T, U, R>
        extends SwitchDelegator<K1, T, U, R, 
                        ConditionAdaptingSwitch<K0, K1, T, U, R>,
                        Switch.Case<T, U, ConditionAdaptingSwitch<K0, K1, T, U, R>>,
                        Switch.Case<T, U, R>,
                        Switch<K0, T, U, R, ?, ?, ?>> {
    
    private final CheckedPredicate.Adapter<? super K0, ? extends K1> adapter;
    
    public ConditionAdaptingSwitch(Switch<K0, T, U, R, ?, ?, ?> delegate, CheckedPredicate.Adapter<? super K0, ? extends K1> adapter) {
        super(delegate);
        this.adapter = adapter;
    }

    @Override
    protected ConditionAdaptingSwitch<K0, K1, T, U, R> wrapNextStep(Switch<K0, T, U, R, ?, ?, ?> newDelegate) {
        return new ConditionAdaptingSwitch<>(newDelegate, adapter);
    }

    @Override
    protected <X extends Exception> Switch.Case<T, U, ConditionAdaptingSwitch<K0, K1, T, U, R>> ifTrue(Switch<K0, T, U, R, ?, ?, ?> delegate, CheckedPredicate<? super K1, X> condition) throws X {
        Switch.Case<T, U, ? extends Switch<K0, T, U, R, ?, ?, ?>> case1 = delegate.ifTrue(test(condition));
        return cached(case1, this::newCase);
    }
    
    protected <X extends Exception> CheckedPredicate<K0, X> test(CheckedPredicate<? super K1, X> condition) {
        return k0 -> {
            try {
                return adapter.test(k0, condition);
            } catch (Exception ex) {
                throw (X) ex;
            }
        };
    }

    private Switch.Case<T, U, ConditionAdaptingSwitch<K0, K1, T, U, R>> newCase(Switch.Case<T, U, ? extends Switch<K0, T, U, R, ?, ?, ?>> case1) {
        return new CaseDelegator<>(case1, wrapNextStep());
    }

    @Override
    protected Switch.Case<T, U, R> orElse(Switch<K0, T, U, R, ?, ?, ?> delegate) {
        Switch.Case<T, U, R> case2 = delegate.orElse();
        return new CaseDelegator<>(case2, identity());
    }
}
