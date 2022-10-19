package org.cthul.observe;

import java.util.function.Function;
import java.util.stream.Stream;

public interface Adaptive {
    
    default <T> T as(Class<T> clazz) {
        return Builder.cast(this, clazz);
    }
    
    interface Builder<A, This> extends Adaptive {
    
        @Override
        default <T> T as(Class<T> clazz) {
            return as(clazz, Builder.cast(clazz));
        }

        <T> T as(Class<T> clazz, Function<? super A, ? extends T> ifUndeclared);

        <T> This declare(Class<T> clazz, Function<? super A, ? extends T> intf);

        default <T> T as(Function<? super A, T> intf) {
            return intf.apply(as(AdapterFactory.Self.clazz(), AdapterFactory.Self.declaration()).getSelf());
        }

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
            if (clazz.isInstance(instance)) return clazz.cast(instance);
            if (clazz == void.class || clazz == Void.class) return null;
            throw new IllegalArgumentException(""+ clazz);
        }
    }
}
