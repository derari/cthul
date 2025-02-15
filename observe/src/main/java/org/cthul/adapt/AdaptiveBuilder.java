package org.cthul.adapt;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AdaptiveBuilder<A> implements Adaptive.Builder<A, AdaptiveBuilder<A>> {
    
    private final A instance;
    private final TypeMap<Object> adapters = new TypeMap<>();
    private boolean copyOnWrite;
    private AdapterFactory<A> adapterFactory;

    public AdaptiveBuilder(A instance) {
        this.instance = instance;
        this.adapterFactory = new AdapterFactory<>();
    }

    protected AdaptiveBuilder(AdaptiveBuilder<A> source) {
        this(source.instance, source);
        adapters.putAll(source.adapters);
    }

    protected AdaptiveBuilder(A instance, AdaptiveBuilder<A> source) {
        this.instance = instance;
        this.copyOnWrite = true;
        source.copyOnWrite = true;
        this.adapterFactory = source.adapterFactory;
    }

    public AdaptiveBuilder<A> copy() {
        return new AdaptiveBuilder<>(this);
    }

    public AdaptiveBuilder<A> copyForInstance(A newInstance) {
        return new AdaptiveBuilder<>(newInstance, this);
    }

    @Override
    public Adaptive.Typed<A> build() {
        return copy();
    }

    private AdapterFactory<A> writableFactory() {
        if (copyOnWrite) {
            adapterFactory = adapterFactory.copy();
            copyOnWrite = false;
        }
        return adapterFactory;
    }

    @Override
    public <T> AdaptiveBuilder<A> declare(Class<T> clazz, Function<? super A, ? extends T> adapt) {
        writableFactory().declare(clazz, adapt);
        return this;
    }

    @Override
    @SafeVarargs
    public final AdaptiveBuilder<A> declare(Function<? super A, ?>... adapt) {
        return Builder.super.declare(adapt);
    }

    @Override
    public <T> T as(Class<T> clazz, BiFunction<? super A, ? super Class<T>, ? extends T> ifUndeclared) {
        return adapters.computeIfAbsent(clazz, c -> createAdapter(c, ifUndeclared));
    }

    private <T> T createAdapter(Class<T> clazz, BiFunction<? super A, ? super Class<T>, ? extends T> ifUndeclared) {
        var adapter = adapterFactory.create(instance, clazz, this::addAdapter);
        if (adapter != null) {
            return adapter;
        }
        return ifUndeclared.apply(instance, clazz);
    }

    private void addAdapter(Object adapter) {
        if (adapter != null) adapters.putIfAbsent(adapter.getClass(), adapter);
    }

    @Override
    public String toString() {
        var sb = idString(this).append('[');
        sb = adapterFactory.sizeString(sb).append("]")
                .append('(').append(instance).append('[');
        return adapters.sizeString(sb).append("])").toString();
    }

    protected static StringBuilder idString(Object o) {
        return new StringBuilder(o.getClass().getSimpleName())
                .append('@')
                .append(Integer.toHexString(o.hashCode()));
    }
}
