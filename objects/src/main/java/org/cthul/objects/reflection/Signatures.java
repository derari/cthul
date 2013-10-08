package org.cthul.objects.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static <T> Constructor<T> bestConstructor(Class<T> clazz, Object[] args) throws AmbiguousSignatureMatchException {
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
    public static <T> Constructor<T> bestConstructor(Class<T> clazz, Class<?>[] argTypes) throws AmbiguousSignatureMatchException {
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
    public static <T> Constructor<T> bestConstructor(Constructor<T>[] constructors, Object[] args) throws AmbiguousSignatureMatchException {
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
    public static <T> Constructor<T> bestConstructor(Constructor<T>[] constructors, Class<?>[] argTypes) throws AmbiguousSignatureMatchException {
        return best(constructors, collectSignatures(constructors), collectVarArgs(constructors), argTypes);
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
     * @param args
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
    public static Method bestMethod(Class<?> clazz, String name, Object[] args) throws AmbiguousSignatureMatchException {
        return bestMethod(collectMethods(clazz, name), args);
    }
    
    /**
     * Finds the best method for the given argument types.
     * @param clazz
     * @param name
     * @param args
     * @return method
     * @throws AmbiguousSignatureMatchException if multiple methods match equally
     */
    public static Method bestMethod(Class<?> clazz, String name, Class<?>[] argTypes) throws AmbiguousSignatureMatchException {
        return bestMethod(collectMethods(clazz, name), argTypes);
    }
    
    /**
     * Selects the best method for the given arguments.
     * @param clazz
     * @param name
     * @param args
     * @return method
     * @throws AmbiguousSignatureMatchException if multiple methods match equally
     */
    public static Method bestMethod(Method[] methods, Object[] args) throws AmbiguousSignatureMatchException {
        return bestMethod(methods, collectArgTypes(args));
    }
    
    /**
     * Selects the best method for the given argument types.
     * @param clazz
     * @param name
     * @param args
     * @return method
     * @throws AmbiguousSignatureMatchException if multiple methods match equally
     */
    public static Method bestMethod(Method[] methods, Class<?>[] argTypes) throws AmbiguousSignatureMatchException {
        return best(methods, collectSignatures(methods), collectVarArgs(methods), argTypes);
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
     * @param args
     * @return methods
     */
    public static Method[] candidateMethods(Class<?> clazz, String name, Class<?>[] argTypes) {
        return candidateMethods(collectMethods(clazz, name), argTypes);
    }
    
    /**
     * Selects the best equally-matching methods for the given arguments.
     * @param clazz
     * @param name
     * @param args
     * @return methods
     */
    public static Method[] candidateMethods(Method[] methods, Object[] args) {
        return candidateMethods(methods, collectArgTypes(args));
    }
    
    /**
     * Selects the best equally-matching methods for the given argument types.
     * @param clazz
     * @param name
     * @param args
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
        T[] result = (T[]) Array.newInstance(items.getClass().getComponentType(), indices.length);
        for (int i = 0; i < indices.length; i++) {
            result[i] = items[indices[i]];
        }
        return result;
    }
    
    private static Class<?>[] collectArgTypes(final Object[] args) {
        final Class<?>[] result = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            Object a = args[i];
            result[i] = a != null ? a.getClass() : null;
        }
        return result;
    }
    
    public static Method[] collectMethods(Class<?> clazz, String name) {
        final List<Method> result = new ArrayList<>();
        for (Method m: clazz.getMethods()) {
            if (m.getName().equals(name)) result.add(m);
        }
        return result.toArray(new Method[result.size()]);
    }
    
    public static Method[] collectMethods(Class<?> clazz, String name, int include, int exclude) {
        final List<Method> result = new ArrayList<>();
        collectMethods(result, new ArrayList<Class[]>(), new HashSet<Class<?>>(), clazz, name, include, exclude);
        return result.toArray(new Method[result.size()]);
    }
    
    private static void collectMethods(List<Method> methods, List<Class[]> signatures, Set<Class<?>> visited, Class<?> clazz, String name, int include, int exclude) {
        if (clazz == null || !visited.add(clazz)) return;
        for (Method m: clazz.getDeclaredMethods()) {
            String n = m.getName();
            int mod = m.getModifiers();
            if (name.equals(n) && include(mod, include, exclude)) {
                Class[] sig = m.getParameterTypes();
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
        for (Class i: clazz.getInterfaces()) {
            collectMethods(methods, signatures, visited, i, name, include, exclude);
        }
    }
    
    public static <T> Constructor<T>[] collectConstructors(Class<T> clazz) {
        return (Constructor[]) clazz.getConstructors();
    }
    
    public static <T> Constructor<T>[] collectConstructors(Class<T> clazz, int include, int exclude) {
        final List<Constructor> result = new ArrayList<>();
        for (Constructor c: clazz.getDeclaredConstructors()) {
            int mod = c.getModifiers();
            if (include(mod, include, exclude)) {
                result.add(c);
            }
        }
        return (Constructor[]) result.toArray(new Constructor[result.size()]);
    }
    
    private static boolean include(int mod, int include, int exclude) {
        if (include == ANY) {
            return (mod & exclude) == 0;
        } else {
            return (mod & include) != 0 && (mod & exclude) == 0;
        }
    }
    
    private static Class<?>[][] collectSignatures(final Method[] methods) {
        final Class<?>[][] result = new Class<?>[methods.length][];
        for (int i = 0; i < methods.length; i++) {
            result[i] = methods[i].getParameterTypes();
        }
        return result;
    }

    private static boolean[] collectVarArgs(final Method[] methods) {
        final boolean[] result = new boolean[methods.length];
        for (int i = 0; i < methods.length; i++) {
            result[i] = methods[i].isVarArgs();
        }
        return result;
    }

    private static Class<?>[][] collectSignatures(final Constructor<?>[] constructors) {
        final Class<?>[][] result = new Class<?>[constructors.length][];
        for (int i = 0; i < constructors.length; i++) {
            result[i] = constructors[i].getParameterTypes();
        }
        return result;
    }

    private static boolean[] collectVarArgs(final Constructor<?>[] constructors) {
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
    
    /** @see #bestMatch(java.lang.Class<?>[][], boolean[], java.lang.Class<?>[]) */
    public static int bestMatch(Class<?>[][] signatures, boolean[] varArgs, JavaSignatureComparator jsCmp) throws AmbiguousSignatureMatchException {
        int bestLevel = JavaSignatureComparator.NO_MATCH+1;
        int bestIndex = -1;
        Class<?>[] bestSig = null;
        LinkedList<Class<?>[]> ambiguous = null;
        boolean bestVarArgs = false;
        signatures: for (int i = 0; i < signatures.length; i++) {
            Class<?>[] sig = signatures[i];
            boolean var = varArgs[i];
            int level = jsCmp.applicability(sig, var);
            if (level < bestLevel) continue;
            if (level > bestLevel) {
                // new is better
                ambiguous = null;
            } else {
                assert level == bestLevel;
                // check against previous matches
                if (ambiguous != null) {
                    Iterator<Class<?>[]> it = ambiguous.iterator();
                    while (it.hasNext()) {
                        int c = jsCmp.compareSpecificness(it.next(), bestVarArgs, sig, var);
                        if (c < 0) continue signatures;
                        if (c > 0) it.remove();
                    }
                    ambiguous.add(sig);
                } else {
                    assert bestIndex > -1;
                    int c = jsCmp.compareSpecificness(bestSig, bestVarArgs, sig, var);
                    if (c < 0) continue;
                    if (c == 0) {
                        ambiguous = new LinkedList<>();
                        ambiguous.add(bestSig);
                        ambiguous.add(sig);
                    }
                }
            }
            bestLevel = level;
            bestIndex = i;
            bestSig = sig;
            bestVarArgs = var;
        }
        if (ambiguous != null && ambiguous.size() > 1) {
            throw new AmbiguousSignatureMatchException(jsCmp, signatures, varArgs);
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
    
    /** @see #candidateMatches(java.lang.Class<?>[][], boolean[], java.lang.Class<?>[]) */
    public static int[] candidateMatches(Class<?>[][] signatures, boolean[] varArgs, JavaSignatureComparator jsCmp) {
        int bestLevel = JavaSignatureComparator.NO_MATCH+1;
        final LinkedList<Integer> candidates = new LinkedList<>();
        signatures: for (int i = 0; i < signatures.length; i++) {
            Class<?>[] sig = signatures[i];
            boolean var = varArgs[i];
            int level = jsCmp.applicability(sig, var);
            if (level < bestLevel) continue;
            if (level > bestLevel) {
                // new is better than all previous
                candidates.clear();
                bestLevel = level;
            } else {
                // check against previous matches
                Iterator<Integer> it = candidates.iterator();
                while (it.hasNext()) {
                    int k = it.next();
                    int cmp = jsCmp.compareSpecificness(signatures[k], varArgs[k], sig, var);
                    if (cmp < 0) continue signatures;
                    if (cmp > 0) it.remove();
                }
            }
            candidates.add(i);
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
    
    public static final int NONE = 0;
    public static final int ANY = -1;
    public static final int STATIC = Modifier.STATIC;
    public static final int PRIVATE = Modifier.PRIVATE;
    public static final int PROTECTED = Modifier.PROTECTED;
    public static final int PUBLIC = Modifier.PUBLIC;
    public static final int NOT_DEFAULT = PRIVATE | PROTECTED | PUBLIC;
    
}
