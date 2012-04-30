package org.cthul.parser.grammar;

import org.cthul.parser.grammar.earley.EarleyGrammar;
import net.sf.twip.TwiP;
import net.sf.twip.Values;
import org.junit.runner.RunWith;
import org.cthul.parser.Grammar;
import org.cthul.parser.Context;
import org.cthul.parser.Lexer;
import org.cthul.parser.NoMatchException;
import org.cthul.parser.Parser;
import org.cthul.parser.TokenBuilder;
import org.cthul.parser.TokenStream;
import org.cthul.parser.annotation.AnnotationScanner;
import org.cthul.parser.lexer.StringToken;
import org.cthul.parser.lexer.TokenStreamFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Arian Treffer
 */
@RunWith(TwiP.class)
public class EarleyGrammarTest {
    
    private Grammar grammar;
    
    public EarleyGrammarTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        grammar = EarleyGrammar.FACTORY.create(
                ProductProduction.INSTANCE,
                SumProduction.INSTANCE,
                IntProduction.INSTANCE);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of parse method, of class EarleyGrammar.
     */
    @Test
    public void testParse1() throws NoMatchException {
        Context ctx = new Context();
        TokenStreamFactory f = new TokenStreamFactory("", StringToken.FACTORY, ctx, null);
        TokenBuilder b = f.getTokenBuilder();
        f.pushToken(b.newToken("INT", "1"));
        f.pushToken(b.newToken("+"));
        f.pushToken(b.newToken("INT", "2"));
        f.pushToken(b.newToken("*"));
        f.pushToken(b.newToken("INT", "3"));
        
        Integer result = (Integer) grammar.parse("term", f.getTokenStream(), ctx);
//        assertThat(result.hasNext(), is(true));
        assertThat(result, is(7));
//        assertThat(result.hasNext(), is(false));
    }
    
    public static final String[] Lists = {
            "{ }", "{ a }", "{a,a}", " { a,a, a }",
            "{a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a}"};
    
    @Test
    public void testList(@Values("Lists") String list) throws NoMatchException {
        Parser<Integer> p = Parser.newInstance(ListGrammar.class);
        
        int actual = list.replaceAll("[^a]+", "").length();
        int c = p.parse(list, "count");
        assertThat(c, is(actual));
    }
    
    @Test
    public void testList2(@Values("Lists") String list) throws NoMatchException {
        Parser<Integer> p = Parser.newInstance(ListGrammar2.class);
        
        int actual = list.replaceAll("[^a]+", "").length();
        int c = p.parse(list, "count");
        assertThat(c, is(actual));
    }
    
    @Test
    public void testList3(@Values("Lists") String list) throws NoMatchException {
        Parser<Integer> p = Parser.newInstance(ListGrammar3.class);
        
        int actual = list.replaceAll("[^a]+", "").length();
        int c = p.parse(list, "count");
        assertThat(c, is(actual));
    }
    
    @Test
    public void benchmarkLists() throws NoMatchException {
        final String input = "{a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a,a}";
        Class[] impls = {ListGrammar.class, ListGrammar2.class, ListGrammar3.class,
                         ListGrammar.class, ListGrammar2.class, ListGrammar3.class,
                         ListGrammar.class, ListGrammar2.class, ListGrammar3.class,
                         ListGrammar.class, ListGrammar2.class, ListGrammar3.class,
                         ListGrammar.class, ListGrammar2.class, ListGrammar3.class};
        for (Class<?> impl: impls) {
            AnnotationScanner as = new AnnotationScanner();
            as.add(impl);
            
            final Lexer lexer = as.createLexer();
            final Grammar g = as.createGrammar();
            final Context context = new Context();
            final TokenStream ts = lexer.scan(input, context);
            
            long t = -System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                g.parse("count", ts, context);
            }
            t += System.currentTimeMillis();
            System.out.println(impl + " " + t + "ms");
        }
    }
    
    
}
