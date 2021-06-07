package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.function.CheckedPredicate;

public class ResultMappingSwitch<K, T, U, R0, R1>
        extends SwitchDelegator<K, T, U, R1, 
                        ResultMappingSwitch<K, T, U, R0, R1>,
                        Switch.Case<T, U, ResultMappingSwitch<K, T, U, R0, R1>>,
                        Switch.Case<T, U, R1>,
                        Switch<K, T, U, R0, ?, ?, ?>> {
    
    private final Function<? super R0, ? extends R1> resultMapping;
    
    public ResultMappingSwitch(Switch<K, T, U, R0, ?, ?, ?> delegate, Function<? super R0, ? extends R1> resultMapping) {
        super(delegate);
        this.resultMapping = resultMapping;
    }

    @Override
    protected ResultMappingSwitch<K, T, U, R0, R1> wrapNextStep(Switch<K, T, U, R0, ?, ?, ?> newDelegate) {
        return new ResultMappingSwitch<>(newDelegate, resultMapping);
    }

    @Override
    protected <X extends Exception> Switch.Case<T, U, ResultMappingSwitch<K, T, U, R0, R1>> ifTrue(Switch<K, T, U, R0, ?, ?, ?> delegate, CheckedPredicate<? super K, X> condition) throws X {
        Switch.Case<T, U, ? extends Switch<K, T, U, R0, ?, ?, ?>> case1 = delegate.ifTrue(condition);
        return cached(case1, this::newCase);
    }

    private Switch.Case<T, U, ResultMappingSwitch<K, T, U, R0, R1>> newCase(Switch.Case<T, U, ? extends Switch<K, T, U, R0, ?, ?, ?>> case1) {
        return new CaseDelegator<>(case1, wrapNextStep());
    }

    @Override
    protected Switch.Case<T, U, R1> orElse(Switch<K, T, U, R0, ?, ?, ?> delegate) {
        Switch.Case<T, U, R0> case2 = delegate.orElse();
        return new CaseDelegator<>(case2, resultMapping);
    }
}
