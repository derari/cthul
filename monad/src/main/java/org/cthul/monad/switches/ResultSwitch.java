package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.CheckedPredicate;
import org.cthul.monad.function.CheckedSupplier;

public interface ResultSwitch<K, T, U,
                        X extends Exception, R,
                        S extends ResultSwitch<K, T, U, X, R, S, ?, ?>,
                        C1 extends ResultSwitch.Case<T, U, X, S>,
                        C2 extends ResultSwitch.Case<T, U, X, R>>
        extends Switch<K, Unsafe<T, X>, Unsafe<U, X>, R, S, C1, C2> {

    @Override
    default <K2> ResultSwitch<K2, T, U, X, R, ?, ?, ?> mapKey(CheckedPredicate.Adapter<? super K, ? extends K2> conditionAdapter) {
        return BasicResultSwitch.wrap(Switch.super.mapKey(conditionAdapter));
    }

    @Override
    default <K2> ResultSwitch<K2,T, U, X, R, ?, ?, ?> mapKey(Function<? super K, ? extends K2> keyMapping) {
        return BasicResultSwitch.wrap(Switch.super.mapKey(keyMapping));
    }

    interface Case<T, U, X extends Exception, S>
            extends Switch.Case<Unsafe<T, X>, Unsafe<U, X>, S>,
                    ResultChoices<T, U, X, EndNestedSwitch<S>> {

        default <X2 extends Exception> S mapIfPresent(CheckedFunction<T, U, X2> function) throws X2 {
            return map(unsafe -> unsafe.map(function));
        }

        default <X2 extends Exception> S setIfPresent(CheckedSupplier<U, X2> supplier) throws X2 {
            return map(unsafe -> unsafe.map(t -> supplier.get()));
        }

        default S setIfPresent(U value) {
            return map(unsafe -> unsafe.map(t -> value));
        }

        default <X2 extends Exception> S setValue(CheckedSupplier<U, X2> supplier) throws X2 {
            return map(unsafe -> unsafe.getScope().value(supplier.get()).unsafe());
        }

        default S setValue(U value) {
            return map(unsafe -> unsafe.getScope().value(value).unsafe());
        }

        @Override
        default ResultSwitch<Unsafe<T, X>, T, U, X, EndNestedSwitch<S>, ?, ?, ?> choose() {
            return BasicResultSwitch.wrap(SwitchFunctionBuilder.functionBuilder(function -> () -> map(function)));
        }
    }

    interface EndNestedSwitch<S> {

        S end() throws Exception;

        default <X extends Exception> S end(Class<X> exceptionType) throws X {
            try {
                return end();
            } catch (Exception ex) {
                if (exceptionType.isInstance(ex)) {
                    throw exceptionType.cast(ex);
                }
                throw new RuntimeException(ex);
            }
        }

        default S endUnchecked() {
            return end(RuntimeException.class);
        }
    }

    interface Identity<K, T, X extends Exception, R,
                            S extends Identity<K, T, X, R, S, ?, ?>,
                            C1 extends IdentityCase<T, X, S>,
                            C2 extends IdentityCase<T, X, R>>
            extends ResultSwitch<K, T, T, X, R, S, C1, C2>,
                    Switch.Identity<K, Unsafe<T, X>, R, S, C1, C2> {

        @Override
        default <K2> ResultSwitch.Identity<K2, T, X, R, ?, ?, ?> mapKey(Function<? super K, ? extends K2> keyMapping) {
            return BasicResultSwitch.wrapIdentity(ResultSwitch.super.mapKey(keyMapping));
        }

        @Override
        default <K2> ResultSwitch.Identity<K2, T, X, R, ?, ?, ?> mapKey(CheckedPredicate.Adapter<? super K, ? extends K2> conditionAdapter) {
            return BasicResultSwitch.wrapIdentity(ResultSwitch.super.mapKey(conditionAdapter));
        }
    }

    interface IdentityCase<T, X extends Exception, S>
            extends Case<T, T, X, S>,
                    Switch.IdentityCase<Unsafe<T, X>, S> {
    }

    interface Direct<K, T, U, X extends Exception,
                        S extends ResultSwitch<K, T, U, X, Unsafe<U, X>, S, ?, ?>,
                        C1 extends ResultSwitch.Case<T, U, X, S>,
                        C2 extends ResultSwitch.Case<T, U, X, Unsafe<U, X>>>
            extends ResultSwitch<K, T, U, X, Unsafe<U, X>, S, C1, C2>,
                    Switch.Direct<K, Unsafe<T, X>, Unsafe<U, X>, S, C1, C2> {
    }

    interface DirectIdentity<K, T, X extends Exception,
                        S extends ResultSwitch.Identity<K, T, X, Unsafe<T, X>, S, ?, ?>,
                        C1 extends ResultSwitch.IdentityCase<T, X, S>,
                        C2 extends ResultSwitch.IdentityCase<T, X, Unsafe<T, X>>>
            extends ResultSwitch.Identity<K, T, X, Unsafe<T, X>, S, C1, C2>,
                    ResultSwitch.Direct<K, T, T, X, S, C1, C2>,
                    Switch.DirectIdentity<K, Unsafe<T, X>, S, C1, C2> {

        @Override
        default <K2> ResultSwitch.DirectIdentity<K2, T, X, ?, ?, ?> mapKey(Function<? super K, ? extends K2> keyMapping) {
            return mapKey(CheckedPredicate.Adapter.map(keyMapping));
        }

        @Override
        default <K2> ResultSwitch.DirectIdentity<K2, T, X, ?, ?, ?> mapKey(CheckedPredicate.Adapter<? super K, ? extends K2> conditionAdapter) {
            return BasicResultSwitch.overrideResultOfUnmatchedIdentity(
                    wrapWith(self -> new ConditionAdaptingSwitch<>(self, conditionAdapter)));
        }
    }
}
