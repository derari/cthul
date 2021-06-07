package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.CheckedPredicate;

import static org.cthul.monad.switches.SwitchDelegator.identity;

public class ValueSwitch<K, T> 
        implements BasicSwitch.DirectIdentity<K, T>,
                Switch.IdentityCase<T, BasicSwitch.Identity<K, T, T>> {

    public static <K, T> ValueSwitch<K, T> choose(K key, T value) {
        return new ValueSwitch<>(key, value);
    }

    public static <T> ValueSwitch<T, T> choose(T value) {
        return new ValueSwitch<>(value, value);
    }
    
    private final K key;
    private final T value;
    private final BasicSwitch.Identity<K, T, T> basic = BasicSwitch.asIdentitySwitch(this);

    public ValueSwitch(K key, T value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public <X extends Exception> IdentityCase<T, BasicSwitch.Identity<K, T, T>> ifTrue(CheckedPredicate<? super K, X> condition) throws X {
        if (condition.test(key)) {
            return new MatchCase<>(value, this::matched);
        }
        return this;
    }

    private BasicSwitch.Identity<K, T, T> matched(T value) {
        return BasicSwitch.asIdentitySwitch(MatchedSwitch.withResult(value));
    }
    
    @Override
    public IdentityCase<T, T> orElse() {
        return new MatchCase<>(value, identity());
    }

    @Override
    public <X extends Exception> BasicSwitch.Identity<K, T, T> map(CheckedFunction<T, T, X> function) throws X {
        return basic;
    }
    
    protected static class MatchCase<T, S> implements Switch.IdentityCase<T, S> {
        
        private final T value;
        private final Function<T, S> resultMapping;

        public MatchCase(T value, Function<T, S> resultMapping) {
            this.value = value;
            this.resultMapping = resultMapping;
        }

        @Override
        public <X extends Exception> S map(CheckedFunction<T, T, X> function) throws X {
            T newValue = function.apply(value);
            return resultMapping.apply(newValue);
        }
    }
}
