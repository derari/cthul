package org.cthul.parser.grammar.earleyx;

import java.util.Iterator;
import java.util.Random;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.grammar.earleyx.algorithm.EarleyXParser;
import org.cthul.parser.grammar.earleyx.rule.*;
import org.cthul.parser.lexer.api.PlainStringInputEval;
import org.cthul.parser.lexer.lazy.LazyStringTokenMatcher;
import org.hamcrest.Matcher;
import org.junit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.cthul.matchers.CthulMatchers.*;

public abstract class EarleyXParserTestBase {
    
    protected TestRuleEval e = TestRuleEval.instance();
    
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

    protected abstract EarleyXGrammar newGrammar();
    
    protected RuleKey rk(String s, int p) {
        return new RuleKey(s, p);
    }
    
    protected PlainStringInputEval str() {
        return PlainStringInputEval.instance();
    }
    
    protected static <X> Matcher<X> isString(String s) {
        return isA(String.class).that(is(s));
    }
    
    @Test
    public void test_basic() {
        EarleyXGrammar grammar = newGrammar();
        grammar.add(new EXTestToken("A", "a"));
        grammar.add(new EXTestProduction("A", 0, "A", "A"));
        grammar.add(new EXTestProduction("doublea", 0, "A", "A"));
        EarleyXParser parser = grammar.parser(Context.forString("aa"));
        Match match = parser.parse("doublea", 0).iterator().next();
        
        assertThat(match.eval(), isString("doublea(a,a)"));
    }
    
    @Test
    public void test_look_ahead() {
        EarleyXGrammar grammar = newGrammar();
        grammar.add(new EXTestToken("A", "a"));
        grammar.add(new EXTestToken("B", "b"));
        grammar.add(new EXTestLookAhead("AB_la", 0, "A", "B"));
        grammar.add(new EXTestProduction("AB", 0, "AB_la", "A", "B"));
        EarleyXParser parser = grammar.parser(Context.forString("ab"));
        Match match = parser.parse("AB", 0).iterator().next();
        assertThat(match.eval(), isString("AB(AB_la(a,b),a,b)"));
    }
    
    @Test
    public void test_anti_match() {
        EarleyXGrammar grammar = newGrammar();
        grammar.add(new EXTestToken("A", "a"));
        grammar.add(new EXTestToken("B", "a"));
        grammar.add(new EXTestToken("B", "b"));
        grammar.add(new EXTestAntiMatch("noAA", 0, "A", "A"));
        grammar.add(new EXTestProduction("A?", 0, "A", "B"));
        grammar.add(new EXTestProduction("AB", 0, "noAA", "A?"));
        EarleyXParser parser = grammar.parser(Context.forString("aa"));
        
        Match match = parser.parse("A?", 0).iterator().next();
        assertThat(match.eval(), isString("A?(a,a)"));
        
        assertThat(parser.parse("AB", 0).iterator().hasNext(), is(false));
        
        parser = grammar.parser(Context.forString("ab"));
        match = parser.parse("AB", 0).iterator().next();
        assertThat(match.eval(), isString("AB(noAA(),A?(a,b))"));
    }
    
    @Test
    public void test_priority() {
        EarleyXGrammar grammar = newGrammar();
        grammar.add(new EXTestToken("N", "0"));
        grammar.add(new EXTestToken("+", "+"));
        grammar.add(new EXTestToken("*", "*"));
        grammar.add(new EXProduction(e, "T", 5, new String[]{"N"}, new int[1]));
        grammar.add(new EXProduction(e, "T", 3, new String[]{"T", "*", "T"}, new int[]{4,0,3}));
        grammar.add(new EXProduction(e, "T", 1, new String[]{"T", "+", "T"}, new int[]{2,0,1}));
        
        Iterator<Match> result = grammar.parser(Context.forString("0+0*0")).parse("T", 0).iterator();
        assertThat(result.next().eval(), isString("T(T(0),+,T(T(0),*,T(0)))"));
        assertThat(result.hasNext(), is(false));
        
        result = grammar.parser(Context.forString("0*0+0")).parse("T", 0).iterator();
        assertThat((String) result.next().eval(), is("T(T(T(0),*,T(0)),+,T(0))"));
        assertThat(result.hasNext(), is(false));
    }
    
