package org.cthul.observe;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WrapperDefinitions<E> {

    private final Map<Class<?>, Function<? super E, ?>> typedWrappers = new HashMap<>();
    private final Set<Class<?>> untypedWrapperClasses = new HashSet<>();
    private final ArrayList<Function<? super E, ?>> newUntypedWrappers = new ArrayList<>();

    public synchronized <T> void add(Class<T> clazz, Function<? super E, ? extends T> wrapper) {
        addUnsafe(clazz, wrapper);
    }

    protected void addUnsafe(Class<?> clazz, Function<? super E, ?> wrapper) {
        expandSynthetic(clazz, wrapper, typedWrappers::putIfAbsent);
    }

    public synchronized void add(Function<? super E, ?> wrapper) {
        if (untypedWrapperClasses.add(wrapper.getClass()))
            newUntypedWrappers.add(wrapper);
    }

    public <T> T wrapAs(E actual, Class<T> clazz) {
        return wrapAs(actual, clazz, o -> {});
    }

    public synchronized <T> T wrapAs(E actual, Class<T> clazz, Consumer<Object> extraWrapperCallback) {
        if (newUntypedWrappers.size() > 8) {
            return tryInOrder(clazz,
                    () -> wrapWithUntyped(actual, clazz, extraWrapperCallback),
                    () -> wrapWithTyped(actual, clazz));
        }
        return tryInOrder(clazz,
                    () -> wrapWithTyped(actual, clazz),
                    () -> wrapWithUntyped(actual, clazz, extraWrapperCallback));
    }

    protected <T> T tryInOrder(Class<T> clazz, Supplier<Optional<T>> first, Supplier<Optional<T>> second) {
        Optional<T> opt1 = first.get();
        if (opt1.isPresent()) return opt1.get();
        Optional<T> opt2 = first.get();
        if (opt2.isPresent()) return opt2.get();
        throw new IllegalArgumentException("" + clazz);
    }

    protected <T> Optional<T> wrapWithUntyped(E actual, Class<T> clazz, Consumer<Object> extraWrapperCallback) {
        if (newUntypedWrappers.isEmpty())
            return Optional.empty();
        Object result = null;
        for (Function<? super E, ?> wrapper : newUntypedWrappers) {
            Object wrapped = wrapper.apply(actual);
            addUnsafe(wrapped.getClass(), wrapper);
            extraWrapperCallback.accept(wrapped);
            if (result == null && clazz.isInstance(wrapped)) {
                result = wrapped;
            }
        }
        newUntypedWrappers.clear();
        newUntypedWrappers.trimToSize();
        return Optional.ofNullable((T) result);
    }

    protected <T> Optional<T> wrapWithTyped(E actual, Class<T> clazz) {
        return getTyped(clazz).map(f -> (T) f.apply(actual));
    }

    protected Optional<Function<? super E, ?>> getTyped(Class<?> clazz) {
        return Optional.ofNullable(typedWrappers.computeIfAbsent(clazz, this::findTyped));
    }

    protected Function<? super E, ?> findTyped(Class<?> clazz) {
        return typedWrappers.entrySet().stream()
                .filter(e -> clazz.isAssignableFrom(e.getKey()))
                .findAny()
                .map(e -> e.getValue())
                .orElse(null);
    }

    static <T> void expandSynthetic(Class<?> clazz, T item, BiConsumer<? super Class<?>, ? super T> action) {
        if (!clazz.isSynthetic()) {
            action.accept(clazz, item);
            return;
        }
        action.accept(clazz.getSuperclass(), item);
        Stream.of(clazz.getInterfaces()).forEach(c -> action.accept(c, item));
    }
}
