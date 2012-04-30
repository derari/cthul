package org.cthul.parser.grammar.sequence;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Arian Treffer
 */
public class SequenceStrategies {
    
    private static final Map<Class<? extends SequenceStrategy<?,?>>, SequenceStrategy<?,?>> instances = new HashMap<Class<? extends SequenceStrategy<?, ?>>, SequenceStrategy<?, ?>>();

//    @SuppressWarnings("unchecked")
//    public static <E, S> SequenceStrategy<E, S> get_(Class<? extends _SequenceStrategy> rawClazz) {
//        if (!SequenceStrategy.class.isAssignableFrom(rawClazz)) {
//            throw new IllegalArgumentException(rawClazz + 
//                    " does not implement SequenceStrategy");
//        }
//        Class<SequenceStrategy<E, S>> clazz = (Class) rawClazz;
//        return get(clazz);
//    }
    
    @SuppressWarnings("unchecked")
    public static <E, S, C extends SequenceStrategy<E, S>> C get(Class<C> clazz) {
        C strategy = (C) instances.get(clazz);
        if (strategy == null) {
            try {
                strategy = clazz.getConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(clazz + " has no default constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(clazz + " is not public or "
                        + "has no public default constructor", e);
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("Cannot instantiate " + clazz, e);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else {
                    throw new RuntimeException(e.getMessage(), cause);
                }
            }
            instances.put(clazz, strategy);
        }
        return strategy;
    }
    
    protected SequenceStrategies() {
    }
    
}
