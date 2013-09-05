package org.cthul.proc;

/**
 *
 * @author Arian Treffer
 */
public class Procs {
    
    public static Class[] anyParameters() {
        return ReflectiveProc.ANY_PARAMETERS;
    }
    
    public static Class[] noParameters() {
        return ReflectiveProc.NO_PARAMETERS;
    }
    
    public static Class[] anyParameters(int i) {
        return ReflectiveProc.anyParameters(i);
    }
    
    public static Proc invoke(Class clazz, String name, Class... paramTypes) {
        return ReflectiveProc.invoke(clazz, name, paramTypes);
    }
    
    public static Proc invokeWith(Class clazz, String name, Object... args) {
        return ReflectiveProc.invokeWith(clazz, name, args);
    }
    
    public static Proc invoke(Class clazz, String name) {
        return invoke(clazz, name, anyParameters());
    }
    
    public static Proc invoke(Class clazz, String name, int paramCount) {
        return invoke(clazz, name, ReflectiveProc.internAnyParameters(paramCount));
    }
    
    public static Proc invoke(Object object, String name, Class... paramTypes) {
        return ReflectiveProc.invoke(object, name, paramTypes);
    }
    
    public static Proc invoke(Object object, String name, Object... args) {
        return ReflectiveProc.invokeWith(object, name, args);
    }
    
    public static Proc invoke(Object object, String name) {
        return invoke(object, name, anyParameters());
    }
    
    public static Proc invoke(Object object, String name, int paramCount) {
        return invoke(object, name, ReflectiveProc.internAnyParameters(paramCount));
    }
    
    public static Proc invoke(String name) {
        return invoke(detectClass(), name);
    }
    
    public static Proc invoke(String name, Class... paramTypes) {
        return invoke(detectClass(), name, paramTypes);
    }
    
    public static Proc invoke(String name, int paramCount) {
        return invoke(detectClass(), name, ReflectiveProc.internAnyParameters(paramCount));
    }
    
    public static Proc invokeWith(String name, Object... args) {
        return invokeWith(detectClass(), name, args);
    }
    
    public static Proc newInstance(Class<?> clazz, Class... paramTypes) {
        return ReflectiveProc.newInstance(clazz, paramTypes);
    }
    
    public static Proc newInstanceWith(Class<?> clazz, Object... args) {
        return ReflectiveProc.newInstanceWith(clazz, args);
    }
    
    public static Proc newInstance(Class clazz, int paramCount) {
        return ReflectiveProc.newInstance(clazz, ReflectiveProc.internAnyParameters(paramCount));
    }
    
    public static Proc newInstance(Class clazz) {
        return ReflectiveProc.newInstance(clazz, anyParameters());
    }
    
    private static Class detectClass() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // 0: getStackTrace
        // 1: detectClass
        // 2: invoke
        // 3: caller
        StackTraceElement caller = stack[3];
        try {
            return Class.forName(caller.getClassName());
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(
                    "Could not detect class of " + caller, ex);
        }
    }

    protected Procs() {
    }
    
}
