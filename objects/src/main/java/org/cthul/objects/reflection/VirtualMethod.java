package org.cthul.objects.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class VirtualMethod<R> {
    
    public static <R> VirtualMethod<R> instance(Class clazz, String name) {
        return VirtualSwitch.instance(clazz).get(name, 0, true, (Class[]) null);
    }
    
    public static <R> VirtualMethod<R> instance(Class clazz, String name, Class... paramTypes) {
        return VirtualSwitch.instance(clazz).get(name, 0, true, paramTypes);
    }
    
    public static <R> VirtualMethod<R> instance(Class clazz, String name, boolean varArgSwitch, Class... paramTypes) {
        return VirtualSwitch.instance(clazz).get(name, 0, varArgSwitch, paramTypes);
    }
    
    public static <R> VirtualMethod<R> instance(Class clazz, String name, int switchParamCount, Class... moreParams) {
        return VirtualSwitch.instance(clazz).get(name, switchParamCount, true, moreParams);
    }
    
    public static <R> VirtualMethod<R> instance(Class clazz, String name, int switchParamCount, boolean varArgSwitch, Class... moreParams) {
        return VirtualSwitch.instance(clazz).get(name, switchParamCount, varArgSwitch, moreParams);
    }

    private final Method[] methods;
    private final Class[] paramTypes;
    private final boolean varArgsSwitch;
    
    public VirtualMethod(Class clazz, String name) {
        this(clazz, name, 0, true, (Class[]) null);
    }
    
    public VirtualMethod(Class clazz, String name, Class... paramTypes) {
        this(clazz, name, 0, true, paramTypes);
    }
    
    public VirtualMethod(Class clazz, String name, boolean varArgsSwitch, Class... paramTypes) {
        this(clazz, name, 0, varArgsSwitch, paramTypes);
    }
    
    public VirtualMethod(Class clazz, String name, int switchParamCount, Class... moreParams) {
        this(clazz, name, switchParamCount, true, moreParams);
    }
    
    public VirtualMethod(Class clazz, String name, int switchParamCount, boolean varArgsSwitch, Class... moreParams) {
        this.methods = Signatures.collectMethods(clazz, name, Signatures.ANY, Signatures.NONE);
        this.varArgsSwitch = varArgsSwitch;
        if (switchParamCount > 0 && moreParams != null) {
            this.paramTypes = new Class[moreParams.length + switchParamCount];
            System.arraycopy(moreParams, 0, paramTypes, switchParamCount, moreParams.length);
        } else {
            this.paramTypes = moreParams != null ? moreParams.clone() : null;
        }
    }
    
    protected Class[] getParamTypes(Object[] args) {
        if (paramTypes == null) {
            return Signatures.collectArgTypes(args);
        }
        int len = varArgsSwitch ? args.length : paramTypes.length;
        Class[] params = Arrays.copyOf(paramTypes, len);
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                Object a = args[i];
                if (a != null) params[i] = a.getClass();
            }
        }
        return params;
    }
    
    public R _invoke(Object self, Object... args) {
        try {
            return invoke(self, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw asRE(e);
        }
    }
    
    public R invoke(Object self, Object... args) throws IllegalAccessException, InvocationTargetException {
        Class[] params = getParamTypes(args);
        Method m = Signatures.bestMethod(methods, params);
        if (!m.isAccessible()) m.setAccessible(true);
        Object r = Signatures.invoke(self, m, args);
        return cast(r);
    }
    
    public R _invokeStatic(Object... args) {
        try {
            return invokeStatic(args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw asRE(e);
        }
    }
    
    public R invokeStatic(Object... args) throws IllegalAccessException, InvocationTargetException {
        return invoke(null, args);
    }
    
    protected R cast(Object o) {
        return (R) o;
    }
    
    protected static RuntimeException asRE(Throwable e) {
        if (e instanceof Error) {
            throw (Error) e;
        }
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        if (e instanceof InvocationTargetException) {
            return asRE(e.getCause());
        }
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        return new RuntimeException(e);
    }
}
