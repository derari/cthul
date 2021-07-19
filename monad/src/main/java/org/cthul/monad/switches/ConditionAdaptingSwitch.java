package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.function.CheckedPredicate;

public class ConditionAdaptingSwitch<K0, K1, T, U, R>
        extends SwitchDelegator<K1, T, U, R, 
                        BasicSwitch<K1, T, U, R>,
                        BasicSwitch.Case<T, U, BasicSwitch<K1, T, U, R>>,
                        BasicSwitch.Case<T, U, R>,
                        Switch<K0, T, U, R, ?, ?, ?>>
        implements BasicSwitch<K1, T, U, R> {
    
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
    protected <X extends Exception> Case<T, U, BasicSwitch<K1, T, U, R>> ifTrue(Switch<K0, T, U, R, ?, ?, ?> delegate, CheckedPredicate<? super K1, X> condition) throws X {
        return cached(delegate.ifTrue(test(condition)), this::newCase);
    }
    
    protected <X extends Exception> CheckedPredicate<K0, X> test(CheckedPredicate<? super K1, X> condition) {
        return k0 -> adapter.testChecked(k0, condition);
    }

    private Case<T, U, BasicSwitch<K1, T, U, R>> newCase(Switch.Case<T, U, ? extends Switch<K0, T, U, R, ?, ?, ?>> case1) {
        return new BasicDelegator<>(case1, wrapNextStep());
    }

    @Override
    protected Switch.Case<T, U, R> orElse(Switch<K0, T, U, R, ?, ?, ?> delegate) {
        return new IdentityDelegator<>(delegate.orElse(), identity());
    }
    
    private static class BasicDelegator<T, U, S0, S1> extends IdentityDelegator<T, U, S0, S1> implements BasicSwitch.Case<T, U, S1> {

        public BasicDelegator(Case<T, U, S0> delegate, Function<? super S0, ? extends S1> resultMapping) {
            super(delegate, resultMapping);
        }
    }
}
