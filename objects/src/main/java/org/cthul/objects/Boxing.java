package org.cthul.objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Utils for dealing with primitives and boxed values.
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
    
    public static Class<?> boxingType(Class<?> clazz) {
        return AUTO_BOXED.get(clazz);
    }
    
    public static Class<?> box(Class<?> clazz) {
        return BOXED.get(clazz);
    }
    
    public static Class<?> primitive(Class<?> clazz) {
        return UNBOXED.get(clazz);
    }

    public static Map<Class<?>, Class<?>> boxingTypes() {
        return P_AUTO_BOXED;
    }
    
    public static Map<Class<?>, Class<?>> boxes() {
        return P_BOXED;
    }
    
    public static Map<Class<?>, Class<?>> primitives() {
        return P_UNBOXED;
    }    
    
    static {
        BOXED = new HashMap<>();
        UNBOXED = new HashMap<>();
        AUTO_BOXED = new HashMap<>();
        INDICES = new HashMap<>();
        for (int i = 0; i < PRIMITIVES.length; i++) {
            Class<?> prim = PRIMITIVES[i], box = BOXES[i];
            BOXED.put(prim, box);
            UNBOXED.put(box, prim);
            AUTO_BOXED.put(prim, box);
            AUTO_BOXED.put(box, prim);
            INDICES.put(prim, i);
            INDICES.put(box, i);
        }
        
        P_BOXED = Collections.unmodifiableMap(BOXED);
        P_UNBOXED = Collections.unmodifiableMap(UNBOXED);
        P_AUTO_BOXED = Collections.unmodifiableMap(AUTO_BOXED);
    }

    public static Object unbox(Class<?> elementType, Object[] src, int srcPos) {
        return unbox(elementType, src, srcPos, -1);
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
    
    public static Object unbox(Object[] src, int srcPos) {
        return unbox(src, srcPos, -1);
    }
    
    public static Object unbox(Object[] src, int srcPos, int len) {
        if (srcPos >= src.length) {
            throw new IndexOutOfBoundsException(String.valueOf(srcPos));
        }
        Class<?> elementType = src[srcPos].getClass();
        return unbox(elementType, src, srcPos, len);
    }
    
    public static boolean[] unboxBooleans(Object[] src, int srcPos, int len) {
        if (len < 0) len = src.length - srcPos + len + 1;
        final boolean[] result = new boolean[len];
        for (int i = 0; i < len; i++) {
            Boolean n = (Boolean) src[srcPos + i];
            result[i] = n.booleanValue();
        }
        return result;
    }
    
    public static byte[] unboxBytes(Object[] src, int srcPos, int len) {
        if (len < 0) len = src.length - srcPos + len + 1;
        final byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            Number n = (Number) src[srcPos + i];
            result[i] = n.byteValue();
        }
        return result;
    }
    
    public static char[] unboxChars(Object[] src, int srcPos, int len) {
        if (len < 0) len = src.length - srcPos + len + 1;
        final char[] result = new char[len];
        for (int i = 0; i < len; i++) {
            Character n = (Character) src[srcPos + i];
            result[i] = n.charValue();
        }
        return result;
    }
    
    public static double[] unboxDoubles(Object[] src, int srcPos, int len) {
        if (len < 0) len = src.length - srcPos + len + 1;
        final double[] result = new double[len];
        for (int i = 0; i < len; i++) {
            Number n = (Number) src[srcPos + i];
            result[i] = n.doubleValue();
        }
        return result;
    }
    
    public static float[] unboxFloats(Object[] src, int srcPos, int len) {
        if (len < 0) len = src.length - srcPos + len + 1;
        final float[] result = new float[len];
        for (int i = 0; i < len; i++) {
            Number n = (Number) src[srcPos + i];
            result[i] = n.floatValue();
        }
        return result;
    }
    
    public static int[] unboxInts(Object[] src, int srcPos, int len) {
        if (len < 0) len = src.length - srcPos + len + 1;
        final int[] result = new int[len];
        for (int i = 0; i < len; i++) {
            Number n = (Number) src[srcPos + i];
            result[i] = n.intValue();
        }
        return result;
    }
    
    public static long[] unboxLongs(Object[] src, int srcPos, int len) {
        if (len < 0) len = src.length - srcPos + len + 1;
        final long[] result = new long[len];
        for (int i = 0; i < len; i++) {
            Number n = (Number) src[srcPos + i];
            result[i] = n.longValue();
        }
        return result;
    }
    
    public static short[] unboxShorts(Object[] src, int srcPos, int len) {
        if (len < 0) len = src.length - srcPos + len + 1;
        final short[] result = new short[len];
        for (int i = 0; i < len; i++) {
            Number n = (Number) src[srcPos + i];
            result[i] = n.shortValue();
        }
        return result;
    }
    
    public static boolean[] unboxBooleans(Object[] src) {
        return unboxBooleans(src, 0, -1);
    }
    
    public static byte[] unboxBytes(Object[] src) {
        return unboxBytes(src, 0, -1);
    }
    
    public static char[] unboxChars(Object[] src) {
        return unboxChars(src, 0, -1);
    }
    
    public static double[] unboxDoubles(Object[] src) {
        return unboxDoubles(src, 0, -1);
    }
    
    public static float[] unboxFloats(Object[] src) {
        return unboxFloats(src, 0, -1);
    }
    
    public static int[] unboxInts(Object[] src) {
        return unboxInts(src, 0, -1);
    }
    
    public static long[] unboxLongs(Object[] src) {
        return unboxLongs(src, 0, -1);
    }
    
    public static short[] unboxShorts(Object[] src) {
        return unboxShorts(src, 0, -1);
    }
}
