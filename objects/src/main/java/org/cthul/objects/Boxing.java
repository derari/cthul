package org.cthul.objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.cthul.objects.internal.BoxingBase;

/**
 * Utils for dealing with primitives and boxed values.
 */
public class Boxing extends BoxingBase {
    
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
    
    /**
     * Returns the boxed type for a primitive and vice versa, or {@code null}.
     * @param type primitive type
     * @return boxed type
     */
    public static Class<?> boxingType(Class<?> clazz) {
        return AUTO_BOXED.get(clazz);
    }
    
    /**
     * Returns the boxed type for a primitive, or {@code null}.
     * @param type primitive type
     * @return boxed type
     */
    public static Class<?> box(Class<?> type) {
        return BOXED.get(type);
    }
    
    
    /**
     * Returns the primitive type for a box, or {@code null}.
     * @param type boxed type
     * @return primitve type
     */
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
    
    protected static int tId(Class<?> type) {
        while (type.isArray()) {
            type = type.getComponentType();
        }
        Integer i = INDICES.get(type);
        if (i == null) return -1;
        return i;
    }

    /**
     * Transforms an array of {@code Boolean}, {@code Character}, or {@code Number}
     * into a primitive array.
     * @param type target type
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return primitive array
     */
    public static Object unbox(Class<?> type, Object[] src, int srcPos, int len) {
        switch (tId(type)) {
            case I_BOOLEAN:     return unboxBooleans(src, srcPos, len);
            case I_BYTE:        return unboxBytes(src, srcPos, len);
            case I_CHARACTER:   return unboxCharacters(src, srcPos, len);
            case I_DOUBLE:      return unboxDoubles(src, srcPos, len);
            case I_FLOAT:       return unboxFloats(src, srcPos, len);
            case I_INTEGER:     return unboxIntegers(src, srcPos, len);
            case I_LONG:        return unboxLongs(src, srcPos, len);
            case I_SHORT:       return unboxShorts(src, srcPos, len);
        }
        throw new IllegalArgumentException("No primitive/box: " + type);
    }
    
    /**
     * Transforms an array of {@code Boolean}, {@code Character}, or {@code Number}
     * into a primitive array.
     * @param type target type
     * @param src source array
     * @return primitive array
     */
    public static Object unbox(Class<?> type, Object[] src) {
        return unbox(type, src, 0, -1);
    }
    
    /**
     * Transforms an array of {@code Boolean}, {@code Character}, or {@code Number}
     * into a primitive array.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return primitive array
     */
    public static Object unbox(Object[] src, int srcPos, int len) {
        if (srcPos >= src.length) {
            throw new IndexOutOfBoundsException(String.valueOf(srcPos));
        }
        Class<?> type = src[srcPos].getClass();
        return unbox(type, src, srcPos, len);
    }
    
    /**
     * Transforms an array of {@code Boolean}, {@code Character}, or {@code Number}
     * into a primitive array.
     * @param src source array
     * @return primitive array
     */
    public static Object unbox(Object[] src) {
        return unbox(src, 0, -1);
    }
    
    /**
     * Transforms any array into a primitive array.
     * @param type target type
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return primitive array
     */
    public static Object unbox(Class<?> type, Object src, int srcPos, int len) {
        switch (tId(type)) {
            case I_BOOLEAN:     return unboxBooleans(src, srcPos, len);
            case I_BYTE:        return unboxBytes(src, srcPos, len);
            case I_CHARACTER:   return unboxCharacters(src, srcPos, len);
            case I_DOUBLE:      return unboxDoubles(src, srcPos, len);
            case I_FLOAT:       return unboxFloats(src, srcPos, len);
            case I_INTEGER:     return unboxIntegers(src, srcPos, len);
            case I_LONG:        return unboxLongs(src, srcPos, len);
            case I_SHORT:       return unboxShorts(src, srcPos, len);
        }
        throw new IllegalArgumentException("No primitive/box: " + type);
    }
    
