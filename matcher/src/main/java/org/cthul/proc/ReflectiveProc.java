package org.cthul.proc;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 * A {@link PN} that uses reflection to invoke a method or a constructor.
 * @author Arian Treffer
 */
public class ReflectiveProc extends PN {
    
    public static final Class[] ANY_PARAMETERS = null;
    private static final Class[][] PARAM_COUNT = { 
        new Class[0], new Class[1], new Class[2], new Class[3], new Class[4], 
        new Class[5], new Class[6], new Class[7], new Class[8], new Class[9]};
    public static final Class[] NO_PARAMETERS = PARAM_COUNT[0];
    
    public static Class[] anyParameters(int c) {
        if (c < 0) {
            return ANY_PARAMETERS;
        }
        if (c == 0) {
            return NO_PARAMETERS;
        }
        return new Class[c];
    }
    
    static Class[] unsafeAnyParameters(int c) {
        if (c < 0) {
            return ANY_PARAMETERS;
        }
        if (c < PARAM_COUNT.length) {
            return PARAM_COUNT[c];
        }
        return new Class[c];
    }
    
    public static ReflectiveProc newInstance(Class<?> clazz, Class... paramTypes) {
        Constructor<?> c = selectConstructor(clazz, paramTypes);
        return new ReflectiveProc(c);
    }
    
    public static PN newInstanceWith(Class<?> clazz, Object... args) {
        Class[] paramTypes = argsToTypes(args);
        return newInstance(clazz, paramTypes).call(args);
    }
    
    public static ReflectiveProc invoke(Class clazz, String name, Class... paramTypes) {
        Method m = selectMethod(clazz, name, paramTypes);
        return new ReflectiveProc(null, m);
    }
    
    public static ReflectiveProc invoke(Object object, String name, Class... paramTypes) {
        Method m = selectMethod(object.getClass(), name, paramTypes);
        return new ReflectiveProc(object, m);
    }
    
    public static PN invokeWith(Class clazz, String name, Object... args) {
        Class[] paramTypes = argsToTypes(args);
        return invoke(clazz, name, paramTypes).call(args);
    }
    
    public static PN invokeWith(Object object, String name, Object... args) {
        Class[] paramTypes = argsToTypes(args);
        return invoke(object, name, paramTypes).call(args);
    }
    
