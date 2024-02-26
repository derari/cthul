package org.cthul.adapt;

import java.util.function.*;

public class AdaptiveBuilder<E> implements Adaptive.Builder<E, AdaptiveBuilder<E>> {
    
    private final E instance;
    private final TypeMap<Object> adapters = new TypeMap<>();
    private boolean copyOnWrite;
    private AdapterFactory<E> adapterFactory;

    public AdaptiveBuilder(E instance) {
        this.instance = instance;
        this.adapterFactory = new AdapterFactory<>();
        this.copyOnWrite = false;
    }

    protected AdaptiveBuilder(AdaptiveBuilder<E> source) {
        this(source.instance, source);
        adapters.putAll(source.adapters);
    }

    protected AdaptiveBuilder(E instance, AdaptiveBuilder<E> source) {
        this.instance = instance;
        this.adapterFactory = source.adapterFactory;
        this.copyOnWrite = true;
    }

    public AdaptiveBuilder<E> copy() {
        return new AdaptiveBuilder<>(this);
    }

    public AdaptiveBuilder<E> copyForInstance(E newInstance) {
        return new AdaptiveBuilder<>(newInstance, this);
    }

    @Override
    public Adaptive.Typed<E> build() {
        return copy();
    }

    private AdapterFactory<E> writableFactory() {
        if (copyOnWrite) {
            adapterFactory = adapterFactory.copy();
            copyOnWrite = false;
        }
        return adapterFactory;
    }

    @Override
    public <T> AdaptiveBuilder<E> declare(Class<T> clazz, Function<? super E, ? extends T> adapt) {
        writableFactory().declare(clazz, adapt);
        return this;
    }

    @Override
    @SafeVarargs
    public final AdaptiveBuilder<E> declare(Function<? super E, ?>... adapt) {
        return Builder.super.declare(adapt);
    }

    @Override
    public <T> T as(Class<T> clazz, BiFunction<? super E, ? super Class<T>, ? extends T> ifUndeclared) {
        return adapters.computeIfAbsent(clazz, c -> createAdapter(c, ifUndeclared));
    }

    private <T> T createAdapter(Class<T> clazz, BiFunction<? super E, ? super Class<T>, ? extends T> ifUndeclared) {
        var adapter = adapterFactory.create(instance, clazz, this::addAdapter);
        if (adapter != null) {
            return adapter;
        }
        return writableFactory().createUndeclared(instance, clazz, ifUndeclared);
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