    /**
     * Transforms any array into a primitive array.
     * @param type target type
     * @param src source array
     * @return primitive array
     */
    public static Object unbox(Class<?> type, Object src) {
        return unbox(type, src, 0, -1);
    }
    
    /**
     * Transforms any array into a primitive array.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @param type target type
     * @return primitive array
     */
    public static <T> T unboxAs(Object src, int srcPos, int len, Class<T> type) {
        return (T) unbox(type, src, srcPos, len);
    }

    /**
     * Transforms any array into a primitive array.
     * @param src source array
     * @param type target type
     * @return primitive array
     */
    public static <T> T unboxAs(Object src, Class<T> type) {
        return (T) unbox(type, src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code boolean}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return boolean array
     */
    public static boolean[] unboxBooleans(Object src, int srcPos, int len) {
        return unboxBooleans(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code byte}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return byte array
     */
    public static byte[] unboxBytes(Object src, int srcPos, int len) {
        return unboxBytes(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code char}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return char array
     */
    public static char[] unboxCharacters(Object src, int srcPos, int len) {
        return unboxCharacters(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code float}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return float array
     */
    public static float[] unboxFloats(Object src, int srcPos, int len) {
        return unboxFloats(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code double}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return double array
     */
    public static double[] unboxDoubles(Object src, int srcPos, int len) {
        return unboxDoubles(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code int}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return int array
     */
    public static int[] unboxIntegers(Object src, int srcPos, int len) {
        return unboxIntegers(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code long}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return long array
     */
    public static long[] unboxLongs(Object src, int srcPos, int len) {
        return unboxLongs(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code short}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return short array
     */
    public static short[] unboxShorts(Object src, int srcPos, int len) {
        return unboxShorts(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code boolean}.
     * @param src source array
     * @return boolean array
     */
    public static boolean[] unboxBooleans(Object src) {
        return unboxBooleans(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code byte}.
     * @param src source array
     * @return byte array
     */
    public static byte[] unboxBytes(Object src) {
        return unboxBytes(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code char}.
     * @param src source array
     * @return char array
     */
    public static char[] unboxCharacters(Object src) {
        return unboxCharacters(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code float}.
     * @param src source array
     * @return float array
     */
    public static float[] unboxFloats(Object src) {
        return unboxFloats(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code double}.
     * @param src source array
     * @return double array
     */
    public static double[] unboxDoubles(Object src) {
        return unboxDoubles(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code int}.
     * @param src source array
     * @return int array
     */
    public static int[] unboxIntegers(Object src) {
        return unboxIntegers(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code long}.
     * @param src source array
     * @return long array
     */
    public static long[] unboxLongs(Object src) {
        return unboxLongs(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code short}.
     * @param src source array
     * @return short array
     */
    public static short[] unboxShorts(Object src) {
        return unboxShorts(src, 0, -1);
    }
    
    /**
     * Transforms a primitive array into an array of boxed values.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return array
     */
    public static Object[] box(Object src, int srcPos, int len) {
        switch (tId(src.getClass())) {
            case I_BOOLEAN:     return box((boolean[]) src, srcPos, len);
            case I_BYTE:        return box((byte[]) src, srcPos, len);
            case I_CHARACTER:   return box((char[]) src, srcPos, len);
            case I_DOUBLE:      return box((double[]) src, srcPos, len);
            case I_FLOAT:       return box((float[]) src, srcPos, len);
            case I_INTEGER:     return box((int[]) src, srcPos, len);
            case I_LONG:        return box((long[]) src, srcPos, len);
            case I_SHORT:       return box((short[]) src, srcPos, len);
        }
        throw new IllegalArgumentException("No primitive array: " + src);
    }
    
    /**
     * Transforms a primitive array into an array of boxed values.
     * @param src source array
     * @return array
     */
    public static Object[] box(Object src) {
        return box(src, 0, -1);
    }
    
    /**
     * Transforms a primitive array into an array of boxed values.
     * @param type target type
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return array
     */
    public static Object[] box(Class<?> type, Object src, int srcPos, int len) {
        switch (tId(type)) {
            case I_BOOLEAN:     return boxBooleans(src, srcPos, len);
            case I_BYTE:        return boxBytes(src, srcPos, len);
            case I_CHARACTER:   return boxCharacters(src, srcPos, len);
            case I_DOUBLE:      return boxDoubles(src, srcPos, len);
            case I_FLOAT:       return boxFloats(src, srcPos, len);
            case I_INTEGER:     return boxIntegers(src, srcPos, len);
            case I_LONG:        return boxLongs(src, srcPos, len);
            case I_SHORT:       return boxShorts(src, srcPos, len);
        }
        throw new IllegalArgumentException("No primitive/box: " + type);
    }
    
    /**
     * Transforms a primitive array into an array of boxed values.
     * @param type target type
     * @param src source array
     * @return array
     */
    public static Object[] box(Class<?> type, Object src) {
        return box(type, src, 0, -1);
    }
    
    /**
     * Transforms a primitive array into an array of boxed values.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @param type target type
     * @return array
     */
    public static <T> T boxAs(Object src, int srcPos, int len, Class<T> type) {
        return (T) box(type, src, srcPos, len);
    }

    /**
     * Transforms any array into an array of boxed values.
     * @param type target type
     * @param src source array
     * @return array
     */
    public static <T> T boxAs(Object src, Class<T> type) {
        return (T) box(type, src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code Boolean}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return Boolean array
     */
    public static Boolean[] boxBooleans(Object src, int srcPos, int len) {
        return boxBooleans(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code Byte}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return Byte array
     */
    public static Byte[] boxBytes(Object src, int srcPos, int len) {
        return boxBytes(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code Character}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return Character array
     */
    public static Character[] boxCharacters(Object src, int srcPos, int len) {
        return boxCharacters(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code Float}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return Float array
     */
    public static Float[] boxFloats(Object src, int srcPos, int len) {
        return boxFloats(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code Double}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return Double array
     */
    public static Double[] boxDoubles(Object src, int srcPos, int len) {
        return boxDoubles(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code Integer}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return Integer array
     */
    public static Integer[] boxIntegers(Object src, int srcPos, int len) {
        return boxIntegers(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code Long}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return Long array
     */
    public static Long[] boxLongs(Object src, int srcPos, int len) {
        return boxLongs(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code Short}.
     * @param src source array
     * @param srcPos start position
     * @param len length
     * @return Short array
     */
    public static Short[] boxShorts(Object src, int srcPos, int len) {
        return boxShorts(array(src), srcPos, len);
    }

    /**
     * Transforms any array into an array of {@code Boolean}.
     * @param src source array
     * @return Boolean array
     */
    public static Boolean[] boxBooleans(Object src) {
        return boxBooleans(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code Byte}.
     * @param src source array
     * @return Byte array
     */
    public static Byte[] boxBytes(Object src) {
        return boxBytes(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code Character}.
     * @param src source array
     * @return Character array
     */
    public static Character[] boxCharacters(Object src) {
        return boxCharacters(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code Floats}.
     * @param src source array
     * @return Floats array
     */
    public static Float[] boxFloats(Object src) {
        return boxFloats(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code Double}.
     * @param src source array
     * @return Double array
     */
    public static Double[] boxDoubles(Object src) {
        return boxDoubles(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code Integer}.
     * @param src source array
     * @return Integer array
     */
    public static Integer[] boxIntegers(Object src) {
        return boxIntegers(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code Long}.
     * @param src source array
     * @return Long array
     */
    public static Long[] boxLongs(Object src) {
        return boxLongs(src, 0, -1);
    }

    /**
     * Transforms any array into an array of {@code Short}.
     * @param src source array
     * @return Short array
     */
    public static Short[] boxShorts(Object src) {
        return boxShorts(src, 0, -1);
    }
    
    static Class<?> arrayBoxingType(Class<?> clazz) {
        int dim = 0;
        while (clazz.isArray()) {
            dim++;
            clazz = clazz.getComponentType();
        }
        Class<?> comp = boxingType(clazz);
        int[] dims = new int[dim];
        Object tmpArray = newArray(comp, dims);
        return tmpArray.getClass();
    }
    
    /**
     * Returns any multidimensional array into an array of primitives.
     * @param src source array
     * @return multidimensional array of primitives
     */
    public static Object deepUnbox(Object[] src) {
        Class<?> resultType = arrayBoxingType(src.getClass());
        return deepUnbox(resultType, src);
    }

    /**
     * Returns any multidimensional array into an array of primitives.
     * @param type target type
     * @param src source array
     * @return multidimensional array of primitives
     */
    public static Object deepUnbox(Class<?> type, final Object src) {
        Class<?> compType = type.getComponentType();
        if (compType.isArray()) {
            final Object[] src2 = (Object[]) src;
            final Object[] result = (Object[]) newArray(compType, src2.length);
            for (int i = 0; i < src2.length; i++) {
                result[i] = deepUnbox(compType, src2[i]);
            }
            return result;
        } else {
            return unbox(compType, src, 0, -1);
        }
    }
    
    /**
     * Returns any multidimensional array into an array of primitives.
     * @param src source array
     * @param type target type
     * @return multidimensional array of primitives
     */
    public static <T> T deepUnboxAs(Object src, Class<T> result) {
        return (T) deepUnbox(result, src);
    }
    
    /**
     * Returns any multidimensional array into an array of boxed values.
     * @param src source array
     * @return multidimensional array
     */
    public static Object deepBox(Object src) {
        Class<?> resultType = arrayBoxingType(src.getClass());
        return deepBox(resultType, src);
    }

    /**
     * Returns any multidimensional array into an array of boxed values.
     * @param type target type
     * @param src source array
     * @return multidimensional array
     */
    public static Object[] deepBox(Class<?> resultType, final Object src) {
        Class<?> compType = resultType.getComponentType();
        if (compType.isArray()) {
            final Object[] src2 = (Object[]) src;
            final Object[] result = (Object[]) newArray(compType, src2.length);
            for (int i = 0; i < src2.length; i++) {
                result[i] = deepBox(compType, src2[i]);
            }
            return result;
        } else {
            return box(compType, src, 0, -1);
        }
    }
    
    /**
     * Returns any multidimensional array into an array of boxed values.
     * @param src source array
     * @param type target type
     * @return multidimensional array
     */
    public static <T> T deepBoxAs(Object src, Class<T> type) {
        return (T) deepBox(type, src);
    }
    
    /**
     * Transforms any (multidimensional) array into an array of another type.
     * @param src source array
     * @param type target type
     * @return array
     */
    public static <T> T as(Object src, Class<T> type) {
        Class<?> t = type;
        while (t.isArray()) {
            t = t.getComponentType();
        }
        if (t.isPrimitive()) {
            return deepUnboxAs(src, type);
        } else {
            return deepBoxAs(src, type);
        }
    }
    
    /**
     * Creates an {@link Array}.
     * @param array source array
     * @return array
     */
    public static Array array(Object array) {
        if (array instanceof Object[]) {
            return array((Object[]) array);
        }
        switch (tId(array.getClass())) {
            case I_BOOLEAN:     return array((boolean[]) array);
            case I_BYTE:        return array((byte[]) array);
            case I_CHARACTER:   return array((char[]) array);
            case I_DOUBLE:      return array((double[]) array);
            case I_FLOAT:       return array((float[]) array);
            case I_INTEGER:     return array((int[]) array);
            case I_LONG:        return array((long[]) array);
            case I_SHORT:       return array((short[]) array);
        }
        throw new IllegalArgumentException("No array: " + array);
    }
    
    private static Object newArray(Class<?> elementType, int length) {
        return java.lang.reflect.Array.newInstance(elementType, length);
    }
    
    private static Object newArray(Class<?> elementType, int... dimensions) {
        return java.lang.reflect.Array.newInstance(elementType, dimensions);
    }
}