    @Test
    public void test_priority_2() {
        EarleyXGrammar grammar = newGrammar();
        grammar.add(new EXTestToken("N", "0"));
        grammar.add(new EXTestToken("+", "+"));
        grammar.add(new EXTestToken("*", "*"));
        grammar.add(new EXTestToken("(", "("));
        grammar.add(new EXTestToken(")", ")"));
        grammar.add(new EXProduction(e, "T", 5, new String[]{"(", "T", ")"}, new int[3]));
        grammar.add(new EXProduction(e, "T", 5, new String[]{"N"}, new int[1]));
        grammar.add(new EXProduction(e, "T", 3, new String[]{"T", "*", "T"}, new int[]{4,0,3}));
        grammar.add(new EXProduction(e, "T", 1, new String[]{"T", "+", "T"}, new int[]{2,0,1}));
        
        Iterator<Match> result = grammar.parser(Context.forString("(0+0)*0")).parse("T", 0).iterator();
        assertThat((String) result.next().eval(), is("T(T((,T(T(0),+,T(0)),)),*,T(0))"));
        assertThat(result.hasNext(), is(false));
        
        result = grammar.parser(Context.forString("0*(0+0)")).parse("T", 0).iterator();
        assertThat((String) result.next().eval(), is("T(T(0),*,T((,T(T(0),+,T(0)),)))"));
        assertThat(result.hasNext(), is(false));
    }
    
    @Test
    public void test_performance() {
        for (int i = 0; i < 10; i++) {
            tGrammar.parser(Context.forString(longString)).parse("T", 0).iterator().next();
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            tGrammar.parser(Context.forString(longString)).parse("T", 0).iterator().next();
        }
        long end = System.currentTimeMillis();
        System.out.println("\n----------------");
        System.out.println((end - start) + " ms");
        System.out.println("----------------\n");
    }
    
    private static final String longString;
    protected final EarleyXGrammar tGrammar;
    
    {
        EarleyXGrammar grammar = newGrammar();
        grammar.add(new EXTestToken("N", "0"));
        grammar.add(new EXTestToken("+", "+"));
        grammar.add(new EXTestToken("*", "*"));
        grammar.add(new EXTestToken("(", "("));
        grammar.add(new EXTestToken(")", ")"));
        grammar.add(new EXProduction(e, "T", 5, new String[]{"(", "T", ")"}, new int[3]));
        grammar.add(new EXProduction(e, "T", 5, new String[]{"N"}, new int[1]));
        grammar.add(new EXProduction(e, "T", 3, new String[]{"T", "*", "T"}, new int[]{4,0,3}));
        grammar.add(new EXProduction(e, "T", 1, new String[]{"T", "+", "T"}, new int[]{2,0,1}));
        tGrammar = grammar;
    }
     
    static {
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
    
    protected class EXTestToken extends EXTokenRule<StringInput> {

        public EXTestToken(String key, String... tokens) {
            super(new LazyStringTokenMatcher(rk(key, 0), PlainStringInputEval.instance(), tokens));
        }
        
    }

    protected class EXTestProduction extends EXProduction {

        public EXTestProduction(String symbol, int priority, String... symbols) {
            super(e, symbol, priority, symbols, new int[symbols.length]);
        }
        
    }
    
    protected class EXTestLookAhead extends EXLookAhead {

        public EXTestLookAhead(String symbol, int priority, String... symbols) {
            super(e, symbol, priority, symbols, new int[symbols.length]);
        }
        
    }
    
    protected class EXTestAntiMatch extends EXAntiMatch {

        public EXTestAntiMatch(String symbol, int priority, String... symbols) {
            super(e, symbol, priority, symbols, new int[symbols.length]);
        }

    }
    
}
