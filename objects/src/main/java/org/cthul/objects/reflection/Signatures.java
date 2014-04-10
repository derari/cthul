package org.cthul.objects.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.cthul.objects.Boxing;

/**
 *
 */
public class Signatures {
    
    /**
     * Finds the best constructor for the given arguments.
     * @param <T>
     * @param clazz
     * @param args
     * @return constructor, or {@code null}
     * @throws AmbiguousSignatureMatchException if multiple constructors match equally
     */
    public static <T> Constructor<T> bestConstructor(Class<T> clazz, Object[] args) throws AmbiguousConstructorMatchException {
        return bestConstructor(collectConstructors(clazz), args);
    }
    
    /**
     * Finds the best constructor for the given arguments types.
     * @param <T>
     * @param clazz
     * @param argTypes
     * @return constructor, or {@code null}
     * @throws AmbiguousSignatureMatchException if multiple constructors match equally
     */
    public static <T> Constructor<T> bestConstructor(Class<T> clazz, Class<?>[] argTypes) throws AmbiguousConstructorMatchException {
        return bestConstructor(collectConstructors(clazz), argTypes);
    }
    
    /**
     * Selects the best constructor for the given arguments.
     * @param <T>
     * @param constructors
     * @param args
     * @return constructor, or {@code null}
     * @throws AmbiguousSignatureMatchException if multiple constructors match equally
     */
    public static <T> Constructor<T> bestConstructor(Constructor<T>[] constructors, Object[] args) throws AmbiguousConstructorMatchException {
        return bestConstructor(constructors, collectArgTypes(args));
    }
    
    /**
     * Selects the best constructor for the given argument types.
     * @param <T>
     * @param constructors
     * @param argTypes
     * @return constructor, or {@code null}
     * @throws AmbiguousSignatureMatchException if multiple constructors match equally
     */
    public static <T> Constructor<T> bestConstructor(Constructor<T>[] constructors, Class<?>[] argTypes) throws AmbiguousConstructorMatchException {
        try {
            return best(constructors, collectSignatures(constructors), collectVarArgs(constructors), argTypes);
        } catch (AmbiguousSignatureMatchException e) {
            throw new AmbiguousConstructorMatchException(e, constructors);
        }
    }
    
    /**
     * Finds the best equally-matching constructors for the given arguments.
     * @param <T>
     * @param clazz
     * @param args
     * @return constructors
     */
    public static <T> Constructor<T>[] candidateConstructors(Class<T> clazz, Object[] args) {
        return candidateConstructors(collectConstructors(clazz), args);
    }
    
    /**
     * Finds the best equally-matching constructors for the given argument types.
     * @param <T>
     * @param clazz
     * @param argTypes
     * @return constructors
     */
    public static <T> Constructor<T>[] candidateConstructors(Class<T> clazz, Class<?>[] argTypes) {
        return candidateConstructors(collectConstructors(clazz), argTypes);
    }
    
    /**
     * Selects the best equally-matching constructors for the given arguments.
     * @param <T>
     * @param constructors
     * @param args
     * @return constructors
     */
    public static <T> Constructor<T>[] candidateConstructors(Constructor<T>[] constructors, Object[] args) {
        return candidateConstructors(constructors, collectArgTypes(args));
    }
    
    /**
     * Selects the best equally-matching constructors for the given argument types.
     * @param <T>
     * @param constructors
     * @param argTypes
     * @return constructors
     */
    public static <T> Constructor<T>[] candidateConstructors(Constructor<T>[] constructors, Class<?>[] argTypes) {
        return candidates(constructors, collectSignatures(constructors), collectVarArgs(constructors), argTypes);
    }
    
    /**
     * Finds the best method for the given arguments.
     * @param clazz
     * @param name
     * @param args
     * @return method
     * @throws AmbiguousSignatureMatchException if multiple methods match equally
     */
    public static Method bestMethod(Class<?> clazz, String name, Object[] args) throws AmbiguousMethodMatchException {
        return bestMethod(collectMethods(clazz, name), args);
    }
    
