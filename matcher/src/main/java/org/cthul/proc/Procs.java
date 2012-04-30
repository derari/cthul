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
    
    public static PN invoke(Class clazz, String name, Class... paramTypes) {
        return ReflectiveProc.invoke(clazz, name, paramTypes);
    }
    
    public static PN invokeWith(Class clazz, String name, Object... args) {
        return ReflectiveProc.invokeWith(clazz, name, args);
    }
    
    public static PN invoke(Class clazz, String name) {
        return invoke(clazz, name, anyParameters());
    }
    
    public static PN invoke(Class clazz, String name, int paramCount) {
        return invoke(clazz, name, ReflectiveProc.unsafeAnyParameters(paramCount));
    }
    
    public static PN invoke(Object object, String name, Class... paramTypes) {
        return ReflectiveProc.invoke(object, name, paramTypes);
    }
    
    public static PN invoke(Object object, String name, Object... args) {
        return ReflectiveProc.invokeWith(object, name, args);
    }
    
    public static PN invoke(Object object, String name) {
        return invoke(object, name, anyParameters());
    }
    
    public static PN invoke(Object object, String name, int paramCount) {
        return invoke(object, name, ReflectiveProc.unsafeAnyParameters(paramCount));
    }
    
    public static PN invoke(String name) {
        return invoke(detectClass(), name);
    }
    
    public static PN invoke(String name, Class... paramTypes) {
        return invoke(detectClass(), name, paramTypes);
    }
    
    public static PN invoke(String name, int paramCount) {
        return invoke(detectClass(), name, ReflectiveProc.unsafeAnyParameters(paramCount));
    }
    
    public static PN invokeWith(String name, Object... args) {
        return invokeWith(detectClass(), name, args);
    }
    
    public static PN newInstance(Class<?> clazz, Class... paramTypes) {
        return ReflectiveProc.newInstance(clazz, paramTypes);
    }
    
    public static PN newInstanceWith(Class<?> clazz, Object... args) {
        return ReflectiveProc.newInstanceWith(clazz, args);
    }
    
    public static PN newInstance(Class clazz, int paramCount) {
        return ReflectiveProc.newInstance(clazz, ReflectiveProc.unsafeAnyParameters(paramCount));
    }
    
    public static PN newInstance(Class clazz) {
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
