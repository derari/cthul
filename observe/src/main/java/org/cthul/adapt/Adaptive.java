package org.cthul.adapt;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Adaptive {
    
    <T> T as(Class<T> clazz);

    interface Typed<A> extends Adaptive {

        @Override
        default <T> T as(Class<T> clazz) {
            return as(clazz, Builder.cast());
        }

        <T> T as(Class<T> clazz, BiFunction<? super A, ? super Class<T>, ? extends T> ifUndeclared);

        default <T> T as(Function<? super A, T> adapt) {
            return adapt.apply(as(Self.clazz(), Self.wrapper()).self());
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        record Self<A>(A self) {
            public static <A> Class<Self<A>> clazz() {
                return (Class) Self.class;
            }

            public static <A> BiFunction<A, Class<Self<A>>, Self<A>> wrapper() {
                return (BiFunction) WRAP;
            }

            private static final BiFunction<Object, Class<?>, ?> CAST = Builder::cast;
            private static final BiFunction<Object, Class<?>, ?> WRAP = (a, c) -> new Self<>(a);
        }
    }

    interface Builder<A, T0 extends Builder<A, T0>> extends Adaptive.Typed<A> {

        Adaptive.Typed<A> build();

        <T> T0 declare(Class<T> clazz, Function<? super A, ? extends T> adapt);

        default T0 declare(Function<? super A, ?> adapt) {
            return declare(null, adapt);
        }

        @SuppressWarnings("unchecked")
        default T0 declare(Function<? super A, ?>... adapt) {
            Stream.of(adapt).forEach(this::declare);
            return (T0) this;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        static <T> BiFunction<Object, Class<T>, T> cast() {
            return (BiFunction) Typed.Self.CAST;
        }

        static <T> T cast(Object instance, Class<T> clazz) {
            if (clazz == void.class || clazz == Void.class) return null;
            return clazz.cast(instance);
        }
    }
}
