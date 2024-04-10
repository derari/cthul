package org.cthul.adapt;

import java.util.function.*;

public class AdaptiveBuilder<A> implements Adaptive.Builder<A, AdaptiveBuilder<A>> {
    
    private final A instance;
    private final TypeMap<Object> adapters = new TypeMap<>();
    private boolean copyOnWrite;
    private AdapterFactory<A> adapterFactory;

    public AdaptiveBuilder(A instance) {
        this.instance = instance;
        this.adapterFactory = new AdapterFactory<>();
        this.copyOnWrite = false;
    }

    protected AdaptiveBuilder(AdaptiveBuilder<A> source) {
        this(source.instance, source);
        adapters.putAll(source.adapters);
    }

    protected AdaptiveBuilder(A instance, AdaptiveBuilder<A> source) {
        this.instance = instance;
        this.adapterFactory = source.adapterFactory;
        this.copyOnWrite = true;
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
        writableFactory().declare(clazz, a -> ifUndeclared.apply(a, clazz));
        return ifUndeclared.apply(instance, clazz);
    }

    private void addAdapter(Object adapter) {
        if (adapter != null) adapters.putIfAbsent(adapter.getClass(), adapter);
    }

    @Override
    public String toString() {
        return super.toString()
                + "[" + adapterFactory.sizeString() + "]"
                + "(" + instance + "[" + adapters.sizeString() + "])";
    }
}
