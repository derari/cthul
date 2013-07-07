package org.cthul.objects.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cthul.objects.Boxing;

/**
 *
 */
public class Signatures {
    
    public static <T> Constructor<T> bestConstructor(Class<T> clazz, Object[] args) {
        return bestConstructor(collectConstructors(clazz), args);
    }
    
    public static <T> Constructor<T> bestConstructor(Class<T> clazz, Class<?>[] argTypes) {
        return bestConstructor(collectConstructors(clazz), argTypes);
    }
    
    public static <T> Constructor<T> bestConstructor(Constructor<T>[] methods, Object[] args) {
        return bestConstructor(methods, collectArgTypes(args));
    }
    
    public static <T> Constructor<T> bestConstructor(Constructor<T>[] methods, Class<?>[] argTypes) {
        return best(methods, collectSignatures(methods), collectVarArgs(methods), argTypes);
    }
    
    public static <T> Constructor<T>[] candidateConstructors(Class<T> clazz, Object[] args) {
        return candidateConstructors(collectConstructors(clazz), args);
    }
    
    public static <T> Constructor<T>[] candidateConstructors(Class<T> clazz, Class<?>[] argTypes) {
        return candidateConstructors(collectConstructors(clazz), argTypes);
    }
    
    public static <T> Constructor<T>[] candidateConstructors(Constructor<T>[] methods, Object[] args) {
        return candidateConstructors(methods, collectArgTypes(args));
    }
    
    public static <T> Constructor<T>[] candidateConstructors(Constructor<T>[] methods, Class<?>[] argTypes) {
        return candidates(methods, collectSignatures(methods), collectVarArgs(methods), argTypes);
    }
    
    public static Method bestMethod(Class<?> clazz, String name, Object[] args) {
        return bestMethod(collectMethods(clazz, name), args);
    }
    
    public static Method bestMethod(Class<?> clazz, String name, Class<?>[] argTypes) {
        return bestMethod(collectMethods(clazz, name), argTypes);
    }
    
    public static Method bestMethod(Method[] methods, Object[] args) {
        return bestMethod(methods, collectArgTypes(args));
    }
    
    public static Method bestMethod(Method[] methods, Class<?>[] argTypes) {
        return best(methods, collectSignatures(methods), collectVarArgs(methods), argTypes);
    }
    
    public static Method[] candidateMethods(Class<?> clazz, String name, Object[] args) {
        return candidateMethods(collectMethods(clazz, name), args);
    }
    
    public static Method[] candidateMethods(Class<?> clazz, String name, Class<?>[] argTypes) {
        return candidateMethods(collectMethods(clazz, name), argTypes);
    }
    
    public static Method[] candidateMethods(Method[] methods, Object[] args) {
        return candidateMethods(methods, collectArgTypes(args));
    }
    
    public static Method[] candidateMethods(Method[] methods, Class<?>[] argTypes) {
        return candidates(methods, collectSignatures(methods), collectVarArgs(methods), argTypes);
    }
    
    public static <T> T best(T[] items, Class<?>[][] signatures, boolean[] varArgs, Class<?>[] argTypes) {
        int i = bestMatch(signatures, varArgs, argTypes);
        return items[i];
    }
    
