package org.cthul.objects.reflection;

import org.cthul.objects.Boxing;

/**
 *
 */
public class JavaSignatureComparator {
    
    public static final int MATCH =          0;
    public static final int MATCH_WILDCARD = 1;
    public static final int MATCH_BOXING =   2;
    public static final int MATCH_VARARGS =  3;
    public static final int NO_MATCH = Integer.MAX_VALUE;

    private final Class<?>[] argTypes;

    public JavaSignatureComparator(Class<?>... argTypes) {
        this.argTypes = argTypes;
    }
    
    /**
     * 
     * @return <0: a is more specific; >0: b is more specific; 0: ambiguous
     */
    public int compareSpecificity(final Class<?>[] a, boolean a_varArgs, final Class<?>[] b, boolean b_varArgs) {
        final int a_fixArgs = a.length - (a_varArgs ? 1 : 0);
        final int b_fixArgs = b.length - (b_varArgs ? 1 : 0);
        final int fixLen = Math.min(a_fixArgs, b_fixArgs);
        int c = 0;
        for (int i = 0; i < fixLen; i++) {
            int newC = compareSpecificity(a[i], b[i], i);
            if (newC != 0 && newC != c) {
                if (c == 0) c = newC;
                else return 0; // ambiguous
            }
        }
        Class<?> a_varType = a_varArgs ? a[a_fixArgs] : null;
        Class<?> b_varType = b_varArgs ? b[b_fixArgs] : null;
        if (a_varArgs && a_fixArgs < b_fixArgs) {
            // compare remaining fixArgs of b
            for (int i = fixLen; i < b_fixArgs; i++) {
                int newC = compareSpecificity(a_varType, b[i], i);
                if (newC != 0 && newC != c) {
                    if (c == 0) c = newC;
                    else return 0; // ambiguous
                }
            }
        }
        if (b_varArgs && b_fixArgs < a_fixArgs) {
            // compare remaining fixArgs of a
            for (int i = fixLen; i < a_fixArgs; i++) {
                int newC = compareSpecificity(a[i], b_varType, i);
                if (newC != 0 && newC != c) {
                    if (c == 0) c = newC;
                    else return 0; // ambiguous
                }
            }
        }
        boolean a_varArgsUsed = a_varArgs && argTypes.length > a_fixArgs;
        boolean b_varArgsUsed = b_varArgs && argTypes.length > b_fixArgs;
        if (a_varArgsUsed && b_varArgsUsed) {
            // if var args are needed, compare them
            int i = Math.max(a_fixArgs, b_fixArgs);
            int newC = compareSpecificity(a_varType, b_varType, i);
            if (newC != 0 && newC != c) {
                if (c == 0) c = newC;
                else return 0; // ambiguous
            }
        }
        if (c == 0) {
            // if still equal, more fix args wins
            if (a_fixArgs > b_fixArgs) {
                return -1;
            } else if (b_fixArgs > a_fixArgs) {
                return  1;
            }
        }
        return c;
    }
    
    private int compareSpecificity(Class<?> a, Class<?> b, int i) {
        final int a_wins = -1;
        final int specifc_wins = moreSpecificWins(i) ? 1 : -1;
        if (a.equals(b)) {
            return  0;
        } else if (a.isAssignableFrom(b)) {
            // b is more specific
            return -a_wins * specifc_wins;
        } else if (b.isAssignableFrom(a)) {
            // a is more specific
            return  a_wins * specifc_wins;
        } else {
            return  0; // not compatible
        }
    }
    
    private boolean moreSpecificWins(int i) {
        if (i < 0) return true;
        if (i >= argTypes.length) return false;
        return argTypes[i] != null;
    }
    
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
