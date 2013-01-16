package org.cthul.strings;

import net.sf.twip.TwiP;
import net.sf.twip.Values;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Arian Treffer
 */
@RunWith(TwiP.class)
public class RomansTest {
        
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
            19, 38, 39, 40,
            89, 90, 399, 400, 2992, 2999
    };
    
    private static final String[] ROMAN_LARGE = {
            "XIX", "XXXVIII", "XXXIX", "XL",
            "LXXXIX", "XC", "CCCXCIX", "CD", "MMCMXCII", "MMCMXCIX"
    };
    
    private static final String[] ROMAN2_LARGE = {
            "XIX", "XXXVIII", "XXXIX", "XL",
            "LXXXIX", "XC", "CCCIC", "CD", "MMXMII", "MMIM"
    };
    
    public static final String[] SIX_VARIATIONS = {
            "VI", "IVII", "IIIIX", "IIIIII", "VXI"
    };
    
    public static final Pair[] DATA_SMALL = Pair.merge(ROMAN_SMALL, NUM_SMALL);
    
    public static final Pair[] DATA_LARGE = Pair.merge(ROMAN_LARGE, NUM_LARGE);
    
    public static final Pair[] DATA2_LARGE = Pair.merge(ROMAN2_LARGE, NUM_LARGE);
    
    public static final Romans[] ROMANS_SMALL = {
        new Romans("I", "V"),
        new Romans("I", "V", "X"),
        new Romans("I", "V", "X", "L"),
    };

    public static final Romans[] ROMANS_LARGE = {
        new Romans("I", "V", "X", "L"),
        new Romans("I", "V", "X", "L", "C", "D", "M"),
    };

    @Test
    public void test_toRoman_small(@Values("ROMANS_SMALL") Romans r, @Values("DATA_SMALL") Pair<String, Integer> p) {
        test_toRoman(r, p.b, p.a);
    }
    
    @Test
    public void test_toRoman_large(@Values("ROMANS_LARGE") Romans r, @Values("DATA_LARGE") Pair<String, Integer> p) {
        test_toRoman(r, p.b, p.a);
    }
    
    public void test_toRoman(Romans r, int n, String s) {
        Proc call = toRoman.call(r, n);
        if (n > getMaxValue(r)) {
            assertThat(call, raises(IllegalArgumentException.class));
        } else {
            assertThat(call, returns(s));
        }
    }
    
    @Test
    public void test_toRoman2_small(@Values("ROMANS_SMALL") Romans r, @Values("DATA_SMALL") Pair<String, Integer> p) {
        test_toRoman2(r, p.b, p.a);
    }
    
    @Test
    public void test_toRoman2_large(@Values("ROMANS_LARGE") Romans r, @Values("DATA2_LARGE") Pair<String, Integer> p) {
        test_toRoman2(r, p.b, p.a);
    }
    
    public void test_toRoman2(Romans r, int n, String s) {
        Proc call = toRoman2.call(r, n);
        if (n > getMaxValue(r)) {
            assertThat(call, raises(IllegalArgumentException.class));
        } else {
            assertThat(call, returns(s));
        }
    }
    
    @Test
    public void test_fromRoman_small(@Values("ROMANS_SMALL") Romans r, @Values("DATA_SMALL") Pair<String, Integer> p) {
        test_fromRoman(r, p.a, p.b);
    }
    
    @Test
    public void test_fromRoman_large(@Values("ROMANS_LARGE") Romans r, @Values("DATA_LARGE") Pair<String, Integer> p) {
        test_fromRoman(r, p.a, p.b);
    }
    
    @Test
    public void test_fromRoman_2_large(@Values("ROMANS_LARGE") Romans r, @Values("DATA2_LARGE") Pair<String, Integer> p) {
        test_fromRoman(r, p.a, p.b);
    }
    
    @Test
    public void test_fromRoman_variations(@Values("SIX_VARIATIONS") String s) {
        Romans r = new Romans("I", "V", "X");
        test_fromRoman(r, s, 6);
    }

    public void test_fromRoman(Romans r, String s, int n) {
        Proc call = fromRoman.call(r, s);
        if (n > getMaxValue(r)) {
            assertThat(call, raises(IllegalArgumentException.class));
        } else {
            assertThat(call, returns(n));
        }
    }
    
    @Test
    public void test_max_small(@Values("ROMANS_SMALL") Romans r) {
        test_max(r);
    }
    
    @Test
    public void test_max_large(@Values("ROMANS_LARGE") Romans r) {
        test_max(r);
    }
    
    public void test_max(Romans r) {
        int max = getMaxValue(r);
        assertThat(toRoman.call(r, max), returns());
        assertThat(toRoman.call(r, max+1), raises(IllegalArgumentException.class));
    }

    private int getMaxValue(Romans r) {
        int max = Romans.maxLetterValue(r.getLetters());
        switch (max) {
            case 5:     return 8;   // viii
            case 10:    return 39;  // xxxix
            case 50:    return 89;  // lxxxix
            case 100:   return 399; // cccxcix
            case 1000:  return 3999;
        }
        return -2;
    }
    
}
