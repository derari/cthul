package org.cthul.parser.earleyx.factory;

import java.util.List;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;
import org.cthul.parser.earleyx.EarleyXGrammar;
import org.cthul.parser.earleyx.EarleyXItParser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.cthul.parser.util.Tools.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class EXRulesTest {
    
    public EXRulesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createListRule method, of class EXRules.
     */
    @Test
    public void testCreateListRule() {
        EarleyXGrammarFactory gf = new EarleyXGrammarFactory();
        gf.createToken("1", "1");
        gf.createToken(",", ",");
        EXRules.createListRule(gf, "list", 0, strings("1"), ints(0), strings(","), ints(0), true);
        EarleyXGrammar grammar = gf.create();
        EarleyXItParser parser = new EarleyXItParser(Context.forString("1,1,1"), grammar);
        Match m = parser.parse("list").iterator().next();
        List<String> list = (List) m.eval();
        assertThat(list, contains("1", "1", "1"));
    }

}