    private static Class[] argsToTypes(final Object[] args) {
        if (args == null) return null;
        final Class[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] == null ? null : args[i].getClass();
        }
        return types;
    }

    private static Method selectMethod(Class clazz, String name, Class[] paramTypes) {
        final Method m = findMethod(clazz, name, paramTypes);
        if (m == null) {
            throw notFound(clazz, name, paramTypes);
        }
        return m;
    }
    
    private static Constructor<?> selectConstructor(Class<?> clazz, Class[] paramTypes) {
        final Constructor<?> m = findConstructor(clazz, paramTypes);
        if (m == null) {
            throw notFound(clazz, null, paramTypes);
        }
        return m;
    }
    
    private static IllegalArgumentException notFound(Class clazz, String name, Class[] paramTypes) {
        return new IllegalArgumentException(
                "No match found for " +
                describeMethod(clazz.getName(), name, paramTypes));
    }
    
    /**
     * Tries to find a method that matches the parameter types.
     * The first exact match is returned.
     * If there is no exact match, a method is returned where all parameter
     * types are assignable.
     * If there is more than one assignable match, an exception is raised,
     * warning about ambiguity.
     * 
     * @param clazz
     * @param name
     * @param paramTypes
     * @return matching method or null
     */
    private static Method findMethod(Class clazz, String name, Class[] paramTypes) {
        return findMethod(clazz, name, paramTypes, null);
    }
    
    private static Method findMethod(Class clazz, String name, Class[] paramTypes, Method assignableResult) {
        if (clazz == null) {
            return assignableResult;
        }
        Method exactResult = null;
        Method assignableAmbigous = null;
        for (Method c: clazz.getDeclaredMethods()) {
            if (c.getName().equals(name)) {
                switch (paramsMatch(c, paramTypes)) {
                    case EXACT:
                        if (exactResult == null) {
                            exactResult = c;
                        } else if (paramTypes == null) {
                            throw ambigous(clazz, "new", exactResult, c, paramTypes);
                        }
                        break;
                    case ASSIGNABLE:
                        if (assignableResult == null) {
                            assignableResult = c;
                        } else if (assignableAmbigous == null) {
                            assignableAmbigous = c;
                        }
                        break;
                    case NONE:
                        break; // ignore mismatch
                    default:
                        throw new AssertionError(paramsMatch(c, paramTypes));
                }
            }
        }
        if (exactResult != null) {
            return exactResult;
        }
        if (assignableAmbigous != null) {
            throw ambigous(clazz, name, assignableResult, assignableAmbigous, paramTypes);
        }
        // look in superclass for exact match
        return findMethod(clazz.getSuperclass(), name, paramTypes, assignableResult);
    }
    
    private static Constructor<?> findConstructor(Class<?> clazz, Class<?>[] paramTypes) {
        if (clazz == null) {
            return null;
        }
        Constructor<?> exactResult = null;
        Constructor<?> assignableResult = null;
        Constructor<?> assignableAmbigous = null;
        for (Constructor<?> c: clazz.getConstructors()) {
            switch (paramsMatch(c, paramTypes)) {
                case EXACT:
                    if (exactResult == null) {
                        exactResult = c;
                    } else if (paramTypes == null) {
                        throw ambigous(clazz, null, exactResult, c, paramTypes);
                    }
                    break;
                case ASSIGNABLE:
                    if (assignableResult == null) {
                        assignableResult = c;
                    } else if (assignableAmbigous == null) {
                        assignableAmbigous = c;
                    }
                    break;
                case NONE:
                    break; // ignore mismatch
                default:
                    throw new AssertionError(paramsMatch(c, paramTypes));
            }
        }
        if (exactResult != null) {
            return exactResult;
        }
        if (assignableAmbigous != null) {
            throw ambigous(clazz, "new", assignableResult, assignableAmbigous, paramTypes);
        }
        return assignableResult;
    }
    
    private static enum Match {
        NONE, ASSIGNABLE, EXACT;
    }
    
    private static Match paramsMatch(final Method method, 
                                       final Class[] givenParamTypes) {
        if (givenParamTypes == null) {
            return Match.ASSIGNABLE;
        }
        return paramsMatch(method.getParameterTypes(), givenParamTypes);
    }
    
    private static <T> Match paramsMatch(final Constructor<T> constructor, 
                                           final Class[] givenParamTypes) {
        if (givenParamTypes == null) {
            return Match.ASSIGNABLE;
        }
        return paramsMatch(constructor.getParameterTypes(), givenParamTypes);
    }
    
    private static Match paramsMatch(final Class<?>[] methodParamTypes, 
                                     final Class<?>[] givenParamTypes) {
        if (methodParamTypes.length != givenParamTypes.length) {
            return Match.NONE;
        }
        boolean exactMatch = true;
        for (int i = 0; i < methodParamTypes.length; i++) {
            Class given = givenParamTypes[i];
            if (given == null) {
                exactMatch = false;
            } else {
                Class expected = methodParamTypes[i];
                if (given.equals(expected)) {
                } else if (isAssignable(expected, given)) {
                    exactMatch = false;
                } else {
                    return Match.NONE;
                }
            }
        }
        return exactMatch ? Match.EXACT : Match.ASSIGNABLE;
    }
    
    private static boolean isAssignable(Class expected, Class given) {
        if (expected.isAssignableFrom(given)) return true;
        if (!expected.isPrimitive()) return false;
        if (expected == byte.class) return given == Byte.class;
        if (expected == char.class) return given == Character.class;
        if (expected == short.class) return given == Short.class;
        if (expected == int.class) return given == Integer.class;
        if (expected == long.class) return given == Long.class;
        if (expected == float.class) return given == Float.class;
        if (expected == double.class) return given == Double.class;
        return false;
    }
    
    private static IllegalArgumentException ambigous(Class clazz, String name, Object m1, Object m2, Class[] paramTypes) {
        String method = describeMethod(clazz.getName(), name, paramTypes);
        
        return new IllegalArgumentException(String.format(
                    "Ambigous results for %s, found %s and %s.", 
                    method, m1, m2));
    }
    
    private static String describeMethod(String invokee, String method, Class[] paramTypes) {
        final String paramString;
        if (paramTypes == null) {
            paramString = "<any args>";
        } else if (paramTypes.length == 0) {
            paramString = "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (Class c: paramTypes) {
                sb.append(',');
                sb.append(c == null ? "<any>" : c.getName());
            }
            paramString = sb.substring(1);
        }
        String mName = String.format(
                method == null ? "new %s" : "%s#%s",
                invokee, method);
        return mName + "(" + paramString + ")";
    }
    
    private final Object instance;
    private final Method method;
    private final Constructor constructor;
    private final int paramCount;
    private final Class varArgType;

    public ReflectiveProc(Object instance, Method method) {
        super(expectedArgCount(method, instance));
        name(describeMethod(
                instance == null ? method.getDeclaringClass().getName() : instance.toString(),
                method.getName(),
                method.getParameterTypes()));
        if (instance == null && isStatic(method)) {
            // static method, set dummy object to avoid that instance
            // is selected from args later on
            instance = method;
        }
        this.instance = instance;
        this.method = method;
        this.paramCount = method.getParameterTypes().length;
        if (!method.isVarArgs()) {
            varArgType = null;
        } else {
            varArgType = method.getParameterTypes()[paramCount-1];
        }
        this.constructor = null;
    }

    private static int expectedArgCount(Method method, Object instance) {
        if (method.isVarArgs()) return -1;
        return method.getParameterTypes().length +
                 ((instance != null || isStatic(method)) ? 0 : 1);
    }

    private static boolean isStatic(Method method) {
        return (method.getModifiers() & Modifier.STATIC) != 0;
    }
    
    public ReflectiveProc(Constructor constructor) {
        super(expectedArgCount(constructor));
        name(describeMethod( 
                constructor.getDeclaringClass().getName(), null,
                constructor.getParameterTypes()));
        this.instance = null;
        this.method = null;
        this.constructor = constructor;
        this.paramCount = constructor.getParameterTypes().length;
        if (!constructor.isVarArgs()) {
            varArgType = null;
        } else {
            varArgType = constructor.getParameterTypes()[paramCount-1];
        }
    }

    private static int expectedArgCount(Constructor constructor) {
        if (constructor.isVarArgs()) return -1;
        return constructor.getParameterTypes().length;
    }

    @Override
    protected Object runN(Object[] args) throws Throwable {
        if (constructor == null) {
            return invoke(args);
        } else {
            return newInstance(args);
        }
    }

    private Object[] detectVarArgs(Object[] args) {
        int len = args.length;
        if (len == paramCount - 1) {
            args = Arrays.copyOf(args, paramCount);
            args[paramCount - 1] = Array.newInstance(varArgType.getComponentType(), 0);
        } else if (len > paramCount ||
                (len == paramCount && 
                 !varArgType.isInstance(args[paramCount-1]))) {
            Object varArgs = copyVarArgs(args, len);
            args = Arrays.copyOf(args, paramCount);
            args[paramCount - 1] = varArgs;
        }
        return args;
    }
    
    private Object copyVarArgs(Object[] args, int len) {
        Class c = varArgType.getComponentType();
        if (c.isPrimitive()) {
            int vargC = args.length - paramCount + 1;
            if (c == byte.class) {
                byte[] vargs = new byte[vargC];
                for (int i = 0; i < vargC; i++) {
                    vargs[i] = (Byte) args[paramCount + i - 1];
                }
                return vargs;
            } else if (c == char.class) {
                char[] vargs = new char[vargC];
                for (int i = 0; i < vargC; i++) {
                    vargs[i] = (Character) args[paramCount + i - 1];
                }
                return vargs;
            } else if (c == short.class) {
                short[] vargs = new short[vargC];
                for (int i = 0; i < vargC; i++) {
                    vargs[i] = (Short) args[paramCount + i - 1];
                }
                return vargs;
            } else if (c == int.class) {
                int[] vargs = new int[vargC];
                for (int i = 0; i < vargC; i++) {
                    vargs[i] = (Integer) args[paramCount + i - 1];
                }
                return vargs;
            } else if (c == long.class) {
                long[] vargs = new long[vargC];
                for (int i = 0; i < vargC; i++) {
                    vargs[i] = (Long) args[paramCount + i - 1];
                }
                return vargs;
            } else if (c == float.class) {
                float[] vargs = new float[vargC];
                for (int i = 0; i < vargC; i++) {
                    vargs[i] = (Float) args[paramCount + i - 1];
                }
                return vargs;
            } else if (c == double.class) {
                double[] vargs = new double[vargC];
                for (int i = 0; i < vargC; i++) {
                    vargs[i] = (Double) args[paramCount + i - 1];
                }
                return vargs;
            }
            throw new AssertionError(c);
        }
        return Arrays.copyOfRange(args, paramCount-1, len, varArgType);
    }

    private Object invoke(Object[] args) throws Throwable {
        final boolean wasAccessible = method.isAccessible();
        final Object result;
        if (!wasAccessible) {
            method.setAccessible(true);
        }
        try {
            Object obj;
            if (instance != null) {
                obj = instance;
            } else {
                obj = args[0];
                args = Arrays.copyOfRange(args, 1, args.length);
            }
            if (method.isVarArgs()) {
                args = detectVarArgs(args);
            }
            result = method.invoke(obj, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } finally {
            if (!wasAccessible) {
                method.setAccessible(false);
            }
        }
        return result;
    }

    private Object newInstance(Object[] args) throws Throwable {
        final boolean wasAccessible = constructor.isAccessible();
        final Object result;
        if (!wasAccessible) {
            constructor.setAccessible(true);
        }
        try {
            if (constructor.isVarArgs()) {
                args = detectVarArgs(args);
            }
            result = constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } finally {
            if (!wasAccessible) {
                constructor.setAccessible(false);
            }
        }
        return result;
    }
    
}