    /**
     * Finds the best method for the given argument types.
     * @param clazz
     * @param name
     * @param argTypes
     * @return method
     * @throws AmbiguousSignatureMatchException if multiple methods match equally
     */
    public static Method bestMethod(Class<?> clazz, String name, Class<?>[] argTypes) throws AmbiguousMethodMatchException {
        return bestMethod(collectMethods(clazz, name), argTypes);
    }
    
    /**
     * Selects the best method for the given arguments.
     * @param methods
     * @param args
     * @return method
     * @throws AmbiguousSignatureMatchException if multiple methods match equally
     */
    public static Method bestMethod(Method[] methods, Object[] args) throws AmbiguousMethodMatchException {
        return bestMethod(methods, collectArgTypes(args));
    }
    
    /**
     * Selects the best method for the given argument types.
     * @param methods
     * @param argTypes
     * @return method
     * @throws AmbiguousSignatureMatchException if multiple methods match equally
     */
    public static Method bestMethod(Method[] methods, Class<?>[] argTypes) throws AmbiguousMethodMatchException {
        try {
            return best(methods, collectSignatures(methods), collectVarArgs(methods), argTypes);
        } catch (AmbiguousSignatureMatchException e) {
            throw new AmbiguousMethodMatchException(e, methods);
        }
    }
    
    /**
     * Finds the best equally-matching methods for the given arguments.
     * @param clazz
     * @param name
     * @param args
     * @return methods
     */
    public static Method[] candidateMethods(Class<?> clazz, String name, Object[] args) {
        return candidateMethods(collectMethods(clazz, name), args);
    }
    
    /**
     * Finds the best equally-matching methods for the given argument types.
     * @param clazz
     * @param name
     * @param argTypes
     * @return methods
     */
    public static Method[] candidateMethods(Class<?> clazz, String name, Class<?>[] argTypes) {
        return candidateMethods(collectMethods(clazz, name), argTypes);
    }
    
    /**
     * Selects the best equally-matching methods for the given arguments.
     * @param methods
     * @param args
     * @return methods
     */
    public static Method[] candidateMethods(Method[] methods, Object[] args) {
        return candidateMethods(methods, collectArgTypes(args));
    }
    
    /**
     * Selects the best equally-matching methods for the given argument types.
     * @param methods
     * @param argTypes
     * @return methods
     */
    public static Method[] candidateMethods(Method[] methods, Class<?>[] argTypes) {
        return candidates(methods, collectSignatures(methods), collectVarArgs(methods), argTypes);
    }
    
    /**
     * Selects the best item for the given argument types.
     * @param <T>
     * @param items
     * @param signatures
     * @param varArgs
     * @param argTypes
     * @return item
     * @throws AmbiguousSignatureMatchException if multiple methods match equally
     */
    public static <T> T best(T[] items, Class<?>[][] signatures, boolean[] varArgs, Class<?>[] argTypes) throws AmbiguousSignatureMatchException {
        int i = bestMatch(signatures, varArgs, argTypes);
        if (i < 0) return null;
        return items[i];
    }
    
