package org.cthul.monad.switches;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.CheckedPredicate;

public class MatchedSwitch<K, T, U, R>
        implements BasicSwitch<K, T, U, R>,
                Switch.Case<T, U, BasicSwitch<K, T, U, R>> {

    public static <K, T, U, R> MatchedSwitch<K, T, U, R> withResult(R result) {
        return new MatchedSwitch<>(result);
    }

    private final R result;

    public MatchedSwitch(R result) {
        this.result = result;
    }

    @Override
    public <X extends Exception> Case<T, U, BasicSwitch<K, T, U, R>> ifTrue(CheckedPredicate<? super K, X> condition) throws X {
        return this;
    }

    @Override
    public Case<T, U, R> orElse() {
        return new LastCase<>(result);
    }

    @Override
    public <X extends Exception> BasicSwitch<K, T, U, R> map(CheckedFunction<T, ? extends U, X> function) throws X {
        return this;
    }

    @Override
    public <K2> Switch<K2, T, U, R, ?, ?, ?> mapKey(Function<? super K, ? extends K2> keyMapping) {
        return (Switch) this;
    }

    @Override
    public <T2> Switch<K, T2, U, R, ?, ?, ?> mapSource(Function<? super T, ? extends T2> valueMapping) {
        return (Switch) this;
    }

    @Override
    public <U2> Switch<K, T, U2, R, ?, ?, ?> mapTarget(Function<? super U2, ? extends U> valueMapping) {
        return (Switch) this;
    }

    @Override
    public <U2> BasicSwitch<K, T, U2, R> mapTarget(BiFunction<? super T, ? super U2, ? extends U> valueMapping) {
        return (BasicSwitch) this;
    }

    protected static class LastCase<T, U, R> implements Switch.Case<T, U, R> {

        private final R result;

        public LastCase(R result) {
            this.result = result;
        }

        @Override
        public <X extends Exception> R map(CheckedFunction<T, ? extends U, X> function) throws X {
            return result;
        }
    }
}
