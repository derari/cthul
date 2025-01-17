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
        synchronized (source.newUntypedFactories) {
            synchronized (source.typedFactories) {
                typedFactories.putAll(source.typedFactories);
                untypedFactoryClasses.addAll(source.untypedFactoryClasses);
                newUntypedFactories.addAll(source.newUntypedFactories);
            }
        }
    }

    public AdapterFactory<A> copy() {
        return new AdapterFactory<>(this);
    }

    public <T> void declare(Class<T> clazz, Function<? super A, ? extends T> factory) {
        if (clazz == null) {
            declare(factory);
            return;
        }
        synchronized (typedFactories) {
            typedFactories.putIfAbsent(clazz, factory);
        }
    }

    public void declare(Function<? super A, ?> factory) {
        synchronized (newUntypedFactories) {
            if (untypedFactoryClasses.add(factory.getClass())) {
                newUntypedFactories.add(factory);
            }
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

    protected <T> Optional<T> adaptWithUntyped(A actual, Class<T> clazz, Consumer<Object> newAdapterCallback) {
        synchronized (newUntypedFactories) {
            T result = applyUntypedFactories(actual, clazz, newAdapterCallback);
            clearUntypedFactories();
            return Optional.ofNullable(result);
        }
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
        synchronized (typedFactories) {
            return typedFactories.putIfAbsent(adapter.getClass(), factory) == null;
        }
    }

    protected <T> T adaptWithTyped(A actual, Class<T> clazz) {
        Function<? super A, T> factory;
        synchronized (typedFactories) {
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
