package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.function.CheckedPredicate;

public abstract class SwitchExtension<K, T, U, R,
                    S extends Switch<K, T, U, R, S, ?, ?>,
                    C1 extends Switch.Case<T, U, S>,
                    C2 extends Switch.Case<T, U, R>>
        extends SwitchDelegator<K, T, U, R, S, C1, C2, Switch<K, T, U, R, ?, ?, ?>> {
    
    public SwitchExtension(Switch<K, T, U, R, ?, ?, ?> delegate) {
        super(delegate);
    }

    @Override
    protected <X extends Exception> C1 ifTrue(Switch<K, T, U, R, ?, ?, ?> delegate, CheckedPredicate<? super K, X> condition) throws X {
        Switch.Case<T, U, ? extends Switch<K, T, U, R, ?, ?, ?>> case1 = delegate.ifTrue(condition);
        return cached(case1, this::newCase);
    }

    private C1 newCase(Switch.Case<T, U, ? extends Switch<K, T, U, R, ?, ?, ?>> case1) {
        return (C1) newCaseImplementation(case1, wrapNextStep());
    }
    
    @Override
    protected C2 orElse(Switch<K, T, U, R, ?, ?, ?> delegate) {
        Switch.Case<T, U, R> case2 = delegate.orElse();
        return (C2) newCaseImplementation(case2, identity());
    }
    
    protected abstract <R0, R1> Switch.Case<T, U, R1> newCaseImplementation(Switch.Case<T, U, R0> caseDelegate, Function<? super R0, ? extends R1> resultMapping);

    @Override
    public <A> A wrapWith(Function<? super Switch<K, T, U, R, ?, ?, ?>, ? extends A> adapter) {
        return getDelegate().as(adapter);
    }
    
    public static abstract class Direct<K, T, U, R,
                        S extends Switch<K, T, U, R, S, ?, ?>,
                        C1 extends Switch.Case<T, U, S>,
                        C2 extends Switch.Case<T, U, R>>
            extends SwitchExtension<K, T, U, R, S, C1, C2> {
        
        public Direct(Switch<K, T, U, R, ?, ?, ?> delegate) {
            super(delegate);
        }

        @Override
        protected S wrapOrReuseStep(Switch<K, T, U, R, ?, ?, ?> newDelegate) {
            return wrapNextStep(newDelegate);
        }
    }
}
