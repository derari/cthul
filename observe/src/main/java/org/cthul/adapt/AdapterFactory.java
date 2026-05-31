package org.cthul.adapt;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.cthul.adapt.AdaptiveBuilder.idString;

public class AdapterFactory<A> {

    private final TypeMap<Function<? super A, ?>> typedFactories = new TypeMap<>();
    private final Set<Class<?>> untypedFactoryClasses = new HashSet<>();
    private final ArrayList<Function<? super A, ?>> newUntypedFactories = new ArrayList<>();

    public AdapterFactory() {
    }

    protected AdapterFactory(AdapterFactory<A> source) {
        source.copyInto(this);
    }

    private synchronized void copyInto(AdapterFactory<A> copy) {
        copy.typedFactories.putAll(typedFactories);
        copy.untypedFactoryClasses.addAll(untypedFactoryClasses);
        copy.newUntypedFactories.addAll(newUntypedFactories);
    }

    public AdapterFactory<A> copy() {
        return new AdapterFactory<>(this);
    }

    public synchronized <T> void declare(Class<T> clazz, Function<? super A, ? extends T> factory) {
        if (clazz == null) {
            declare(factory);
            return;
        }
        typedFactories.putIfAbsent(clazz, factory);
    }

    public synchronized void declare(Function<? super A, ?> factory) {
        if (untypedFactoryClasses.add(factory.getClass())) {
            newUntypedFactories.add(factory);
        }
    }

    public <T> T create(A actual, Class<T> clazz) {
        return create(actual, clazz, o -> {});
    }

    public <T> T create(A actual, Class<T> clazz, Consumer<Object> newAdapterCallback) {
        if (newUntypedFactories.isEmpty()) {
            return adaptWithTyped(actual, clazz);
        }
        return adaptWithUntyped(actual, clazz, newAdapterCallback)
                .orElseGet(() -> adaptWithTyped(actual, clazz));
    }

    protected synchronized <T> Optional<T> adaptWithUntyped(A actual, Class<T> clazz, Consumer<Object> newAdapterCallback) {
        var result = applyUntypedFactories(actual, clazz, newAdapterCallback);
        clearUntypedFactories();
        return Optional.ofNullable(result);
    }

    private <T> T applyUntypedFactories(A actual, Class<T> clazz, Consumer<Object> newAdapterCallback) {
        T result = null;
        for (var factory: newUntypedFactories) {
            var adapter = factory.apply(actual);
            if (putTypedFactory(adapter, factory)) {
                newAdapterCallback.accept(adapter);
                if (result == null && clazz.isInstance(adapter)) {
                    result = clazz.cast(adapter);
                }
            }
        }
        return result;
    }

    private void clearUntypedFactories() {
        var sizeBefore = newUntypedFactories.size();
        newUntypedFactories.clear();
        if (sizeBefore > 32) newUntypedFactories.trimToSize();
        if (untypedFactoryClasses.size() > 1024) {
            untypedFactoryClasses.clear();
        }
    }

    protected boolean putTypedFactory(Object adapter, Function<? super A, ?> factory) {
        if (adapter == null) return false;
        synchronized (this) {
            return typedFactories.putIfAbsent(adapter.getClass(), factory) == null;
        }
    }

    protected <T> T adaptWithTyped(A actual, Class<T> clazz) {
        Function<? super A, T> factory;
        synchronized (this) {
            factory = typedFactories.get(clazz);
        }
        return factory == null ? null : factory.apply(actual);
    }

    protected StringBuilder sizeString(StringBuilder sb) {
        if (newUntypedFactories.isEmpty()) return typedFactories.sizeString(sb);
        return typedFactories.sizeString(sb).append(',').append(newUntypedFactories.size());
    }

    @Override
    public String toString() {
        var sb = idString(this).append('[');
        return sizeString(sb).append(']').toString();
    }
}
