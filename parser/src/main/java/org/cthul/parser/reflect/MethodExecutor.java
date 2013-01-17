package org.cthul.parser.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.cthul.parser.api.Context;

/**
 * Executes a method, mapping a single argument on the method's parameters.
 * @author Arian Treffer
 * @param <Arg> argument
 */
public final class MethodExecutor<Arg> {
    
    private final Object instance;
    private final Class<?> clazz;
    private final Method m;
    private final ArgMap<? super Arg, ?>[] argMap;
    private final int[] indexMap;

    /**
     * 
     * @param <Hint> type of the argument mapping hint
     * @param impl the instance that should be invoked, or the class that implements the {@code method}
     * @param method the method to be invoked
     * @param mapper creates the map that will map the argument to the method's parameters
     * @param hint a hint for the {@code mapper}
     */
    public <Hint> MethodExecutor(Object impl, Method method, ArgMapFactory<? super Arg, ? super Hint> mapper, Hint hint) {
        if (impl == null) {
            throw new NullPointerException("impl");
        }
        if (impl instanceof Class<?> && 
                (method.getModifiers() & Modifier.STATIC) == 0) {
            // instance will be obtained from the instance map for each invocation
            this.instance = null;
            this.clazz = (Class<?>) impl;
        } else {
            // instance is already provided or method is static
            this.instance = impl;
            this.clazz = null;
        }
        this.m = method;
        final Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) {
            this.argMap = null;
            this.indexMap = null;
        } else {
            this.argMap = newArgMap(paramTypes.length);
            this.indexMap = new int[paramTypes.length];
            initArgMap(method, paramTypes, mapper, hint);
        }
    }
    
    protected <Hint> void initArgMap(Method method, final Class<?>[] paramTypes, ArgMapFactory<? super Arg, ? super Hint> mapper, Hint hint) {
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < paramTypes.length; i++) {
            Annotation[] annotations = paramAnnotations[i];
            ArgMap<? super Arg, ? super Hint> arg = mapper.create(paramTypes[i], annotations, hint);
            if (arg == null) throw mapFailed(method, i, paramTypes[i]);
            argMap[i] = arg;
            indexMap[i] = arg.mapIndex(i, annotations, hint);
        }
    }
    
    @SuppressWarnings("unchecked")
    private ArgMap<Arg, Object>[] newArgMap(int length) {
        return new ArgMap[length];
    }
    
    public Object invoke(Context<?> ctx, Arg arg) {
        final Object[] args;
        if (argMap == null) {
            args = null;
        } else {
            args = new Object[argMap.length];
            for (int i = 0; i < args.length; i++) {
                int srcIndex = indexMap[i];
                args[i] = argMap[i].map(srcIndex, ctx, arg);
            }
        }
        Object obj = clazz == null ? instance : ctx.getOrCreate(clazz);
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
                throw new RuntimeException(m + " failed", cause);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Can not access " + m, e);
        } catch (RuntimeException e) {
            throw new RuntimeException("Can not invoke " + m, e);
        }
    }

    protected RuntimeException mapFailed(Method method, int i, Class<?> type) {
        throw new IllegalArgumentException(
                "Cannot map parameter " + i + " (" + type + ") of" + m);
    }

    public static interface ArgMapFactory<Arg, Hint> {
        
        ArgMap<? super Arg, ? super Hint> create(Class<?> paramType, Annotation[] annotations, Hint hint);
        
    }
    
    public static interface ArgMap<Arg, Hint> {
        
        Object map(int i, Context<?> ctx, Arg arg);
        
        int mapIndex(int i, Annotation[] annotations, Hint hint);
        
    }
    
    /**
     * An argument mapping that takes it's parameter's index,
     * or does not care about the index at all.
     */
    public static abstract class DirectIndexArgMap<Arg> implements ArgMap<Arg, Object> {
        @Override
        public final int mapIndex(int i, Annotation[] annotations, Object hint) {
            return i;
        }
    }
    
    
}
