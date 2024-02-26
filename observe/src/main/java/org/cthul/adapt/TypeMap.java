package org.cthul.adapt;

import java.util.*;
import java.util.function.*;

public class TypeMap<E> {

    private final Map<Class<?>, E> map = new HashMap<>();
    private final List<Class<?>> dirty = new ArrayList<>();

    public void putAll(TypeMap<E> source) {
        source.flushDirty();
        map.putAll(source.map);
    }

    public <E2 extends E> E2 putIfAbsent(Class<?> clazz, E2 value) {
        if (value == null) throw new NullPointerException("value");
        if (clazz == null) throw new NullPointerException("class");
        if (clazz.isSynthetic()) return putSynthetic(clazz, value);
        return putDirty(clazz, value);
    }

    @SuppressWarnings("unchecked")
    private <E2 extends E> E2 putDirty(Class<?> clazz, E2 value) {
        var result = map.putIfAbsent(clazz, value);
        if (result == null) dirty.add(clazz);
        return (E2) result;
    }

    private <E2 extends E> E2 putSynthetic(Class<?> clazz, E2 value) {
        var size = map.size();
        var sup = clazz.getSuperclass();
        if (sup != null) putDirty(sup, value);
        for (var i: clazz.getInterfaces()) {
            putDirty(i, value);
        }
        return size == map.size() ? value : null;
    }

    public <E2 extends E, C> E2 computeIfAbsent(Class<C> clazz, Function<? super Class<C>, ? extends E2> ifMissing) {
        E2 result = get(clazz);
        if (result != null) return result;
        result = ifMissing.apply(clazz);
        if (result != null) {
            E2 previous = putIfAbsent(clazz, result);
            if (previous != null) return previous;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T2 extends E> T2 get(Class<?> clazz) {
        Objects.requireNonNull(clazz, "class");
        if (clazz.isSynthetic()) throw new IllegalArgumentException(clazz + " is synthetic");
        var result = map.get(clazz);
        if (result == null && !dirty.isEmpty()) {
            flushDirty();
            result = map.get(clazz);
        }
        return (T2) result;
    }

    private void flushDirty() {
        dirty.forEach(this::putSupertypes);
        dirty.clear();
    }

    private void putSupertypes(Class<?> clazz) {
        var size = map.size();
        var value = map.get(clazz);
        putSupertypes(clazz, value);
        if (size != map.size()) {
            putClass(PRIMITIVES.get(clazz), value);
            putClass(BOXED.get(clazz), value);
        }
    }

    private void putClass(Class<?> clazz, E value) {
        if (clazz != null && map.putIfAbsent(clazz, value) == null) {
            putSupertypes(clazz, value);
        }
    }

    private void putSupertypes(Class<?> clazz, E value) {
        var sup = clazz.getSuperclass();
        if (sup != null && map.putIfAbsent(sup, value) == null) {
            putSupertypes(sup, value);
        }
        for (var i: clazz.getInterfaces()) {
            putInterface(i, value);
        }
    }

    private void putInterface(Class<?> clazz, E value) {
        if (map.putIfAbsent(clazz, value) == null) {
            for (var i: clazz.getInterfaces()) {
                putInterface(i, value);
            }
        }
    }

    protected String sizeString() {
        if (dirty.isEmpty()) return Integer.toString(map.size());
        return map.size() + "+" + dirty.size();
    }

    @Override
    public String toString() {
        return super.toString() + "[" + sizeString() + "]";
    }

    private static final Map<Class<?>, Class<?>> PRIMITIVES = Map.of(
            Void.class, void.class,
            Byte.class, byte.class,
            Character.class, char.class,
            Short.class, short.class,
            Integer.class, int.class,
            Long.class, long.class,
            Float.class, float.class,
            Double.class, double.class
    );

    private static final Map<Class<?>, Class<?>> BOXED = Map.of(
            void.class, Void.class,
            byte.class, Byte.class,
            char.class, Character.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class
    );
}
