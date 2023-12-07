package org.cthul.observe;

import java.util.function.Function;
import java.util.stream.Stream;

public interface Adaptive {
    
    default <T> T as(Class<T> clazz) {
        return Builder.cast(this, clazz);
    }

    interface Typed<A> extends Adaptive {

        @Override
        default <T> T as(Class<T> clazz) {
            return as(clazz, Builder.cast(clazz));
        }

        <T> T as(Class<T> clazz, Function<? super A, ? extends T> ifUndeclared);


        default <T> T as(Function<? super A, T> intf) {
            return intf.apply(as(Self.clazz(), Self.declaration()).self());
        }

        record Self<A>(A self) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            public static <A> Class<Self<A>> clazz() {
                return (Class) Self.class;
            }

            public static <A> Function<A, Self<A>> declaration() {
                return Self::new;
            }
        }
    }

    interface Builder<A, This> extends Adaptive.Typed<A> {

        Adaptive.Typed<A> build();

        <T> This declare(Class<T> clazz, Function<? super A, ? extends T> intf);

        default This declare(Function<? super A, ?> intf) {
            return declare(null, intf);
        }

        @SuppressWarnings("unchecked")
        default This declare(Function<? super A, ?>... intf) {
            Stream.of(intf).forEach(this::declare);
            return (This) this;
        }

        default <T> T declaringAs(Function<? super A, T> intf) {
            declare(intf);
            return as(intf);
        }
        
        static <T> Function<Object, T> cast(Class<T> clazz) {
            return o -> cast(o, clazz);
        }
        
        static <T> T cast(Object instance, Class<T> clazz) {
            if (clazz == void.class || clazz == Void.class) return null;
            return clazz.cast(instance);
        }
    }
}
