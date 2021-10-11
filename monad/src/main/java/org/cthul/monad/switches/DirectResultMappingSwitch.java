package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.CheckedPredicate;
import org.cthul.monad.util.SafeStrings;

public class DirectResultMappingSwitch<K, T, U1>
        extends SwitchDelegator<K, T, U1, U1,
                        BasicSwitch<K, T, U1, U1>,
                        Switch.Case<T, U1, BasicSwitch<K, T, U1, U1>>,
                        Switch.Case<T, U1, U1>,
                        Switch<K, T, ?, ?, ?, ?, ?>>
        implements BasicSwitch.Direct<K, T, U1> {

    public static <K, T, U> BasicSwitch.Direct<K, T, U> overrideResultOfUnmatchedSwitch(Switch<K, T, ?, ?, ?, ?, ?> delegate) {
        return new DirectResultMappingSwitch<>(delegate);
    }

    public static <K, T, U> BasicSwitch.Direct<K, T, U> mapResult(Switch.Direct<K, T, ?, ?, ?, ?> delegate) {
        return new DirectResultMappingSwitch<>(delegate);
    }

    private final Function<Object, BasicSwitch<K, T, U1, U1>> selfAsBasicSwitch = this::asBasic;
    private BasicSwitch<K, T, U1, U1> basic = null;

    public DirectResultMappingSwitch(Switch.Direct<K, T, ?, ?, ?, ?> delegate) {
        super(delegate);
    }

    private DirectResultMappingSwitch(Switch<K, T, ?, ?, ?, ?, ?> delegate) {
        super(delegate);
    }

    private BasicSwitch<K, T, U1, U1> asBasic(Object o) {
        return basic == null ? basic = asBasicSwitch() : basic;
    }

    @Override
    protected <X extends Exception> Case<T, U1, BasicSwitch<K, T, U1, U1>> ifTrue(Switch<K, T, ?, ?, ?, ?, ?> delegate, CheckedPredicate<? super K, X> condition) throws X {
        return cached(delegate.ifTrue(condition), this::newCase);
    }

    private Case<T, U1, BasicSwitch<K, T, U1, U1>> newCase(Case<T, ?, ? extends Switch<K, T, ?, ?, ?, ?, ?>> case1) {
        return new ValueMappingCase<>(case1, selfAsBasicSwitch, wrapMatch());
    }

    @Override
    protected BasicSwitch<K, T, U1, U1> wrapNextStep(Switch<K, T, ?, ?, ?, ?, ?> newDelegate) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Case<T, U1, U1> orElse(Switch<K, T, ?, ?, ?, ?, ?> delegate) {
        return new ValueMappingCase<>(delegate.orElse(), matchExpected(), SwitchDelegator.identity());
    }

    @Override
    public <U2> Direct<K, T, U2, ?, ?, ?> mapResult() {
        return (Direct) this;
    }

    protected static class ValueMappingCase<T, U, S0, S1> implements Switch.Case<T, U, S1> {

        private final Switch.Case<T, ?, S0> delegate;
        private final Function<? super S0, ? extends S1> stepMapping;
        private final Function<? super U, ? extends S1> matchMapping;

        public ValueMappingCase(Case<T, ?, S0> delegate, Function<? super S0, ? extends S1> stepMapping, Function<? super U, ? extends S1> matchMapping) {
            this.delegate = delegate;
            this.stepMapping = stepMapping;
            this.matchMapping = matchMapping;
        }

        @Override
        public <X extends Exception> S1 map(CheckedFunction<T, ? extends U, X> function) throws X {
            Interceptor<U> interceptor = new Interceptor<>();
            S0 delegateResult = delegate.map(interceptor.wrap(function));
            return interceptor.mapResult(delegateResult, stepMapping, matchMapping);
        }
    }

    private static class Interceptor<U> {
        private boolean matched = false;
        private U value = null;

        public <T, U0, X extends Exception> CheckedFunction<T, U0, X> wrap(CheckedFunction<T, ? extends U, X> function) {
            return t -> {
                matched = true;
                value = function.apply(t);
                return null;
            };
        }

        public <S0, S1> S1 mapResult(S0 delegateResult, Function<? super S0, ? extends S1> stepMapping, Function<? super U, ? extends S1> matchMapping) {
            if (matched) {
                return matchMapping.apply(value);
            }
            return stepMapping.apply(delegateResult);
        }
    }

    private static final Function<?, ?> WRAP_MATCH = MatchedSwitch::new;
    private static final Function<?, ?> MATCH_EXPECTED = value -> { throw new IllegalStateException("Unmatched value: " + SafeStrings.toString(value)); };

    protected static <K, T, U> Function<U, BasicSwitch<K, T, U, U>> wrapMatch() {
        return (Function) WRAP_MATCH;
    }

    protected static <T> Function<Object, T> matchExpected() {
        return (Function) MATCH_EXPECTED;
    }
}
