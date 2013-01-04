package org.cthul.parser.earleyx;

import java.util.Iterator;
import java.util.Random;
import org.cthul.parser.api.Context;
import org.cthul.parser.earleyx.api.*;
import org.junit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author derari
 */
public abstract class EarleyXParserTestBase {
    
    public static final TestMatchEval e = TestMatchEval.INSTANCE;
    
    public EarleyXParserTestBase() {
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

    protected abstract EarleyXParserBase parser(Context c, EarleyXGrammar g);
    
    @Test
    public void test_basic() {
        EarleyXGrammar grammar = new EarleyXGrammar();
        grammar.add(new EXStringTokenRule("A", 0, "a"));
        grammar.add(new EXTestProduction("A", 0, "A", "A"));
        grammar.add(new EXTestProduction("doublea", 0, "A", "A"));
        EarleyXParserBase parser = parser(Context.forString("aa"), grammar);
        EXMatch match = parser.parse("doublea").iterator().next();
        assertThat((String) match.eval(), is("doublea(a,a)"));
    }
    
    @Test
    public void test_look_ahead() {
        EarleyXGrammar grammar = new EarleyXGrammar();
        grammar.add(new EXStringTokenRule("A", 0, "a"));
        grammar.add(new EXStringTokenRule("B", 0, "b"));
        grammar.add(new EXTestLookAheadProduction("AB_la", 0, "A", "B"));
        grammar.add(new EXTestProduction("AB", 0, "AB_la", "A", "B"));
        EarleyXParserBase parser = parser(Context.forString("ab"), grammar);
        EXMatch match = parser.parse("AB").iterator().next();
        assertThat((String) match.eval(), is("AB(AB_la(a,b),a,b)"));
    }
    
    @Test
    public void test_anti_match() {
        EarleyXGrammar grammar = new EarleyXGrammar();
        grammar.add(new EXStringTokenRule("A", 0, "a"));
        grammar.add(new EXStringTokenRule("B", 0, "a"));
        grammar.add(new EXStringTokenRule("B", 0, "b"));
        grammar.add(new EXAntiMatchRule(e, "noAA", 0, new String[]{"A", "A"}, new int[2]));
        grammar.add(new EXTestProduction("A?", 0, "A", "B"));
        grammar.add(new EXTestProduction("AB", 0, "noAA", "A?"));
        EarleyXParserBase parser = parser(Context.forString("aa"), grammar);
        
        EXMatch match = parser.parse("A?").iterator().next();
        assertThat((String) match.eval(), is("A?(a,a)"));
        
        assertThat(parser.parse("AB").iterator().hasNext(), is(false));
        
        parser = parser(Context.forString("ab"), grammar);
        match = parser.parse("AB").iterator().next();
        assertThat((String) match.eval(), is("AB(noAA(),A?(a,b))"));
    }
    
    @Test
    public void test_priority() {
        EarleyXGrammar grammar = new EarleyXGrammar();
        grammar.add(new EXStringTokenRule("N", 0, "0"));
        grammar.add(new EXStringTokenRule("+", 0, "+"));
        grammar.add(new EXStringTokenRule("*", 0, "*"));
        grammar.add(new EXProduction(e, "T", 5, new String[]{"N"}, new int[1]));
        grammar.add(new EXProduction(e, "T", 3, new String[]{"T", "*", "T"}, new int[]{4,0,3}));
        grammar.add(new EXProduction(e, "T", 1, new String[]{"T", "+", "T"}, new int[]{2,0,1}));
        
        Iterator<EXMatch> result = parser(Context.forString("0+0*0"), grammar).parse("T").iterator();
        assertThat((String) result.next().eval(), is("T(T(0),+,T(T(0),*,T(0)))"));
        assertThat(result.hasNext(), is(false));
        
        result = parser(Context.forString("0*0+0"), grammar).parse("T").iterator();
        assertThat((String) result.next().eval(), is("T(T(T(0),*,T(0)),+,T(0))"));
        assertThat(result.hasNext(), is(false));
    }
    
    @Test
    public void test_priority_2() {
        EarleyXGrammar grammar = new EarleyXGrammar();
        grammar.add(new EXStringTokenRule("N", 0, "0"));
        grammar.add(new EXStringTokenRule("+", 0, "+"));
        grammar.add(new EXStringTokenRule("*", 0, "*"));
        grammar.add(new EXStringTokenRule("(", 0, "("));
        grammar.add(new EXStringTokenRule(")", 0, ")"));
        grammar.add(new EXProduction(e, "T", 5, new String[]{"(", "T", ")"}, new int[3]));
        grammar.add(new EXProduction(e, "T", 5, new String[]{"N"}, new int[1]));
        grammar.add(new EXProduction(e, "T", 3, new String[]{"T", "*", "T"}, new int[]{4,0,3}));
        grammar.add(new EXProduction(e, "T", 1, new String[]{"T", "+", "T"}, new int[]{2,0,1}));
        
        Iterator<EXMatch> result = parser(Context.forString("(0+0)*0"), grammar).parse("T").iterator();
        assertThat((String) result.next().eval(), is("T(T((,T(T(0),+,T(0)),)),*,T(0))"));
        assertThat(result.hasNext(), is(false));
        
        result = parser(Context.forString("0*(0+0)"), grammar).parse("T").iterator();
        assertThat((String) result.next().eval(), is("T(T(0),*,T((,T(T(0),+,T(0)),)))"));
        assertThat(result.hasNext(), is(false));
    }
    
    @Test
    public void test_performance() {
        for (int i = 0; i < 10; i++) {
            parser(Context.forString(longString), tGrammar).parse("T").iterator().next();
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            parser(Context.forString(longString), tGrammar).parse("T").iterator().next();
        }
        long end = System.currentTimeMillis();
        System.out.println("\n----------------");
        System.out.println((end - start) + " ms");
        System.out.println("----------------\n");
    }
    
    private static final String longString;
    private static final EarleyXGrammar tGrammar;
    
    static {
        EarleyXGrammar grammar = new EarleyXGrammar();
        grammar.add(new EXStringTokenRule("N", 0, "0"));
        grammar.add(new EXStringTokenRule("+", 0, "+"));
        grammar.add(new EXStringTokenRule("*", 0, "*"));
        grammar.add(new EXStringTokenRule("(", 0, "("));
        grammar.add(new EXStringTokenRule(")", 0, ")"));
        grammar.add(new EXProduction(e, "T", 5, new String[]{"(", "T", ")"}, new int[3]));
        grammar.add(new EXProduction(e, "T", 5, new String[]{"N"}, new int[1]));
        grammar.add(new EXProduction(e, "T", 3, new String[]{"T", "*", "T"}, new int[]{4,0,3}));
        grammar.add(new EXProduction(e, "T", 1, new String[]{"T", "+", "T"}, new int[]{2,0,1}));
        tGrammar = grammar;
        
        Random rnd = new Random(1337);
        final int maxLen = 1001;
        int len = 0;
        int nesting = 0;
        StringBuilder sb = new StringBuilder(maxLen+3);
        while (len + nesting + 1 < maxLen) {
            while (rnd.nextInt(6) == 0) {
                sb.append('(');
                nesting++;
            }
            sb.append('0');
            while (nesting > 0 && rnd.nextInt(5) == 0) {
                sb.append(')');
                nesting--;
            }
            sb.append(rnd.nextBoolean() ? '*' : '+');
            len = sb.length();
        }
        sb.append('0');
        while (nesting > 0) {
            sb.append(')');
            nesting--;
        }
        longString = sb.toString();
    }
    
    private class EXTestProduction extends EXProduction {

        public EXTestProduction(String symbol, int priority, String... symbols) {
            super(e, symbol, priority, symbols, new int[symbols.length]);
        }
        
    }
    
    private class EXTestLookAheadProduction extends EXProduction {

        public EXTestLookAheadProduction(String symbol, int priority, String... symbols) {
            super(e, symbol, priority, symbols, new int[symbols.length]);
        }

        @Override
        public EXMatch match(Context context, int start, int end, EXMatch[] right) {
            return super.match(context, start, start, right);
        }
        
    }
    
}
