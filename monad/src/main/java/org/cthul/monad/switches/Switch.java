package org.cthul.monad.switches;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.cthul.monad.Status;
import org.cthul.monad.function.*;

public interface Switch<K, T, U, R, S extends Switch<K, T, U, R, S, ?, ?>,
                    C1 extends Switch.Case<T, U, S>,
                    C2 extends Switch.Case<T, U, R>> {

    static <K, T> BasicSwitch.DirectIdentity<K, T> choose(K key, T value) {
        return ValueSwitch.choose(key, value);
    }

    static <T> BasicSwitch.DirectIdentity<T, T> choose(T value) {
        return ValueSwitch.choose(value);
    }

    <X extends Exception> C1 ifTrue(CheckedPredicate<? super K, X> condition) throws X;

    default <X extends Exception> C1 ifFalse(CheckedPredicate<? super K, X> condition) throws X {
        return ifTrue(k -> !condition.test(k));
    }

    default C1 ifEqual(K value) {
        return ifTrue(equalsKey(value));
    }

    default C1 ifNotEqual(K value) {
        return ifFalse(equalsKey(value));
    }

    default C1 ifInstanceOf(Class<? extends K> clazz) {
        return ifTrue(clazz::isInstance);
    }

    default C1 ifNotInstanceOf(Class<? extends K> clazz) {
        return ifFalse(clazz::isInstance);
    }

    @SuppressWarnings("Convert2Lambda")
    default <V, X extends Exception> Test<V, C1, X> ifValue(CheckedFunction<? super K, ? extends V, X> mapping) {
        return new Test<V, C1, X>() {
            @Override
            public <X2 extends Exception> C1 is(CheckedPredicate<? super V, X2> condition) throws X, X2 {
                try {
                    return ifTrue(k -> condition.test(mapping.apply(k)));
                } catch (Exception ex) {
                    throw (X) ex;
                }
            }
            @Override
            public <X2 extends Exception> C1 isNot(CheckedPredicate<? super V, X2> condition) throws X, X2 {
                try {
                    return ifFalse(k -> condition.test(mapping.apply(k)));
                } catch (Exception ex) {
                    throw (X) ex;
                }
            }
        };
    }

    C2 orElse();

    default <K2> Switch<K2, T, U, R, ?, ?, ?> mapKey(CheckedPredicate.Adapter<? super K, ? extends K2> conditionAdapter) {
        return SwitchOperations.mapKey(this, conditionAdapter);
    }

    default <K2> Switch<K2, T, U, R, ?, ?, ?> mapKey(Function<? super K, ? extends K2> keyMapping) {
        return SwitchOperations.mapKey(this, keyMapping);
    }

    default StatusSwitch<T, U, R, ?, ?, ?> mapKeyToStatus(CheckedPredicate.Adapter<? super K, ? extends Status> conditionAdapter) {
        return BasicStatusSwitch.asBasicStatusSwitch(mapKey(conditionAdapter));
    }

    default StatusSwitch<T, U, R, ?, ?, ?> mapKeyToStatus(Function<? super K, ? extends Status> keyMapping) {
        return BasicStatusSwitch.asBasicStatusSwitch(mapKey(keyMapping));
    }

    default <T2> Switch<K, T2, U, R, ?, ?, ?> mapSource(Function<? super T, ? extends T2> valueMapping) {
        return SwitchOperations.mapSource(this, valueMapping);
    }

    default Switch.Identity<K, U, R, ?, ?, ?> mapSourceToTarget(Function<? super T, ? extends U> valueMapping) {
        return BasicSwitch.asIdentitySwitch(mapSource(valueMapping));
    }

    default <U2> Switch<K, T, U2, R, ?, ?, ?> mapTarget(Function<? super U2, ? extends U> valueMapping) {
        return SwitchOperations.mapTarget(this, valueMapping);
    }

    default <U2> Switch<K, T, U2, R, ?, ?, ?> mapTarget(BiFunction<? super T, ? super U2, ? extends U> valueMapping) {
        return SwitchOperations.mapTarget(this, valueMapping);
    }

    default Switch.Identity<K, T, R, ?, ?, ?> mapTargetFromSource(Function<? super T, ? extends U> valueMapping) {
        return BasicSwitch.asIdentitySwitch(mapTarget(valueMapping));
    }

    default Switch.Identity<K, T, R, ?, ?, ?> mapTargetFromSource(BiFunction<? super T, ? super T, ? extends U> valueMapping) {
        return BasicSwitch.asIdentitySwitch(mapTarget(valueMapping));
    }

    default <R2> Switch<K, T, U, R2, ?, ?, ?> mapResult(Function<? super R, ? extends R2> resultMapping) {
        return SwitchOperations.mapResult(this, resultMapping);
    }

    default BasicSwitch<K, T, U, R> asBasicSwitch() {
        return as(BasicSwitch.basicSwitch());
    }

    default BasicSwitch.Identity<K, U, R> asIdentitySwitch(Function<? super Switch<K, T, U, R, ?, ?, ?>, ? extends BasicSwitch.Identity<K, U, R>> adapter) {
        return as(adapter);
    }

    default BasicSwitch.Status<T, U, R> asStatusSwitch(Function<? super Switch<K, T, U, R, ?, ?, ?>, ? extends BasicSwitch.Status<T, U, R>> adapter) {
        return as(adapter);
    }

    default BasicSwitch.StatusIdentity<U, R> asStatusIdentitySwitch(Function<? super Switch<K, T, U, R, ?, ?, ?>, ? extends BasicSwitch.StatusIdentity<U, R>> adapter) {
        return as(adapter);
    }

    default <A> A as(Function<? super Switch<K, T, U, R, ?, ?, ?>, ? extends A> adapter) {
        return wrapWith(adapter);
    }

    default <A> A wrapWith(Function<? super Switch<K, T, U, R, ?, ?, ?>, ? extends A> adapter) {
        return adapter.apply(this);
    }

    interface Case<T, U, S> {

        <X extends Exception> S map(CheckedFunction<T, ? extends U, X> mapping) throws X;

        default <X extends Exception> S set(CheckedSupplier<? extends U, X> source) throws X {
            return map(t -> source.get());
        }

        default S set(U value) {
            return map(t -> value);
        }
    }

    interface Identity<K, T, R, S extends Identity<K, T, R, S, ?, ?>,
                        C1 extends IdentityCase<T, S>,
                        C2 extends IdentityCase<T, R>>
            extends Switch<K, T, T, R, S, C1, C2> {

        default R orDefault() {
            return orElse().keep();
        }
    }

    interface IdentityCase<T, S> extends Case<T, T, S> {

        default <X extends Exception> S call(CheckedConsumer<T, X> callback) throws X {
            return map(t -> { callback.accept(t); return t; });
        }

        default <X extends Exception> S run(CheckedRunnable<X> action) throws X {
            return map(t -> { action.run(); return t; });
        }

        default S keep() {
            return map(t -> t);
        }
    }

    interface Direct<K, T, U, S extends Switch<K, T, U, U, S, ?, ?>,
                        C1 extends Case<T, U, S>,
                        C2 extends Case<T, U, U>>
            extends Switch<K, T, U, U, S, C1, C2> {

        @Override
        default <T2> Direct<K, T2, U, ?, ?, ?> mapSource(Function<? super T, ? extends T2> valueMapping) {
            Switch<K, T2, ?, ?, ?, ?, ?> adapted = Switch.super.mapSource(valueMapping);
            return DirectResultMappingSwitch.overrideResultOfUnmatchedSwitch(adapted);
        }

        @Override
        default DirectIdentity<K, U, ?, ?, ?> mapSourceToTarget(Function<? super T, ? extends U> valueMapping) {
            return DirectIdentitySwitch.wrap(mapSource(valueMapping));
        }

        default <U2> Direct<K, T, U2, ?, ?, ?> mapResult() {
            return DirectResultMappingSwitch.mapResult(this);
        }

        default <U> Direct<K, T, U, ?, ?, ?> mapResultTo(Class<U> clazz) {
            return mapResult();
        }
    }

    interface DirectIdentity<K, T, S extends Identity<K, T, T, S, ?, ?>,
                        C1 extends IdentityCase<T, S>,
                        C2 extends IdentityCase<T, T>>
            extends Direct<K, T, T, S, C1, C2>,
                    Identity<K, T, T, S, C1, C2> {
    }

    interface Test<T, C, X extends Exception> {

        default C equalTo(Object other) throws X {
            return is(equalsKey(other));
        }

        default C notEqualTo(Object other) throws X {
            return isNot(equalsKey(other));
        }

        <X2 extends Exception> C is(CheckedPredicate<? super T, X2> condition) throws X, X2;

        <X2 extends Exception> C isNot(CheckedPredicate<? super T, X2> condition) throws X, X2;
    }

    static UncheckedPredicate<Object, RuntimeException> equalsKey(Object value) {
        if (value == null) {
            return Objects::isNull;
        }
        return value::equals;
    }
}
