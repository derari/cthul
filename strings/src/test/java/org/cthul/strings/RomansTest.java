package org.cthul.strings;

import net.sf.twip.*;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.junit.*;
import org.junit.runner.RunWith;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
@RunWith(TwiP.class)
public class RomansTest {
    
    public RomansTest() {
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
    
    private static Proc toRoman = Procs.invoke(Romans.class, "toRoman", int.class);
    private static Proc fromRoman = Procs.invoke(Romans.class, "fromRoman", String.class);
    private static Proc toRoman2 = Procs.invoke(Romans.class, "toRoman2", int.class);
    
    private static final Integer[] NUM_SMALL = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10
    };
    
    private static final String[] ROMAN_SMALL = {
            "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"
    };
    
    private static final Integer[] NUM_LARGE = {
            1, 4, 8, 9, 19, 38, 39, 40,
            89, 90, 399, 400, 2992, 2999
    };
    
    private static final String[] ROMAN_LARGE = {
            "I", "IV", "VIII", "IX", "XIX", "XXXVIII", "XXXIX", "XL",
            "LXXXIX", "XC", "CCCXCIX", "CD", "MMCMXCII", "MMCMXCIX"
    };
    
    private static final String[] ROMAN2_LARGE = {
            "I", "IV", "VIII", "IX", "XIX", "XXXVIII", "XXXIX", "XL",
            "LXXXIX", "XC", "CCCIC", "CD", "MMXMII", "MMIM"
    };
    
    public static final String[] SIX_VARIATIONS = {
            "VI", "IVII", "IIIIX", "IIIIII", "VXI"
    };
    
    public static final Pair[] DATA_SMALL = Pair.merge(ROMAN_SMALL, NUM_SMALL);
    
    public static final Pair[] DATA_LARGE = Pair.merge(ROMAN_LARGE, NUM_LARGE);
    
    public static final Pair[] DATA2_LARGE = Pair.merge(ROMAN2_LARGE, NUM_LARGE);
    
    @AutoTwip
    public static final Romans[] ROMANS = {
        new Romans("I", "V"),
        new Romans("I", "V", "X"),
        new Romans("I", "V", "X", "L"),
        new Romans("I", "V", "X", "L", "C"),
        new Romans("I", "V", "X", "L", "C", "D", "M"),
    };

    @Test
    public void test_roman_small(@Values("DATA_SMALL") Pair<String, Integer> p) {
        Romans r = new Romans("I", "V", "X");
        assertThat(r.toRoman(p.b), is(p.a));
    }

    @Test
    public void test_roman_small_2(@Values("DATA_SMALL") Pair<String, Integer> p) {
        Romans r = new Romans("I", "V", "X", "L");
        assertThat(r.toRoman(p.b), is(p.a));
    }
    
    @Test
    public void test_roman_small_3(@Values("DATA_SMALL") Pair<String, Integer> p) {
        Romans r = new Romans("I", "V");
        Proc pr = toRoman.call(r, p.b);
        if (p.b < 9) {
            assertThat(pr, returns(p.a));
        } else {
            assertThat(pr, raises(IllegalArgumentException.class));
        }
    }
    
    @Test
    public void test_roman_large(Romans r, @Values("DATA_LARGE") Pair<String, Integer> p) {
        Proc pr = toRoman.call(r, p.b);
        if (p.b > getMaxValue(r)) {
            assertThat(pr, raises(IllegalArgumentException.class));
        } else {
            assertThat(pr, returns(p.a));
        }
    }
    
    @Test
    public void test_max(Romans r) {
        int max = getMaxValue(r);
        assertThat(toRoman.call(r, max), returns());
        assertThat(toRoman.call(r, max+1), raises(IllegalArgumentException.class));
    }

    private int getMaxValue(Romans r) {
        int max = Romans.MaxRomanLetterValue(r.getLetters());
        switch (max) {
            case 5:     return 8;   // viii
            case 10:    return 39;  // xxxix
            case 50:    return 89;  // lxxxix
            case 100:   return 399; // cccxcix
            case 1000:  return 3999;
        }
        return -2;
    }
    
    @Test
    public void test_from_roman_small(@Values("DATA_SMALL") Pair<String, Integer> p) {
        Romans r = new Romans(new String[]{"I", "V", "X"});
        assertThat(r.fromRoman(p.a), is(p.b));
    }
    
    @Test
    public void test_from_roman_small_2(@Values("DATA_SMALL") Pair<String, Integer> p) {
        Romans r = new Romans(new String[]{"I", "V", "X", "L"});
        assertThat(r.fromRoman(p.a), is(p.b));
    }
    
    @Test
    public void test_from_roman_large(Romans r, @Values("DATA_LARGE") Pair<String, Integer> p) {
        Proc pr = fromRoman.call(r, p.a);
        if (p.b > getMaxValue(r)) {
            assertThat(pr, raises(IllegalArgumentException.class));
        } else {
            assertThat(pr, returns(p.b));
        }
    }
    
    @Test
    public void test_from_roman_variations(@Values("SIX_VARIATIONS") String s) {
        Romans r = new Romans(new String[]{"I", "V", "X"});
        assertThat(r.fromRoman(s), is(6));
    }

    @Test
    public void test_roman2_small(@Values("DATA_SMALL") Pair<String, Integer> p) {
        Romans r = new Romans(new String[]{"I", "V", "X"});
        assertThat(r.toRoman2(p.b), is(p.a));
    }

    @Test
    public void test_roman2_small_2(@Values("DATA_SMALL") Pair<String, Integer> p) {
        Romans r = new Romans(new String[]{"I", "V", "X", "L"});
        assertThat(r.toRoman2(p.b), is(p.a));
    }
    
    @Test
    public void test_roman2_small_3(@Values("DATA_SMALL") Pair<String, Integer> p) {
        Romans r = new Romans(new String[]{"I", "V"});
        Proc pr = toRoman2.call(r, p.b);
        if (p.b < 9) {
            assertThat(pr, returns(p.a));
        } else {
            assertThat(pr, raises(IllegalArgumentException.class));
        }
    }
    
    @Test
    public void test_roman2_large(Romans r, @Values("DATA2_LARGE") Pair<String, Integer> p) {
        Proc pr = toRoman2.call(r, p.b);
        if (p.b > getMaxValue(r)) {
            assertThat(pr, raises(IllegalArgumentException.class));
        } else {
            assertThat(pr, returns(p.a));
        }
    }
    
}
