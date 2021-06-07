package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.CheckedPredicate;

public abstract class SwitchDelegator<K, T, U, R, S extends Switch<K, T, U, R, S, ?, ?>, 
                    C1 extends Switch.Case<T, U, S>, 
                    C2 extends Switch.Case<T, U, R>, D>
        implements Switch<K, T, U, R, S, C1, C2> {
    
    private final D delegate;
    private final Function<D, S> wrapNextStep = this::wrapOrReuseStep;
    
    private Object case1CacheKey1 = null;
    private C1 case1CacheValue1 = null;
    private Object case1CacheKey2 = null;
    private C1 case1CacheValue2 = null;

    public SwitchDelegator(D delegate) {
        this.delegate = delegate;
    }

    protected D getDelegate() {
        return delegate;
    }
    
    protected Function<D, S> wrapNextStep() {
        return wrapNextStep;
    }
    
    protected S wrapOrReuseStep(D newDelegate) {
        if (newDelegate == delegate) {
            return (S) this;
        }
        return wrapNextStep(newDelegate);
    }
    
    protected abstract S wrapNextStep(D newDelegate);

    @Override
    public <X extends Exception> C1 ifTrue(CheckedPredicate<? super K, X> condition) throws X {
        return ifTrue(delegate, condition);
    }

    @Override
    public C2 orElse() {
        return orElse(delegate);
    }

    protected abstract <X extends Exception> C1 ifTrue(D delegate, CheckedPredicate<? super K, X> condition) throws X;
    
    protected abstract C2 orElse(D delegate);
    
    protected <T> C1 cached(T key, Function<? super T, ? extends C1> function) {
        if (case1CacheKey1 == key) {
            return case1CacheValue1;
        }
        if (case1CacheKey1 == null) {
            case1CacheKey1 = key;
            return case1CacheValue1 = function.apply(key);
        }
        if (case1CacheKey2 == key) {
            return case1CacheValue2;
        }
        if (case1CacheKey2 == null) {
            case1CacheKey2 = key;
            return case1CacheValue2 = function.apply(key);
        }
        return function.apply(key);
    }
    
    protected static class CaseDelegator<T, U, S0, S1> implements Switch.Case<T, U, S1> {
        
        private final Switch.Case<T, U, S0> delegate;
        private final Function<? super S0, ? extends S1> resultMapping;

        public CaseDelegator(Case<T, U, S0> delegate, Function<? super S0, ? extends S1> resultMapping) {
            this.delegate = delegate;
            this.resultMapping = resultMapping;
        }

        @Override
        public <X extends Exception> S1 map(CheckedFunction<T, U, X> function) throws X {
            S0 delegateResult = delegate.map(function);
            return resultMapping.apply(delegateResult);
        }
    }
    
    private static final Function<?, ?> IDENTITY = Function.identity();
    
    protected static <T> Function<T, T> identity() {
        return (Function) IDENTITY;
    }
}
