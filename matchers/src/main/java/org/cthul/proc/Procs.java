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
    
    public static Proc invokeWith(Object object, String name, Object... args) {
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
    
    public static Proc p(final LN lambda) {
        return new PN() { 
            @Override
            protected Object runN(Object[] args) throws Throwable {
                return lambda.run(args);
            }
        };
    };
    
    public static Proc0 p(final L0 lambda) {
        return new P0() { 
            @Override
            protected Object run() throws Throwable {
                return lambda.run();
            }
        };
    };
    
    public static <A> Proc1<A> p(final L1<? super A> lambda) {
        return new P1<A>() { 
            @Override
            protected Object run(A a) throws Throwable {
                return lambda.run(a);
            }
        };
    };
    
    public static <A, B> Proc2<A, B> p(final L2<? super A, ? super B> lambda) {
        return new P2<A, B>() { 
            @Override
            protected Object run(A a, B b) throws Throwable {
                return lambda.run(a, b);
            }
        };
    };
    
    public static <A, B, C> Proc3<A, B, C> p(final L3<? super A, ? super B, ? super C> lambda) {
        return new P3<A, B, C>() { 
            @Override
            protected Object run(A a, B b, C c) throws Throwable {
                return lambda.run(a, b, c);
            }
        };
    };
    
    public static <A, B, C, D> Proc4<A, B, C, D> p(final L4<? super A, ? super B, ? super C, ? super D> lambda) {
        return new P4<A, B, C, D>() { 
            @Override
            protected Object run(A a, B b, C c, D d) throws Throwable {
                return lambda.run(a, b, c, d);
            }
        };
    };
    
    public static Proc pn(LN lambda) {
        return p(lambda);
    }
    
    public static Proc0 p0(L0 lambda) {
        return p(lambda);
    }
    
    public static <A> Proc1<A> p1(L1<? super A> lambda) {
        return p(lambda);
    }
    
    public static <A, B> Proc2<A, B> p2(L2<? super A, ? super B> lambda) {
        return p(lambda);
    }
    
    public static <A, B, C> Proc3<A, B, C> p3(L3<? super A, ? super B, ? super C> lambda) {
        return p(lambda);
    }
    
    public static <A, B, C, D> Proc4<A, B, C, D> p4(L4<? super A, ? super B, ? super C, ? super D> lambda) {
        return p(lambda);
    }
    
    private static Class detectClass() {
        return detectClass(1);
    }
    
    private static Class detectClass(int i) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // 0: getStackTrace
        // 1: detectClass
        // 2: invoke
        // 3: caller
        StackTraceElement caller = stack[i+3];
        try {
            return Class.forName(caller.getClassName());
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(
                    "Could not detect class of " + caller, ex);
        }
    }

    protected Procs() {
    }
 
    public static interface LN {
        Object run(Object... args) throws Throwable;
    }
 
    public static interface L0 {
        Object run() throws Throwable;
    }
 
    public static interface L1<A> {
        Object run(A a) throws Throwable;
    }
 
    public static interface L2<A, B> {
        Object run(A a, B b) throws Throwable;
    }
 
    public static interface L3<A, B, C> {
        Object run(A a, B b, C c) throws Throwable;
    }
 
    public static interface L4<A, B, C, D> {
        Object run(A a, B b, C c, D d) throws Throwable;
    }
}
