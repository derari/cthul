package org.cthul.strings;

import net.sf.twip.TwiP;
import net.sf.twip.Values;
import org.junit.*;
import org.junit.runner.RunWith;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
@RunWith(TwiP.class)
public class AlphaIndexTest {
    
    public AlphaIndexTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    private static final String[] ALPHA = {
            "A", "B", "C", "Z", "AA", "AB", "AZ", "ZZ", "AAA",
    };
    
    private static final Integer[] NUM = {
            0, 1, 2, 25, 26, 27, 51, 701, 702,
    };
    
    public static final Pair[] DATA = Pair.merge(ALPHA, NUM);
    
//    {
//            p("A", 0), p("B", 1), p("C", 2), p("Z", 25),
//            p("AA", 26), p("AB", 27), p("AZ", 51),
//            p("ZZ", 701), p("AAA", 702)
//    };
    
    /**
     * Test of ToAlpha method, of class AlphaIndex.
     */
    @Test
    public void test_ToAlpha(@Values("DATA") Pair<String, Integer> data) {
        assertThat(AlphaIndex.ToAlpha(data.b), is(data.a));
    }

    /**
     * Test of FromAlpha method, of class AlphaIndex.
     */
    @Test
    public void test_FromAlpha(@Values("DATA") Pair<String, Integer> data) {
        assertThat(AlphaIndex.FromAlpha(data.a), is(data.b.longValue()));
    }
    
    private static Object[] convertPrimitiveArray(Object primArray) {
        Class itemType = primArray.getClass().getComponentType();
        if (itemType == byte.class) {
            final byte[] bytes = (byte[]) primArray;
            final Byte[] result = new Byte[bytes.length];
            for (int i = 0; i < bytes.length; i++)
                result[i] = bytes[i];
            return result;
        }
        if (itemType == char.class) {
            final char[] chars = (char[]) primArray;
            final Character[] result = new Character[chars.length];
            for (int i = 0; i < chars.length; i++)
                result[i] = chars[i];
            return result;
        }
        if (itemType == short.class) {
            final short[] shorts = (short[]) primArray;
            final Short[] result = new Short[shorts.length];
            for (int i = 0; i < shorts.length; i++)
                result[i] = shorts[i];
            return result;
        }
        if (itemType == int.class) {
            final int[] ints = (int[]) primArray;
            final Integer[] result = new Integer[ints.length];
            for (int i = 0; i < ints.length; i++)
                result[i] = ints[i];
            return result;
        }
        if (itemType == long.class) {
            final long[] longs = (long[]) primArray;
            final Long[] result = new Long[longs.length];
            for (int i = 0; i < longs.length; i++)
                result[i] = longs[i];
            return result;
        }
        if (itemType == float.class) {
            final float[] floats = (float[]) primArray;
            final Float[] result = new Float[floats.length];
            for (int i = 0; i < floats.length; i++)
                result[i] = floats[i];
            return result;
        }
        if (itemType == double.class) {
            final double[] doubles = (double[]) primArray;
            final Double[] result = new Double[doubles.length];
            for (int i = 0; i < doubles.length; i++)
                result[i] = doubles[i];
            return result;
        }
        throw new IllegalArgumentException("Expected primitive, got " + itemType);
    }
    
}
