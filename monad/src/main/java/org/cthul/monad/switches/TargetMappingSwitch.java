package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.CheckedPredicate;

public class TargetMappingSwitch<K, T, U0, U1, R>
        extends SwitchDelegator<K, T, U1, R, 
                        TargetMappingSwitch<K, T, U0, U1, R>,
                        Switch.Case<T, U1, TargetMappingSwitch<K, T, U0, U1, R>>,
                        Switch.Case<T, U1, R>,
                        Switch<K, T, U0, R, ?, ?, ?>> {
    
    private final Function<? super U1, ? extends U0> valueMapping;

    public TargetMappingSwitch(Switch<K, T, U0, R, ?, ?, ?> delegate, Function<? super U1, ? extends U0> valueMapping) {
        super(delegate);
        this.valueMapping = valueMapping;
    }

    @Override
    protected TargetMappingSwitch<K, T, U0, U1, R> wrapNextStep(Switch<K, T, U0, R, ?, ?, ?> newDelegate) {
        return new TargetMappingSwitch<>(newDelegate, valueMapping);
    }

    @Override
    protected <X extends Exception> Case<T, U1, TargetMappingSwitch<K, T, U0, U1, R>> ifTrue(Switch<K, T, U0, R, ?, ?, ?> delegate, CheckedPredicate<? super K, X> condition) throws X {
        return cached(delegate.ifTrue(condition), this::newCase);
    }

    private Switch.Case<T, U1, TargetMappingSwitch<K, T, U0, U1, R>> newCase(Switch.Case<T, U0, ? extends Switch<K, T, U0, R, ?, ?, ?>> case1) {
        return new MappingDelegator<>(case1, wrapNextStep());
    }

    @Override
    protected Switch.Case<T, U1, R> orElse(Switch<K, T, U0, R, ?, ?, ?> delegate) {
        return new MappingDelegator<>(delegate.orElse(), identity());
    }
    
    protected class MappingDelegator<S0, S1> extends CaseDelegator<T, U1, S0, S1, Case<T, U0, S0>>{

        public MappingDelegator(Case<T, U0, S0> delegate, Function<? super S0, ? extends S1> resultMapping) {
            super(delegate, resultMapping);
        }

        @Override
        protected <X extends Exception> S0 delegateCase(Case<T, U0, S0> delegate, CheckedFunction<T, U1, X> function) throws X {
            return delegate.map(t -> {
                U1 u1 = function.apply(t);
                return valueMapping.apply(u1);
            });
        }
    }
}
