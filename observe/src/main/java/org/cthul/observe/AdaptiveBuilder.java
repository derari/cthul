package org.cthul.observe;

import java.util.*;
import java.util.function.Function;
import static org.cthul.observe.AdapterFactory.expandClass;

public class AdaptiveBuilder<E> implements Adaptive.Builder<E, AdaptiveBuilder<E>> {
    
    private final E instance;
    private final AdapterFactory<E> adapterFactory;
    private final Map<Class<?>, Object> adapters = new HashMap<>();

    public AdaptiveBuilder(E instance) {
        this(instance, new AdapterFactory<>());
    }
    
    protected AdaptiveBuilder(E instance, AdapterFactory<E> adapterFactory) {
        this.instance = instance;
        this.adapterFactory = adapterFactory;
    }

    public AdaptiveBuilder<E> forInstance(E newInstance) {
        return new AdaptiveBuilder<>(newInstance, adapterFactory);
    }
    
    public AdaptiveBuilder<E> copy() {
        return new AdaptiveBuilder<>(instance, adapterFactory.copy());
    }
    
    public Adaptive build() {
        return copy();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T as(Class<T> clazz, Function<? super E, ? extends T> ifUndeclared) {
        Objects.requireNonNull(clazz, "class");
        if (clazz.isSynthetic()) throw new IllegalArgumentException(clazz + " is synthetic");
        var result = adapters.get(clazz);
        if (result != null) return (T) result;
        result = adapters.values().stream()
                .filter(clazz::isInstance).findAny()
                .orElseGet(() -> createAdapter(clazz, ifUndeclared));
        adapters.putIfAbsent(clazz, result);
        return (T) result;
    }

    @Override
    public <T> AdaptiveBuilder<E> declare(Class<T> clazz, Function<? super E, ? extends T> intf) {
        adapterFactory.declare(clazz, intf);
        return this;
    }

    @Override
    @SafeVarargs
    public final AdaptiveBuilder<E> declare(Function<? super E, ?>... intf) {
        return Builder.super.declare(intf);
    }

    private <T> T createAdapter(Class<T> clazz, Function<? super E, ? extends T> ifUndeclared) {
        return adapterFactory.create(instance, clazz, ifUndeclared, this::addWrapper);
    }

    private void addWrapper(Object wrapper) {
        expandClass(wrapper, wrapper, adapters::putIfAbsent);
    }
}