    /**
     * Selects the best equally-matching item for the given argument types.
     * @param <T>
     * @param items
     * @param signatures
     * @param varArgs
     * @param argTypes
     * @return item
     */
    public static <T> T[] candidates(T[] items, Class<?>[][] signatures, boolean[] varArgs, Class<?>[] argTypes) {
        final int[] indices = candidateMatches(signatures, varArgs, argTypes);
        T[] result = newArray(items.getClass().getComponentType(), indices.length);
        for (int i = 0; i < indices.length; i++) {
            result[i] = items[indices[i]];
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T[] newArray(Class<?> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
    }
    
    public static Class<?>[] collectArgTypes(Collection<?> args) {
        final Class<?>[] result = new Class<?>[args.size()];
        int i = 0;
        for (Object a: args) {
            result[i++] = argType(a);
        }
        return result;
    }
    
    /**
     * Returnes the types for all values. 
     * For {@code null}-values, the type is {@code null}.
     * @param args
     * @return types
     */
    public static Class<?>[] collectArgTypes(final Object[] args) {
        final Class<?>[] result = new Class<?>[args.length];
        collectArgTypes(args, result, 0, 0, args.length);
        return result;
    }
    
    public static void collectArgTypes(final Object[] args, final Class<?>[] types, int argIndex, int typeIndex, int length) {
        for (int i = 0; i < length; i++) {
            Object a = args[i + argIndex];
            types[i + typeIndex] = argType(a);
        }
    }
    
    private static Class<?> argType(Object a) {
        return a != null ? a.getClass() : null;
    }
    
    /**
     * Collects all public methods of {@code clazz} with the given {@code name}.
     * @param clazz
     * @param name
     * @return methods
     */
    public static Method[] collectMethods(Class<?> clazz, String name) {
        final List<Method> result = new ArrayList<>();
        for (Method m: clazz.getMethods()) {
            if (m.getName().equals(name)) result.add(m);
        }
        return result.toArray(new Method[result.size()]);
    }
    
    /**
     * Collects methods of {@code clazz} with the given {@code name}.
     * Methods are included if their modifier bits match each bit of {@code include}
     * and no bit of {@code exclude}.
     * @param clazz
     * @param name
     * @param include 
     * @param exclude 
     * @return methods
     */
    public static Method[] collectMethods(Class<?> clazz, String name, int include, int exclude) {
        final List<Method> result = new ArrayList<>();
        collectMethods(result, new ArrayList<Class<?>[]>(), new HashSet<Class<?>>(), clazz, name, include, exclude);
        return result.toArray(new Method[result.size()]);
    }
    
    private static void collectMethods(List<Method> methods, List<Class<?>[]> signatures, Set<Class<?>> visited, Class<?> clazz, String name, int include, int exclude) {
        if (clazz == null || !visited.add(clazz)) return;
        for (Method m: clazz.getDeclaredMethods()) {
            String n = m.getName();
            int mod = m.getModifiers();
            if (name.equals(n) && include(mod, include, exclude)) {
                Class<?>[] sig = m.getParameterTypes();
                int len = methods.size();
                boolean isNew = true;
                for (int i = 0; i < len; i++) {
                    if (n.equals(methods.get(i).getName()) &&
                            Arrays.equals(sig, signatures.get(i))) {
                        isNew = false;
                        break;
                    }
                }
                if (isNew) {
                    methods.add(m);
                    signatures.add(sig);
                }
            }
        }
        collectMethods(methods, signatures, visited, clazz.getSuperclass(), name, include, exclude);
        for (Class<?> i: clazz.getInterfaces()) {
            collectMethods(methods, signatures, visited, i, name, include, exclude);
        }
    }
    
    /**
     * Collects all public constructors of {@code clazz}.
     * @param <T>
     * @param clazz
     * @return constructors
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T>[] collectConstructors(Class<T> clazz) {
        return (Constructor<T>[]) clazz.getConstructors();
    }
    
    /**
     * Collects constructors of {@code clazz}.
     * Constructors are included if their modifier bits match each bit of {@code include}
     * and no bit of {@code exclude}.
     * @param <T>
     * @param clazz
     * @param include
     * @param exclude
     * @return constructors
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Constructor<T>[] collectConstructors(Class<T> clazz, int include, int exclude) {
        final List<Constructor<?>> result = new ArrayList<>();
        for (Constructor<?> c: clazz.getDeclaredConstructors()) {
            int mod = c.getModifiers();
            if (include(mod, include, exclude)) {
                result.add(c);
            }
        }
        return (Constructor<T>[]) result.toArray(new Constructor<?>[result.size()]);
    }
    
    private static boolean include(int mod, int include, int exclude) {
        if (include == ANY) {
            return (mod & exclude) == 0;
        } else {
            return (mod & include) == include && (mod & exclude) == 0;
        }
    }
    
    public static Class<?>[][] collectSignatures(final Method[] methods) {
        final Class<?>[][] result = new Class<?>[methods.length][];
        for (int i = 0; i < methods.length; i++) {
            result[i] = methods[i].getParameterTypes();
        }
        return result;
    }

    public static boolean[] collectVarArgs(final Method[] methods) {
        final boolean[] result = new boolean[methods.length];
        for (int i = 0; i < methods.length; i++) {
            result[i] = methods[i].isVarArgs();
        }
        return result;
    }

    public static Class<?>[][] collectSignatures(final Constructor<?>[] constructors) {
        final Class<?>[][] result = new Class<?>[constructors.length][];
        for (int i = 0; i < constructors.length; i++) {
            result[i] = constructors[i].getParameterTypes();
        }
        return result;
    }

    public static boolean[] collectVarArgs(final Constructor<?>[] constructors) {
        final boolean[] result = new boolean[constructors.length];
        for (int i = 0; i < constructors.length; i++) {
            result[i] = constructors[i].isVarArgs();
        }
        return result;
    }
    
    /**
     * Selects the best match in signatures for the given argument types.
     * @param signatures
     * @param varArgs
     * @param argTypes
     * @return index of best signature, -1 if nothing matched
     * @throws AmbiguousSignatureMatchException if two signatures matched equally
     */
    public static int bestMatch(Class<?>[][] signatures, boolean[] varArgs, Class<?>[] argTypes) throws AmbiguousSignatureMatchException {
        return bestMatch(signatures, varArgs, new JavaSignatureComparator(argTypes));
    }
    
    /**
     * @param signatures
     * @param varArgs
     * @param jsCmp
     * @return index
     * @see #bestMatch(java.lang.Class<?>[][], boolean[], java.lang.Class<?>[]) 
     */
    public static int bestMatch(final Class<?>[][] signatures, final boolean[] varArgs, final JavaSignatureComparator jsCmp) throws AmbiguousSignatureMatchException {
        int bestLevel = JavaSignatureComparator.NO_MATCH+1;
        int bestIndex = -1;
        LinkedList<Integer> ambiguous = null;
        for (int i = 0; i < signatures.length; i++) {
            Class<?>[] sig = signatures[i];
            boolean var = varArgs[i];
            int level = jsCmp.applicability(sig, var);
            if (level > bestLevel) {
                // new is better
                ambiguous = null;
                bestLevel = level;
                bestIndex = i;
            } else if (level == bestLevel) {
                // check against previous matches
                if (ambiguous != null && !ambiguous.isEmpty()) {
                    boolean isAmbiguous = true;
                    Iterator<Integer> it = ambiguous.iterator();
                    while (it.hasNext()) {
                        int a = it.next();
                        int c = jsCmp.compareSpecificness(signatures[a], varArgs[a], sig, var);
                        if (c < 0) isAmbiguous = false;
                        if (c > 0) it.remove();
                    }
                    if (isAmbiguous) {
                        if (ambiguous.isEmpty()) {
                            bestIndex = i;
                        } else {
                            ambiguous.add(i);
                        }
                    }
                } else {
                    assert bestIndex > -1;
                    int c = jsCmp.compareSpecificness(signatures[bestIndex], varArgs[bestIndex], sig, var);
                    if (c > 0) {
                        bestIndex = i;
                    } else if (c == 0) {
                        if (ambiguous == null) ambiguous = new LinkedList<>();
                        ambiguous.add(bestIndex);
                        ambiguous.add(i);
                    }
                }
            }
        }
        if (ambiguous != null) {
            if (ambiguous.size() > 1){
                throw new AmbiguousSignatureMatchException(jsCmp, signatures, varArgs, Boxing.unboxIntegers(ambiguous));
            } else if (ambiguous.size() == 1) {
                return ambiguous.get(0);
            }
        }
        return bestIndex;
    }
    
    /**
     * Returns indices of all signatures that are a best match for the given
     * argument types.
     * @param signatures
     * @param varArgs
     * @param argTypes
     * @return signature indices
     */
    public static int[] candidateMatches(Class<?>[][] signatures, boolean[] varArgs, Class<?>[] argTypes) {
        return candidateMatches(signatures, varArgs, new JavaSignatureComparator(argTypes));
    }
    
    /** 
     * @param signatures
     * @param varArgs
     * @param jsCmp
     * @return signature indices
     * @see #candidateMatches(java.lang.Class<?>[][], boolean[], java.lang.Class<?>[]) 
     */
    public static int[] candidateMatches(Class<?>[][] signatures, boolean[] varArgs, JavaSignatureComparator jsCmp) {
        int bestLevel = JavaSignatureComparator.NO_MATCH+1;
        final LinkedList<Integer> candidates = new LinkedList<>();
        for (int i = 0; i < signatures.length; i++) {
            Class<?>[] sig = signatures[i];
            boolean var = varArgs[i];
            int level = jsCmp.applicability(sig, var);
            if (level > bestLevel) {
                // new is better than all previous
                candidates.clear();
                bestLevel = level;
                candidates.add(i);
            } else if (level == bestLevel) {
                // check against previous matches
                boolean isCandidate = true;
                Iterator<Integer> it = candidates.iterator();
                while (it.hasNext()) {
                    int k = it.next();
                    int c = jsCmp.compareSpecificness(signatures[k], varArgs[k], sig, var);
                    if (c < 0) isCandidate = false;
                    if (c > 0) it.remove();
                }
                if (isCandidate) {
                    candidates.add(i);
                }
            }
        }
        return Boxing.unboxIntegers(candidates);
    }
    
    /**
     * Fixes an arguments array to fit parameter types, 
     * where the last parameter is an varargs array.
     * @param paramsWithVarArgs
     * @param arguments
     * @return fixed arguments array
     */
    public static Object[] fixVarArgs(Class<?>[] paramsWithVarArgs, Object[] arguments) {
        int n = paramsWithVarArgs.length;
        return fixVarArgs(n, paramsWithVarArgs[n-1], arguments);
    }
    
    /**
     * Fixes an arguments array to fit a given length,
     * the last value is an array filled with varargs.
     * @param length
     * @param varArgType
     * @param arguments
     * @return fixed arguments array
     */
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public static Object[] fixVarArgs(int length, Class<?> varArgType, Object[] arguments) {
        final Object[] result;
        if (arguments.length == length && varArgType.isInstance(arguments[length-1])) {
            return arguments;
        } else {
            result = Arrays.copyOf(arguments, length, Object[].class);
        }
        final Object varArgs;
        Class<?> varArgElementType = varArgType.getComponentType();
        if (varArgElementType.isPrimitive()) {
            varArgs = Boxing.unboxAll(varArgElementType, arguments, length-1, -1);
        } else {
            int varLen = arguments.length - length + 1;
            varArgs = Array.newInstance(varArgElementType, varLen);
            System.arraycopy(arguments, length-1, varArgs, 0, varLen);
        }
        result[length-1] = varArgs;
        return result;
    }
    
    /**
     * Invokes {@code constructor}, automatically tries to fix varargs arguments.
     * @param <T>
     * @param constructor
     * @param args
     * @return new instance
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException 
     */
    public static <T> T newInstance(Constructor<T> constructor, Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (constructor.isVarArgs()) {
            args = fixVarArgs(constructor.getParameterTypes(), args);
        }
        return constructor.newInstance(args);
    }
    
    /**
     * Invokes {@code method}, automatically tries to fix varargs arguments.
     * @param instance
     * @param method
     * @param args
     * @return result
     * @throws IllegalAccessException
     * @throws InvocationTargetException 
     */
    public static Object invoke(Object instance, Method method, Object... args) throws IllegalAccessException, InvocationTargetException {
        if (method.isVarArgs()) {
            args = fixVarArgs(method.getParameterTypes(), args);
        }
        return method.invoke(instance, args);
    }
    
    public static final int NONE = 0;
    public static final int ANY = 0;
    public static final int STATIC = Modifier.STATIC;
    public static final int PRIVATE = Modifier.PRIVATE;
    public static final int PROTECTED = Modifier.PROTECTED;
    public static final int PUBLIC = Modifier.PUBLIC;
    public static final int NOT_DEFAULT = PRIVATE | PROTECTED | PUBLIC;
}
