package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.CheckedPredicate;

public class SourceMappingSwitch<K, T0, T1, U, R>
        extends SwitchDelegator<K, T1, U, R, 
                        SourceMappingSwitch<K, T0, T1, U, R>,
                        Switch.Case<T1, U, SourceMappingSwitch<K, T0, T1, U, R>>,
                        Switch.Case<T1, U, R>,
                        Switch<K, T0, U, R, ?, ?, ?>> {
    
    private final Function<? super T0, ? extends T1> valueMapping;

    public SourceMappingSwitch(Switch<K, T0, U, R, ?, ?, ?> delegate, Function<? super T0, ? extends T1> valueMapping) {
        super(delegate);
        this.valueMapping = valueMapping;
    }

    @Override
    protected SourceMappingSwitch<K, T0, T1, U, R> wrapNextStep(Switch<K, T0, U, R, ?, ?, ?> newDelegate) {
        return new SourceMappingSwitch<>(newDelegate, valueMapping);
    }

    @Override
    protected <X extends Exception> Case<T1, U, SourceMappingSwitch<K, T0, T1, U, R>> ifTrue(Switch<K, T0, U, R, ?, ?, ?> delegate, CheckedPredicate<? super K, X> condition) throws X {
        Switch.Case<T0, U, ? extends Switch<K, T0, U, R, ?, ?, ?>> case1 = delegate.ifTrue(condition);
        return cached(case1, this::newCase);
    }

    private Switch.Case<T1, U, SourceMappingSwitch<K, T0, T1, U, R>> newCase(Switch.Case<T0, U, ? extends Switch<K, T0, U, R, ?, ?, ?>> case1) {
        return new CaseDelegator<>(case1, wrapNextStep());
    }

    @Override
    protected Switch.Case<T1, U, R> orElse(Switch<K, T0, U, R, ?, ?, ?> delegate) {
        Switch.Case<T0, U, R> case2 = delegate.orElse();
        return new CaseDelegator<>(case2, identity());
    }
    
    protected class CaseDelegator<S0, S1> implements Switch.Case<T1, U, S1> {
        
        private final Switch.Case<T0, U, S0> delegate;
        private final Function<? super S0, ? extends S1> resultMapping;

        public CaseDelegator(Case<T0, U, S0> delegate, Function<? super S0, ? extends S1> resultMapping) {
            this.delegate = delegate;
            this.resultMapping = resultMapping;
        }

        @Override
        public <X extends Exception> S1 map(CheckedFunction<T1, U, X> function) throws X {
            S0 delegateResult = delegate.map(t0 -> {
                T1 t1 = valueMapping.apply(t0);
                return function.apply(t1);
            });
            return resultMapping.apply(delegateResult);
        }
    }
}
