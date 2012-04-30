package org.cthul.parser.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.cthul.parser.Context;

/**
 *
 * @author Arian Treffer
 */
public class Instance {
    
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz, Context context) {
        for (Object o: context.values()) {
            if (clazz.isInstance(o)) return (T) o;
        }
        try {
            T o = clazz.getConstructor().newInstance();
            context.put(clazz, o);
            inject(o, context);
            return o;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void inject(Object obj, Context context) {
        try {
            for (Field f: obj.getClass().getFields()) {
                if (f.getAnnotation(Inject.class) != null) {
                    Object value = get(f.getType(), context);
                    f.set(obj, value);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
}