    public static <T> T[] candidates(T[] items, Class<?>[][] signatures, boolean[] varArgs, Class<?>[] argTypes) {
        final int[] indices = candidateMatches(signatures, varArgs, argTypes);
        T[] result = Arrays.copyOf(items, indices.length);
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
    
    private static Method[] collectMethods(Class<?> clazz, String name) {
        final List<Method> result = new ArrayList<>();
        for (Method m: clazz.getMethods()) {
            if (m.getName().equals(name)) result.add(m);
        }
        return result.toArray(new Method[result.size()]);
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

    private static <T> Constructor<T>[] collectConstructors(Class<T> clazz) {
        return (Constructor[]) clazz.getConstructors();
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
     * Finds the best match in signatures for the given argument types.
     * @param signatures
     * @param varArgs
     * @param jsCmp
     * @return index of best signature, -1 if nothing matched
     * @throws AmbiguousSignatureMatchException if two signatures matched equally
     */
    public static int bestMatch(Class<?>[][] signatures, boolean[] varArgs, Class<?>[] argTypes) {
        return bestMatch(signatures, varArgs, new JavaSignatureComparator(argTypes));
    }
    
    /** @see #bestMatch(java.lang.Class<?>[][], boolean[], java.lang.Class<?>[]) */
    public static int bestMatch(Class<?>[][] signatures, boolean[] varArgs, JavaSignatureComparator jsCmp){
        int bestLevel = JavaSignatureComparator.NO_MATCH-1;
        int bestIndex = -1;
        Class<?>[] bestSig = null;
        boolean bestVarArgs = false;
        for (int i = 0; i < signatures.length; i++) {
            Class<?>[] sig = signatures[i];
            boolean var = varArgs[i];
            int level = jsCmp.applicability(sig, var);
            if (level <= bestLevel) {
                if (bestIndex > -1) {
                    int c = jsCmp.compareSpecificity(bestSig, bestVarArgs, sig, var);
                    if (c == 0) throw new AmbiguousSignatureMatchException(jsCmp, signatures, varArgs);
                    if (c < 0) continue;
                }
                bestLevel = level;
                bestIndex = i;
                bestSig = sig;
                bestVarArgs = var;
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
    
    /** @see #candidateMatches(java.lang.Class<?>[][], boolean[], java.lang.Class<?>[]) */
    public static int[] candidateMatches(Class<?>[][] signatures, boolean[] varArgs, JavaSignatureComparator jsCmp) {
        int bestLevel = JavaSignatureComparator.NO_MATCH-1;
        final Candidate candidateList = new Candidate(-1);
        Candidate candidateListEnd = candidateList;
        int candidateCount = 0;
        signatures: for (int i = 0; i < signatures.length; i++) {
            Class<?>[] sig = signatures[i];
            boolean var = varArgs[i];
            int level = jsCmp.applicability(sig, var);
            if (level <= bestLevel) {
                if (level < bestLevel) {
                    // new is better than all previous
                    candidateList.clear();
                    candidateCount = 0;
                    candidateListEnd = candidateList;
                    bestLevel = level;
                } else {
                    // check against previous matches
                    Candidate lastC = candidateList;
                    Candidate c = lastC.next;
                    while (c != null) {
                        int cmp = jsCmp.compareSpecificity(signatures[c.index], varArgs[c.index], sig, var);
                        if (cmp < 0) {
                            // existing is more specific
                            continue signatures;
                        } else if (cmp > 0) {
                            // new is more specific
                            lastC.removeNext();
                            if (c.next == null) candidateListEnd = lastC;
                            c = lastC;
                            candidateCount--;
                        }
                        lastC = c;
                        c = c.next;
                    }
                }
                candidateListEnd = candidateListEnd.append(i);
                candidateCount++;
            }
        }
        final int[] result = new int[candidateCount];
        Candidate c = candidateList.next;
        for (int i = 0; i < candidateCount; i++) {
            result[i] = c.index;
            c = c.next;
        }
        return result;
    }
    
    private static class Candidate {
        int index;
        Candidate next = null;
        public Candidate(int index) {
            this.index = index;
        }
        Candidate append(int i) {
            return next = new Candidate(i);
        }
        void removeNext() {
            next = next.next;
        }
        void clear() {
            next = null;
        }
    }
    
    public static Object[] fixVarArgs(Class<?>[] paramsWithVarArgs, Object[] arguments) {
        int n = paramsWithVarArgs.length;
        return fixVarArgs(n, paramsWithVarArgs[n-1], arguments);
    }
    
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public static Object[] fixVarArgs(int paramC, Class<?> varArgType, Object[] arguments) {
        final Object[] result;
        if (arguments.length != paramC) {
            result = Arrays.copyOf(arguments, paramC);
        } else {
            if (varArgType.isInstance(arguments[paramC-1])) {
                return arguments;
            }
            result = arguments;
        }
        final Object varArgs;
        Class<?> varArgElementType = varArgType.getComponentType();
        if (varArgElementType.isPrimitive()) {
            varArgs = Boxing.unbox(varArgElementType, arguments, paramC-1);
        } else {
            int varLen = arguments.length - paramC + 1;
            varArgs = Array.newInstance(varArgElementType, varLen);
            System.arraycopy(arguments, paramC-1, varArgs, 0, varLen);
        }
        result[paramC-1] = varArgs;
        return result;
    }
    
}
