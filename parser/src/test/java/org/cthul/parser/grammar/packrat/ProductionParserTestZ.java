package org.cthul.parser.grammar.packrat;

import java.util.List;
import net.sf.twip.TwiP;
import org.junit.runner.RunWith;
import net.sf.twip.Values;
import org.cthul.parser.grammar.packrat.PRProduction.Part;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

@RunWith(TwiP.class)
public class ProductionParserTestZ {
    
    public ProductionParserTestZ() {
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

    public static final String[] ONE_STEP = { " a", "'a'" };
    
    @Test
    public void one_step(@Values("ONE_STEP") String s) {
        Part[] parts = ProductionParser.parse(s);
        assertThat(parts.length, is(1));
        assertThat(parts[0].key, is("a"));
    }
    
    public static final String[] ONE_STEP_WS = { " a", "a ", "  a\n" };
    
    @Test
    public void one_step_ws(@Values("ONE_STEP_WS") String s) {
        Part[] parts = ProductionParser.parse(s);
        assertThat(parts.length, is(1));
        assertThat(parts[0].key, is("a"));
    }
    
    public static final String[] TWO_STEPS = { " a b", "a    b", "  a\nb" };
    
    @Test
    public void two_steps(@Values("TWO_STEPS") String s) {
        Part[] parts = ProductionParser.parse(s);
        assertThat(parts.length, is(2));
        assertThat(parts[0].key, is("a"));
        assertThat(parts[1].key, is("b"));
    }
    
    public static final String[][] ALT_STRINGS = {
        {" a b / c", "a b/c"},
    };
    
    // {strings, nexts, alts}
    public static final int[][][] ALT_DATA = { 
        {{ 1, -1, -1},
         { 2, -1, -1}},
    };
    
    @Test
    public void alts(@Values("ALT_STRINGS") String[] strings,
                     @Values("ALT_DATA") int[][] data) {
        int[] nexts = data[0];
        int[] alts = data[1];
        for (int s = 0; s < strings.length; s++) {
            Part[] parts = ProductionParser.parse(strings[s]);
            assertThat(parts.length, is(nexts.length));
            for (int i = 0; i < nexts.length; i++) {
                assertThat(parts[i].next, is(nexts[i] < 0 ? null : parts[nexts[i]]));
                assertThat(parts[i].alt, is(alts[i] < 0 ? null : parts[alts[i]]));
            }
        }
        
        A a = null; B b = null; C c = null;
        List<? extends B> leb = null;
        List<? super B> lsb = null;
        assertThat(leb.get(0), is(a));
    }
    
    private class A {}
    private class B extends A {}
    private class C extends B {}
    
}
