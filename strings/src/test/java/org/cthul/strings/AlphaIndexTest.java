package org.cthul.strings;

import net.sf.twip.TwiP;
import net.sf.twip.Values;
import org.cthul.proc.Proc1;
import org.cthul.proc.Procs;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cthul.matchers.CthulMatchers.raises;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author Arian Treffer
 */
@RunWith(TwiP.class)
public class AlphaIndexTest {
    
    private static final String[] ALPHA = {
            "A", "B", "C", "Z", "AA", "AB", "AZ", "ZZ", "AAA",
    };
    
    private static final Integer[] NUM = {
            0, 1, 2, 25, 26, 27, 51, 701, 702,
    };
    
    public static Pair[] DATA = Pair.merge(ALPHA, NUM);
    
    private static Proc1<Long> ToAlpha = Procs.invoke(AlphaIndex.class, "toAlpha", 1).asProc1();
    private static Proc1<String> FromAlpha = Procs.invoke(AlphaIndex.class, "fromAlpha").asProc1();
    
    /**
     * Test of toAlpha method, of class AlphaIndex.
     */
    @Test
    public void test_ToAlpha(@Values("DATA") Pair<String, Integer> data) {
        assertThat(AlphaIndex.toAlpha(data.b), is(data.a));
    }

    /**
     * Test of fromAlpha method, of class AlphaIndex.
     */
    @Test
    public void test_FromAlpha(@Values("DATA") Pair<String, Integer> data) {
        assertThat(AlphaIndex.fromAlpha(data.a), is(data.b.longValue()));
    }
    
    @Test
    public void test_max_value() {
        String sMax = "CRPXNLSKVLJFHG";
        long lMax = Long.MAX_VALUE-1;
        assertThat(AlphaIndex.toAlpha(lMax), is(sMax));
        assertThat(AlphaIndex.fromAlpha(sMax), is(lMax));
    }
    
    @Test
    public void test_max_value_fail() {
        long lMax = Long.MAX_VALUE;
        assertThat(ToAlpha.call(lMax), raises(IllegalArgumentException.class));
        
        String sMax = "CRPXNLSKVLJFHH";
        assertThat(FromAlpha.call(sMax), raises(IllegalArgumentException.class));
    }

}
