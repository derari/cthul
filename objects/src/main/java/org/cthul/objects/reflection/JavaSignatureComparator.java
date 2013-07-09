package org.cthul.objects.reflection;

import org.cthul.objects.Boxing;

/**
 * Compares how method signatures match for a given argument signature.
 */
public class JavaSignatureComparator {
    
    public static final int MATCH =          400;
    public static final int MATCH_WILDCARD = 300;
    public static final int MATCH_BOXING =   200;
    public static final int MATCH_VARARGS =  100;
    public static final int NO_MATCH =       -99;

    private final Class<?>[] argTypes;

    public JavaSignatureComparator(Class<?>... argTypes) {
        this.argTypes = argTypes;
    }

    public Class<?>[] getReferenceSignature() {
        return argTypes.clone();
    }
    
    /**
     * Compares two signatures against a reference signature, according to
     * the rules by which Java resolves method overloading.
     * It is assumed that both signatures have the same 
     * {@linkplain #applicability(java.lang.Class<?>[], boolean) applicability level}.
     * @return {@code <0}: a is more specific; 
     *         {@code >0}: b is more specific; 
     *         {@code =0}: ambiguous
     */
    public int compareSpecificness(final Class<?>[] a, boolean a_varArgs, final Class<?>[] b, boolean b_varArgs) {
        final int a_fixLen = a.length - (a_varArgs ? 1 : 0);
        final int b_fixLen = b.length - (b_varArgs ? 1 : 0);
        final int fixLen = Math.min(a_fixLen, b_fixLen);
        int c = 0; // result of comparison
        
        // compare fix args
        for (int i = 0; i < fixLen; i++) {
            int newC = compareParameter(a[i], b[i]);
            if (newC != 0 && newC != c) {
                if (c == 0) c = newC;
                else return 0; // ambiguous
            }
        }
        
        // compare varargs of the shorter signature against fixargs of other
        Class<?> a_varType = a_varArgs ? a[a_fixLen] : null;
        Class<?> b_varType = b_varArgs ? b[b_fixLen] : null;
        if (a_varArgs && a_fixLen < b_fixLen) {
            // compare remaining fixArgs of b
            for (int i = fixLen; i < b_fixLen; i++) {
                int newC = compareParameter(a_varType, b[i]);
                if (newC != 0 && newC != c) {
                    if (c == 0) c = newC;
                    else return 0; // ambiguous
                }
            }
        }
        if (b_varArgs && b_fixLen < a_fixLen) {
            // compare remaining fixArgs of a
            for (int i = fixLen; i < a_fixLen; i++) {
                int newC = compareParameter(a[i], b_varType);
                if (newC != 0 && newC != c) {
                    if (c == 0) c = newC;
                    else return 0; // ambiguous
                }
            }
        }
        
        boolean a_varArgsUsed = a_varArgs && argTypes.length > a_fixLen;
        boolean b_varArgsUsed = b_varArgs && argTypes.length > b_fixLen;
        if (a_varArgsUsed && b_varArgsUsed) {
            // if var args are needed, compare them
            int newC = compareParameter(a_varType, b_varType);
            if (newC != 0 && newC != c) {
                if (c == 0) c = newC;
                else return 0; // ambiguous
            }
        }
        if (c == 0) {
            // if still equal, more fix args wins
            if (a_fixLen > b_fixLen) {
                return -1;
            } else if (b_fixLen > a_fixLen) {
                return  1;
            }
        }
        return c;
    }
    
    /**
     * Compares two parameter types.
     * 
     * @param a
     * @param b
     * @return 
     */
    private int compareParameter(Class<?> a, Class<?> b) {
        final int a_preferred = -1;
//        if (index >= argTypes.length || argTypes[index] == null) {
//            return 0;
//        }
        if (a.equals(b)) {
            return  0;
        } else if (b.isAssignableFrom(a)) {
            // a is more specific
            return  a_preferred;
        } else if (a.isAssignableFrom(b)) {
            // b is more specific
            return -a_preferred;
        } else {
            return  0; // not compatible
        }
    }
    
//    /**
//     * If an argument type is missing ({@code null} indicates wildcard),
//     * the signature with the more generic type is preferred.
//     * 
//     * @param index
//     * @return 
//     */
//    private boolean moreSpecificPreferred(int index) {
//        if (index < 0) return true;
//        if (index >= argTypes.length) return false;
//        return argTypes[index] != null;
//    }
    
    /**
     * Returns the 
     * 
     * @param paramTypes
     * @param varArgs
     * @return 
     */
    public int applicability(final Class<?>[] paramTypes, boolean varArgs) {
        int bestLevel = 0;
        final int fixArgs = paramTypes.length - (varArgs ? 1 : 0);
        if (fixArgs > argTypes.length 
                || (!varArgs && fixArgs != argTypes.length)) {
            return NO_MATCH;
        }
        for (int i = 0; i < fixArgs; i++) {
            int m = applicable(argTypes[i], paramTypes[i], false);
            if (m == NO_MATCH) {
                return NO_MATCH;
            }
            if (m > bestLevel) bestLevel = m;
        }
        if (varArgs) {
            Class<?> comp = paramTypes[fixArgs].getComponentType();
            for (int i = fixArgs; i < argTypes.length; i++) {
                // varargs not allowed, we test against the component type
                int m = applicable(argTypes[i], comp, false);
                if (m == NO_MATCH) {
                    return NO_MATCH;
                }
//                if (m > bestLevel) bestLevel = m;
            }
            return MATCH_VARARGS;
        }
        return bestLevel;
    }
    
    private int applicable(Class<?> arg, Class<?> param, boolean varArgs) {
        if (arg == null) {
            return MATCH;
        }
        if (param == null) {
            return MATCH_WILDCARD;
        }
        if (param.isAssignableFrom(arg)) {
            return MATCH;
        }
        Class<?> boxArg = Boxing.autoBoxed(arg);
        if (boxArg != null && param.isAssignableFrom(boxArg)) {
            return MATCH_BOXING;
        }
        if (varArgs && param.isArray()) {
            Class<?> comp = param.getComponentType();
            if (comp.isAssignableFrom(arg) || 
                    (boxArg != null && comp.isAssignableFrom(boxArg))) {
                return MATCH_VARARGS;
            }
        }
        return NO_MATCH;
    }
    
}
