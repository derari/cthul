package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.CheckedPredicate;

public class SourceMappingSwitch<K, T0, T1, U, R>
        extends SwitchDelegator<K, T1, U, R, 
                        BasicSwitch<K, T1, U, R>,
                        BasicSwitch.Case<T1, U, BasicSwitch<K, T1, U, R>>,
                        BasicSwitch.Case<T1, U, R>,
                        Switch<K, T0, U, R, ?, ?, ?>> 
        implements BasicSwitch<K, T1, U, R> {
    
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
    protected <X extends Exception> Case<T1, U, BasicSwitch<K, T1, U, R>> ifTrue(Switch<K, T0, U, R, ?, ?, ?> delegate, CheckedPredicate<? super K, X> condition) throws X {
        return cached(delegate.ifTrue(condition), this::newCase);
    }

    private Switch.Case<T1, U, BasicSwitch<K, T1, U, R>> newCase(Switch.Case<T0, U, ? extends Switch<K, T0, U, R, ?, ?, ?>> case1) {
        return new MappingDelegator<>(case1, wrapNextStep());
    }

    @Override
    protected Switch.Case<T1, U, R> orElse(Switch<K, T0, U, R, ?, ?, ?> delegate) {
        Switch.Case<T0, U, R> case2 = delegate.orElse();
        return new MappingDelegator<>(case2, identity());
    }
    
    protected class MappingDelegator<S0, S1> extends CaseDelegator<T1, U, S0, S1, Case<T0, U, S0>> implements BasicSwitch.Case<T1, U, S1> {

        public MappingDelegator(Case<T0, U, S0> delegate, Function<? super S0, ? extends S1> resultMapping) {
            super(delegate, resultMapping);
        }

        @Override
        protected <X extends Exception> S0 delegateCase(Case<T0, U, S0> delegate, CheckedFunction<T1, U, X> function) throws X {
            return delegate.map(t0 -> {
                T1 t1 = valueMapping.apply(t0);
                return function.apply(t1);
            });
        }
    }
}
