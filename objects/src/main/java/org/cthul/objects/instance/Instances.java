package org.cthul.objects.instance;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import org.cthul.objects.Boxing;
import org.cthul.objects.reflection.Signatures;

/**
 *
 */
public class Instances {
    
    public static Object newInstance(Instance inst) {
        return getInstance(null, inst);
    }
    
    public static Object getInstance(InstanceMap map, Instance inst) {
        return getInstance(map, inst.key(), inst.impl(), inst.factory(), null);
    }
    
    public static Object[] getInstances(InstanceMap map, Instance[] inst) {
        Object[] result = new Object[inst.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getInstance(map, inst[i]);
        }
        return result;
    }
    
    public static Object newInstance(Class<?> impl, String factory, Arg[] args) {
        return getInstance(null, null, impl, factory, args);
    }
    
    public static Object newInstance(Class<?> impl, String factory, Arg[] args, Object[] moreValues) {
        return getInstance(null, null, impl, factory, args, moreValues);
    }
    
    public static Object getInstance(InstanceMap map, String key, Class<?> impl, String factory, Arg[] args) {
        return getInstance(map, key, impl, factory, args, null);
    }
    
    public static Object getInstance(InstanceMap map, String key, Class<?> impl, String factory, Arg[] args, Object[] moreValues) {
        if (map != null) {
            if (!isDefault(key, "")) {
                Object o = map.get(key);
                if (o != null) return o;
            } else if (isDefault(factory, "") && isEmpty(args)) {
                Object o = map.get(impl);
                if (o != null) return o;
            }
        }
        if (args == null) {
            return newInstance(map, impl, factory, null, moreValues);
        }
        Object[] argValues = new Object[lengthOf(args)];
        Class[] argTypes = new Class[argValues.length];
        fillArgs(map, args, argValues, argTypes, moreValues);
        return newInstance(map, impl, factory, argTypes, argValues);
    }
    
    public static void fillArgs(InstanceMap map, Arg[] args, Object[] values, Class[] types, Object[] moreValues) {
        if (isEmpty(args)) return;
        for (int i = 0; i < args.length; i++) {
            fillArg(map, args, i, values, types, moreValues);
        }
    }
    
    private static void fillArg(InstanceMap map, Arg[] args, int i, Object[] values, Class[] types, Object[] moreValues) {
        Arg a = args[i];
        Object value = null;
        Class<?> type = null;
        boolean storeValue = false;
        if (!isDefault(a.key(), "")) {
            if (map != null) {
                value = map.get(a.key());
                if (value != null) {
                    type = value.getClass();
                    storeValue = true;
                }
            } else if (moreValues != null) {
                String[] keys = a.key().split(",");
                Object[] v = new Object[keys.length];
                try {
                    for (int j = 0; j < keys.length; j++) {
                        int index = Integer.parseInt(keys[j]);
                        v[j] = moreValues[index];
                    }
                    value = v;
                    type = Object[].class;
                } catch (NumberFormatException e) {
                }
            }
        }
        Object[] prims = {a.x(), a.b(), a.c(), a.d(), a.f(), a.i(), a.l(), a.s(), a.str(), a.o()};
        for (Object p: prims) {
            if (!isEmpty(p)) {
                if (type != null) {
                    throw new IllegalArgumentException(
                            "Ambiguous @Arg, got " 
                            + type.getSimpleName() 
                            + " and " + p.getClass().getSimpleName());
                }
                if (p instanceof Instance[]) {
                    p = getInstances(map, (Instance[]) p);
                }
                value = p;
                type = value.getClass();
            }
        }
        Class<?> compType = a.arrayOf();
        if (compType == void.class && value != null) {
            int l = lengthOf(value);
            if (l == 1) {
                value = Array.get(value, 0);
                type = value != null ? value.getClass() : null;
            }
        } else if (value == null) {
            value = Array.newInstance(compType, 0);
            type = value.getClass();
        } else if (Boxing.boxingType(compType) != null) {
            type = Array.newInstance(compType, 0).getClass();
            value = Boxing.as(value, type);
        } else {
            Class<Object[]> t = (Class) Array.newInstance(compType, 0).getClass();
            Object[] src = (Object[]) value;
            type = t;
            value = Arrays.copyOf(src, src.length, t);
        }
        Class<?> argType = a.type();
        if (argType != void.class) {
            type = argType;
        }
        values[i] = value;
        types[i] = type;
        if (map != null && storeValue) {
            map.put(a.key(), value);
        }
    }
    
    public static Class[] getTypes(InstanceMap map, Arg[] args, Class[] inputTypes) {
        Class[] result = new Class[args.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getType(map, args[i], inputTypes);
        }
        return result;
    }
    
