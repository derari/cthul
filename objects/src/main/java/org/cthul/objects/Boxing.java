package org.cthul.objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Boxing {
    
    private static final Class<?>[] PRIMITIVES = {
                boolean.class, byte.class, char.class, double.class,
                float.class, int.class, long.class, short.class};

    private static final Class<?>[] BOXES = {
                Boolean.class, Byte.class, Character.class, Double.class,
                Float.class, Integer.class, Long.class, Short.class};
    
    private static final int I_BOOLEAN =    0;
    private static final int I_BYTE =       1;
    private static final int I_CHARACTER =  2;
    private static final int I_DOUBLE =     3;
    private static final int I_FLOAT =      4;
    private static final int I_INTEGER =    5;
    private static final int I_LONG =       6;
    private static final int I_SHORT =      7;

    private static final Map<Class<?>, Class<?>> BOXED;
    private static final Map<Class<?>, Class<?>> UNBOXED;
    private static final Map<Class<?>, Class<?>> AUTO_BOXED;
    
    private static final Map<Class<?>, Integer> INDICES;
    
    private static final Map<Class<?>, Class<?>> P_BOXED;
    private static final Map<Class<?>, Class<?>> P_UNBOXED;
    private static final Map<Class<?>, Class<?>> P_AUTO_BOXED;
    
    public static Class<?> autoBoxed(Class<?> clazz) {
        return AUTO_BOXED.get(clazz);
    }
    
    public static Class<?> boxed(Class<?> clazz) {
        return BOXED.get(clazz);
    }
    
    public static Class<?> unboxed(Class<?> clazz) {
        return UNBOXED.get(clazz);
    }

    public static Map<Class<?>, Class<?>> autoBoxed() {
        return P_AUTO_BOXED;
    }
    
    public static Map<Class<?>, Class<?>> boxed() {
        return P_BOXED;
    }
    
    public static Map<Class<?>, Class<?>> unboxed() {
        return P_UNBOXED;
    }    
    
    static {
        BOXED = new HashMap<>();
        UNBOXED = new HashMap<>();
        AUTO_BOXED = new HashMap<>();
        INDICES = new HashMap<>();
        for (int i = 0; i < PRIMITIVES.length; i++) {
            Class<?> p = PRIMITIVES[i], b = BOXES[i];
            BOXED.put(p, b);
            UNBOXED.put(b, p);
            AUTO_BOXED.put(p, b);
            AUTO_BOXED.put(b, p);
            INDICES.put(p, i);
            INDICES.put(b, i);
        }
        
        P_BOXED = Collections.unmodifiableMap(BOXED);
        P_UNBOXED = Collections.unmodifiableMap(UNBOXED);
        P_AUTO_BOXED = Collections.unmodifiableMap(AUTO_BOXED);
    }

    public static Object unbox(Class<?> elementType, Object[] src, int srcPos) {
        return unbox(elementType, src, srcPos, src.length - srcPos);
    }
    
    public static Object unbox(Class<?> elementType, Object[] src, int srcPos, int len) {
        switch (INDICES.get(elementType)) {
            case I_BOOLEAN:     return unboxBooleans(src, srcPos, len);
            case I_BYTE:        return unboxBytes(src, srcPos, len);
            case I_CHARACTER:   return unboxChars(src, srcPos, len);
            case I_DOUBLE:      return unboxDoubles(src, srcPos, len);
            case I_FLOAT:       return unboxFloats(src, srcPos, len);
            case I_INTEGER:     return unboxInts(src, srcPos, len);
            case I_LONG:        return unboxLongs(src, srcPos, len);
            case I_SHORT:       return unboxShorts(src, srcPos, len);
            default:
                throw new IllegalArgumentException("No primitive/box: " + elementType);
        }
    }
    
    public static boolean[] unboxBooleans(Object[] src, int srcPos, int len) {
        final boolean[] result = new boolean[len];
        for (int i = 0; i < len; i++) {
            Boolean n = (Boolean) src[srcPos + i];
            result[i] = n.booleanValue();
        }
        return result;
    }
    
    public static byte[] unboxBytes(Object[] src, int srcPos, int len) {
        final byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            Number n = (Number) src[srcPos + i];
            result[i] = n.byteValue();
        }
        return result;
    }
    
    public static char[] unboxChars(Object[] src, int srcPos, int len) {
        final char[] result = new char[len];
        for (int i = 0; i < len; i++) {
            Character n = (Character) src[srcPos + i];
            result[i] = n.charValue();
        }
        return result;
    }
    
    public static double[] unboxDoubles(Object[] src, int srcPos, int len) {
        final double[] result = new double[len];
        for (int i = 0; i < len; i++) {
            Number n = (Number) src[srcPos + i];
            result[i] = n.doubleValue();
        }
        return result;
    }
    
    public static float[] unboxFloats(Object[] src, int srcPos, int len) {
        final float[] result = new float[len];
        for (int i = 0; i < len; i++) {
            Number n = (Number) src[srcPos + i];
            result[i] = n.floatValue();
        }
        return result;
    }
    
    public static int[] unboxInts(Object[] src, int srcPos, int len) {
        final int[] result = new int[len];
        for (int i = 0; i < len; i++) {
            Number n = (Number) src[srcPos + i];
            result[i] = n.intValue();
        }
        return result;
    }
    
    public static long[] unboxLongs(Object[] src, int srcPos, int len) {
        final long[] result = new long[len];
        for (int i = 0; i < len; i++) {
            Number n = (Number) src[srcPos + i];
            result[i] = n.longValue();
        }
        return result;
    }
    
    public static short[] unboxShorts(Object[] src, int srcPos, int len) {
        final short[] result = new short[len];
        for (int i = 0; i < len; i++) {
            Number n = (Number) src[srcPos + i];
            result[i] = n.shortValue();
        }
        return result;
    }
    
}
