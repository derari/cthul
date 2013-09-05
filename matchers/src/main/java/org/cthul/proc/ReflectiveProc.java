package org.cthul.proc;

import java.lang.reflect.*;
import java.util.Arrays;
import org.cthul.objects.reflection.Signatures;

/**
 * A {@link PN} that uses reflection to invoke a method or a constructor.
 */
public class ReflectiveProc extends PN {
    
    public static final Class[] ANY_PARAMETERS = null;
    private static final Class[][] PARAM_COUNT = { 
        new Class[0], new Class[1], new Class[2], new Class[3], new Class[4], 
        new Class[5], new Class[6], new Class[7], new Class[8], new Class[9]};
    public static final Class[] NO_PARAMETERS = PARAM_COUNT[0];
    
    public static Class[] anyParameters(int count) {
        if (count < 0) {
            return ANY_PARAMETERS;
        }
        if (count == 0) {
            return NO_PARAMETERS;
        }
        return new Class[count];
    }
    
    static Class[] internAnyParameters(int count) {
        if (count < 0) {
            return ANY_PARAMETERS;
        }
        if (count < PARAM_COUNT.length) {
            return PARAM_COUNT[count];
        }
        return new Class[count];
    }
    
    public static ReflectiveProc newInstance(Class<?> clazz, Class... paramTypes) {
        Constructor<?> c = Signatures.bestConstructor(clazz, paramTypes);
        if (c == null) {
            throw notFound(clazz, null, paramTypes);
        }
        return new ReflectiveProc(c);
    }
    
    public static PN newInstanceWith(Class<?> clazz, Object... args) {
        Constructor<?> c = Signatures.bestConstructor(clazz, args);
        if (c == null) {
            throw notFound(clazz, null, args);
        }
        return new ReflectiveProc(c).call(args);
    }
    
    public static ReflectiveProc invoke(Class clazz, String name, Class... paramTypes) {
        Method m = Signatures.bestMethod(clazz, name, paramTypes);
        if (m == null) {
            throw notFound(clazz, name, paramTypes);
        }
        return new ReflectiveProc(null, m);
    }
    
    public static ReflectiveProc invoke(Object object, String name, Class... paramTypes) {
        Method m = Signatures.bestMethod(object.getClass(), name, paramTypes);
        if (m == null) {
            throw notFound(object, name, paramTypes);
        }
        return new ReflectiveProc(object, m);
    }
    
    public static PN invokeWith(Class clazz, String name, Object... args) {
        Method m = Signatures.bestMethod(clazz, name, args);
        if (m == null) {
            throw notFound(clazz, name, args);
        }
        return new ReflectiveProc(null, m).call(args);
    }
    
    public static PN invokeWith(Object object, String name, Object... args) {
        Method m = Signatures.bestMethod(object.getClass(), name, args);
        if (m == null) {
            throw notFound(object, name, args);
        }return new ReflectiveProc(object, m).call(args);
    }
    
    private static IllegalArgumentException notFound(Object owner, String name, Object[] args) {
        Class[] argTypes;
        if (args instanceof Class[]) {
            argTypes = (Class[]) args;
        } else if (args == null) {
            argTypes = null;
        } else {
            argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                Object a = args[i];
                argTypes[i] = a == null ? null : a.getClass();
            }
        }
        String invokee;
        if (owner instanceof Class) {
            invokee = ((Class) owner).getName();
        } else if (owner == null) {
            invokee = "null";
        } else {
            invokee = owner.getClass().getName();
        }
        String msg = describeMethod(invokee, name, argTypes) + " not found";
        return new IllegalArgumentException(msg);
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
        Class[] params = method.getParameterTypes();
        this.paramCount = params.length;
        if (method.isVarArgs()) {
            varArgType = params[paramCount-1];
        } else {
            varArgType = null;
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
        Class[] params = constructor.getParameterTypes();
        this.paramCount = params.length;
        if (constructor.isVarArgs()) {
            varArgType = params[paramCount-1];
        } else {
            varArgType = null;
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

    private Object[] fixVarArgs(Object[] args) {
        return Signatures.fixVarArgs(paramCount, varArgType, args);
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
                args = fixVarArgs(args);
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
                args = fixVarArgs(args);
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