    private static Class getType(InstanceMap map, Arg a, Class[] inputTypes) {
        if (!isDefault(a.key(), "")) {
            if (map != null) {
                return map.get(a.key()).getClass();
            } else if (inputTypes != null) {
                String[] keys = a.key().split(",");
                if (keys.length > 1) {
                    return Object[].class;
                }
                try {
                    int i = Integer.parseInt(keys[0]);
                    return inputTypes[i];
                } catch (NumberFormatException e) {
                    // continue
                }
            }
        }
        Class<?> compType = a.arrayOf();
        if (compType != void.class && compType != null) {
            return Array.newInstance(compType, 0).getClass();
        }
        Object[] prims = {a.x(), a.b(), a.c(), a.d(), a.f(), a.i(), a.l(), a.s(), a.str(), a.o()};
        for (Object p: prims) {
            if (!isEmpty(p)) {
                if (p instanceof Instance[]) {
                    p = getInstances(map, (Instance[]) p);
                }
                if (Array.getLength(p) == 1) {
                    return p.getClass().getComponentType();
                } else {
                    return p.getClass();
                }
            }
        }
        return null;
    }
    
    public static Object[] getArgs(InstanceMap map, Arg[] args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private static int lengthOf(Object array) {
        return array == null ? 0 : Array.getLength(array);
    }
    
    private static boolean isEmpty(Object array) {
        return array == null || Array.getLength(array) == 0;
    }
    
    private static boolean isDefault(Object value, Object def) {
        return value == null || value.equals(def);
    }
    
    private static boolean isStatic(Method m) {
        return (m.getModifiers() & Modifier.STATIC) != 0;
    }
    
    private static boolean isStatic(Field f) {
        return (f.getModifiers() & Modifier.STATIC) != 0;
    }

    public static Object newInstance(Class<?> impl, String factory, Class[] argTypes, Object[] argValues) {
        return newInstance(null, impl, factory, argTypes, argValues);
    }
    
    private static Object newInstance(InstanceMap map, Class<?> impl, String factory, Class[] argTypes, Object[] argValues) {
        final boolean useConstructor, useMethod;
        if (isDefault(factory, "")) {
            useConstructor = useMethod = true;
        } else if (factory.equals("new")) {
            useConstructor = true;
            useMethod = false;
        } else {
            useConstructor = false;
            useMethod = true;
        }
        if (useConstructor) {
            Constructor<?> c;
            if (argTypes != null) {
                c = Signatures.bestConstructor(impl, argTypes);
            } else {
                c = Signatures.bestConstructor(impl, argValues);
            }
            if (c == null && !useMethod) {
                throw new IllegalArgumentException(
                        "No suitable constructor in " + impl);
            }
            if (c != null) {
                try {
                    c.setAccessible(true);
                    return c.newInstance(argValues);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Method m;
        if (isDefault(factory, "")) {
            m = null;
            String[] methods = {"instance", "newInstance", "getInstance"};
            for (String name: methods) {
                if (argTypes != null) {
                    m = Signatures.bestMethod(impl, name, argTypes);
                } else {
                    m = Signatures.bestMethod(impl, name, argValues);
                }
                if (m != null) {
                    break;
                }
            }
        } else {
            if (argTypes != null) {
                m = Signatures.bestMethod(impl, factory, argTypes);
            } else {
                m = Signatures.bestMethod(impl, factory, argValues);
            }
        }
        if (m != null) {
            Object factoryInstance = null;
            if (!isStatic(m)) {
                if (map == null) {
                    throw new IllegalArgumentException(
                            "InstanceMap required to get factory instance: " + impl);
                }
                factoryInstance = map.getOrCreate(impl);
            }
            try {
                m.setAccessible(true);
                return m.invoke(factoryInstance, argValues);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        Field f = null;
        if (isDefault(factory, "")) {
            for (Field f2: impl.getDeclaredFields()) {
                if (f2.getName().equalsIgnoreCase("instance")) {
                    f = f2;
                    break;
                }
            }
        } else {
            for (Field f2: impl.getDeclaredFields()) {
                if (f2.getName().equals(factory)) {
                    f = f2;
                    break;
                }
            }
        }
        if (f != null) {
            Object factoryInstance = null;
            if (!isStatic(f)) {
                if (map == null) {
                    throw new IllegalArgumentException(
                            "InstanceMap required to get factory instance: " + impl);
                }
                factoryInstance = map.getOrCreate(impl);
            }
            if (!isEmpty(argValues)) {
                throw new IllegalArgumentException(
                        "No args allowed for field " + f);
            }
            try {
                f.setAccessible(true);
                return f.get(factoryInstance);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        if (useConstructor) {
            throw new IllegalArgumentException(
                    "No suitable constructor or factory in " + impl);
        } else {
            throw new IllegalArgumentException(
                    "No suitable factory '" + factory + "' in " + impl);
        }
    }
}
