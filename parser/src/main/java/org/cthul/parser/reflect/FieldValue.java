package org.cthul.parser.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.cthul.parser.api.Context;
import org.cthul.parser.util.InstanceMap;

/**
 * @author Arian Treffer
 */
public abstract class FieldValue {
    
    public static FieldValue create(Object impl, Field field){
        if (field.getType() == Void.class) {
            return new Constant(null);
        }
        int mod = field.getModifiers();
        if ((mod & Modifier.FINAL) == 0) {
            // non-final, read field every time
            return new Access(impl, field);
        }
        if ((mod & Modifier.STATIC) == 0 && impl instanceof Class) {
            // instance final field, but no instance given
            return new CachedAccess(impl, field);
        }
        // static or instance is given
        Object value = new Access(impl, field).get(null);
        return new Constant(value);
    }

    public abstract Object get(InstanceMap ctx);

    public static class Constant extends FieldValue {

        private final Object value;

        public Constant(Object value) {
            this.value = value;
        }

        @Override
        public Object get(InstanceMap ctx) {
            return value;
        }
    }

    public static class Access extends FieldValue {

        private final Object instance;
        private final Class<?> clazz;
        private final Field f;

        public Access(Object impl, Field field) {
            if (impl == null) {
                throw new NullPointerException("impl");
            }
            if (impl instanceof Class<?>
                    && (field.getModifiers() & Modifier.STATIC) == 0) {
                // instance will be obtained from the instance map for each invocation
                this.instance = null;
                this.clazz = (Class<?>) impl;
            } else {
                // instance is already provided or method is static
                this.instance = impl;
                this.clazz = null;
            }
            this.f = field;
        }

        @Override
        public Object get(InstanceMap ctx) {
            Object obj = clazz == null ? instance : ctx.getOrCreate(clazz);
            return get(obj);
        }

        private Object get(Object obj) {
            try {
                return f.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Can not access " + f, e);
            } catch (RuntimeException e) {
                throw new RuntimeException("Can not invoke " + f, e);
            }
        }
    }
    
    public static class CachedAccess extends Access {

        private final String key;
        
        public CachedAccess(Object impl, Field field) {
            super(impl, field);
            String key1;
            if (impl instanceof Class) {
                key1 = ((Class) impl).getName();
            } else {
//                int hc = System.identityHashCode(impl);
//                key1 = String.format("%s@%8x", impl.getClass().getName(), hc);
                key1 = impl.getClass().getName();
            }
            key = key1 + "#" + field.getName();
        }

        @Override
        public Object get(InstanceMap ctx) {
            Object cached = ctx.get(key);
            if (cached == null) {
                cached = super.get(ctx);
                ctx.put(key, cached);
            }
            return cached;
        }   
    }
}
