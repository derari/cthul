package org.cthul.adapt;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.cthul.adapt.AdaptiveBuilder.idString;

public abstract class TypedAdaptiveBase<A, T0 extends A> {

    private final AdaptiveBuilder<A> adaptiveBuilder;

    protected TypedAdaptiveBase() {
        this.adaptiveBuilder = new AdaptiveBuilder<>(self());
    }

    protected TypedAdaptiveBase(TypedAdaptiveBase<A, T0> source) {
        this.adaptiveBuilder = source.adaptiveBuilder.copyForInstance(self());
    }

    @SuppressWarnings("unchecked")
    protected final T0 self() {
        return (T0) this;
    }

    public <T> T as(Class<T> clazz, BiFunction<? super A, ? super Class<T>, ? extends T> ifUndeclared) {
        return adaptiveBuilder.as(clazz, ifUndeclared);
    }

    public <T> T as(Function<? super A, T> adapt) {
        return adapt.apply(self());
    }

    public <T> T0 declare(Class<T> clazz, Function<? super A, ? extends T> adapt) {
        adaptiveBuilder.declare(clazz, adapt);
        return self();
    }

    public T0 declare(Function<? super A, ?> adapt) {
        return declare(null, adapt);
    }

    @SafeVarargs
    public final T0 declare(Function<? super A, ?>... adapt) {
        Stream.of(adapt).forEach(this::declare);
        return self();
    }

    @Override
    public String toString() {
        return idString(this).toString();
    }
}
