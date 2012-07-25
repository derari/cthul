package org.cthul.parser.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import org.cthul.parser.Context;
import org.cthul.parser.ExecutionException;

/**
 *
 * @author Arian Treffer
 */
public final class Executor<Arg> {
    
    private final Object instance;
    private final Class<?> clazz;
    private final Method m;
    private final ArgMap<Arg>[] argMap;
    private final int[] indexMap;

    public <Hint> Executor(Object impl, Method m, ArgMapFactory<Arg, Hint> mapper, Hint hint) {
        if (impl instanceof Class<?> && 
                (m.getModifiers() & Modifier.STATIC) == 0) {
            this.instance = null;
            this.clazz = (Class<?>) impl;
        } else {
            this.instance = impl;
            this.clazz = null;
        }
        this.m = m;
        final Class<?>[] paramTypes = m.getParameterTypes();
        final Annotation[][] paramAnnotations = m.getParameterAnnotations();
        if (paramTypes.length == 0) {
            this.argMap = null;
            this.indexMap = null;
        } else {
            this.argMap = newArgMap(paramTypes.length);
            this.indexMap = new int[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                Annotation[] annotations = paramAnnotations[i];
                argMap[i] = mapper.create(paramTypes[i], annotations);
                indexMap[i] = mapper.mapIndex(i, annotations, hint);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private ArgMap<Arg>[] newArgMap(int length) {
        return (ArgMap<Arg>[]) new ArgMap<?>[length];
    }
    
    public Object invoke(Context context, Arg arg) {
        final Object[] args;
        if (argMap == null) {
            args = null;
        } else {
            args = new Object[argMap.length];
            for (int i = 0; i < args.length; i++) {
                int srcIndex = indexMap[i];
                args[i] = argMap[i].map(srcIndex, context, arg);
            }
        }
        Object obj = clazz == null ? instance : Instance.get(clazz, context);
        return invoke(obj, args);
    }
    
    private Object invoke(Object obj, Object[] args) {
        try {
            return m.invoke(obj, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new ExecutionException(m + " failed", cause);
            }
        } catch (IllegalAccessException e) {
            throw new ExecutionException("Can not access " + m, e);
        } catch (RuntimeException e) {
            throw new ExecutionException("Can not invoke " + m, e);
        }
    }
    
    public static interface ArgMapFactory<Arg, Hint> {
        
        public ArgMap<Arg> create(Class<?> paramType, Annotation[] annotations);
        
        public int mapIndex(int i, Annotation[] annotations, Hint hint);
        
    }
    
    public static interface ArgMap<Arg> {
        
        public Object map(int i, Context ctx, Arg arg);
        
    }
}
