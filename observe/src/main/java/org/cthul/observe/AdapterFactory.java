package org.cthul.observe;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AdapterFactory<E> {

    private final Map<Class<?>, Function<? super E, ?>> typedAdapters = new HashMap<>();
    private final Set<Class<?>> untypedAdapterClasses = new HashSet<>();
    private final ArrayList<Function<? super E, ?>> newUntypedAdapters = new ArrayList<>();

    public AdapterFactory() {
    }

    protected AdapterFactory(AdapterFactory<E> src) {
        typedAdapters.putAll(src.typedAdapters);
        untypedAdapterClasses.addAll(src.untypedAdapterClasses);
        newUntypedAdapters.addAll(src.newUntypedAdapters);
    }

    public AdapterFactory<E> copy() {
        return new AdapterFactory<>(this);
    }
    
    public synchronized <T> void declare(Class<T> clazz, Function<? super E, ? extends T> adapter) {
        if (clazz == null) {
            declare(adapter);
            return;
        }
        expandSynthetic(clazz, adapter, typedAdapters::putIfAbsent);
    }

    public synchronized void declare(Function<? super E, ?> adapter) {
        if (untypedAdapterClasses.add(adapter.getClass()))
            newUntypedAdapters.add(adapter);
    }

    protected boolean putTypedAdapter(Object adapted, Function<? super E, ?> adapter) {
        var sizeBefore = typedAdapters.size();
        expandClass(adapted, adapter, typedAdapters::putIfAbsent);
        return sizeBefore < typedAdapters.size();
    }

    public <T> T create(E actual, Class<T> clazz) {
        return create(actual, clazz, Adaptive.Builder.cast(clazz), o -> {});
    }

    public synchronized <T> T create(E actual, Class<T> clazz, Function<? super E, ? extends T> ifUndeclared, Consumer<Object> extraAdapterCallback) {
        if (newUntypedAdapters.size() > 8) {
            return tryInOrder(
                    () -> adaptWithUntyped(actual, clazz, extraAdapterCallback),
                    () -> adaptWithTyped(actual, clazz),
                    () -> adaptUndeclared(actual, clazz, ifUndeclared));
        }
        return tryInOrder(
                    () -> adaptWithTyped(actual, clazz),
                    () -> adaptWithUntyped(actual, clazz, extraAdapterCallback),
                    () -> adaptUndeclared(actual, clazz, ifUndeclared));
    }

    protected <T> T tryInOrder(Supplier<Optional<T>> first, Supplier<Optional<T>> second, Supplier<T> third) {
        var opt1 = first.get();
        if (opt1.isPresent()) return opt1.get();
        var opt2 = second.get();
        return opt2.orElseGet(third);
    }

    protected <T> Optional<T> adaptWithUntyped(E actual, Class<T> clazz, Consumer<Object> extraAdapterCallback) {
        if (newUntypedAdapters.isEmpty())
            return Optional.empty();
        Object result = null;
        for (var adapter : newUntypedAdapters) {
            var adapted = adapter.apply(actual);
            if (putTypedAdapter(adapted, adapter)) {
                extraAdapterCallback.accept(adapted);
                if (result == null && clazz.isInstance(adapted)) {
                    result = adapted;
                }
            }
        }
        newUntypedAdapters.clear();
        newUntypedAdapters.trimToSize();
        if (untypedAdapterClasses.size() > 1024) {
            untypedAdapterClasses.clear();
        }
        return Optional.ofNullable(clazz.cast(result));
    }

    @SuppressWarnings("unchecked")
    protected <T> Optional<T> adaptWithTyped(E actual, Class<T> clazz) {
        return getTyped(clazz).map(f -> (T) f.apply(actual));
    }
    
    protected Optional<Function<? super E, ?>> getTyped(Class<?> clazz) {
        return Optional.ofNullable(typedAdapters.computeIfAbsent(clazz, this::findTyped));
    }

    protected Function<? super E, ?> findTyped(Class<?> clazz) {
        return typedAdapters.entrySet().stream()
                .filter(e -> clazz.isAssignableFrom(e.getKey()))
                .findAny()
                .map(Map.Entry::getValue)
                .orElse(null);
    }
    
    protected <T> T adaptUndeclared(E actual, Class<T> clazz, Function<? super E, ? extends T> ifUndeclared) {
        var result = ifUndeclared.apply(actual);
        putTypedAdapter(result, ifUndeclared);
        return result;
    }

    public static <T> void expandClass(Object instance, T item, BiConsumer<? super Class<?>, ? super T> action) {
        if (instance == null) {
            action.accept(void.class, item);
            action.accept(Void.class, item);
            return;
        }
        expandSynthetic(instance.getClass(), item, action);
    }
    
    public static <T> void expandSynthetic(Class<?> clazz, T item, BiConsumer<? super Class<?>, ? super T> action) {
        if (!clazz.isSynthetic()) {
            action.accept(clazz, item);
            return;
        }
        action.accept(clazz.getSuperclass(), item);
        Stream.of(clazz.getInterfaces()).forEach(c -> action.accept(c, item));
    }
    
    private static final Object NO_RESULT = new Object();
}
