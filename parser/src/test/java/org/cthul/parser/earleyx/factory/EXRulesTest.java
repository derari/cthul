package org.cthul.parser.earleyx.factory;

import java.util.List;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.XMatch;
import org.cthul.parser.earleyx.EarleyXGrammar;
import org.cthul.parser.earleyx.EarleyXItParser;
import org.junit.*;
import static org.cthul.parser.util.Tools.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

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
        XMatch m = parser.parse("list").iterator().next();
        List<String> list = (List) m.eval();
        assertThat(list, contains("1", "1", "1"));
    }

}
