package org.cthul.strings;

import net.sf.twip.TwiP;
import net.sf.twip.Values;
import org.junit.*;
import org.junit.runner.RunWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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
    
    @Test
    public void test_max_value() {
        String sMax = "CRPXNLSKVLJFHG";
        long lMax = Long.MAX_VALUE-1;
        assertThat(AlphaIndex.ToAlpha(lMax), is(sMax));
        assertThat(AlphaIndex.FromAlpha(sMax), is(lMax));
    }
    
}
