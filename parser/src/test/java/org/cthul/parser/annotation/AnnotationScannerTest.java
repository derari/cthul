package org.cthul.parser.annotation;

import org.cthul.parser.Token;
import java.util.List;
import org.cthul.parser.grammar.earley.Production;
import org.cthul.parser.lexer.Match;
import org.cthul.parser.NoMatchException;
import org.cthul.parser.Parser;
import org.cthul.parser.TokenBuilder;
import org.cthul.parser.TokenStream;
import org.cthul.parser.lexer.IntegerToken;
import org.cthul.parser.lexer.StringToken;
import org.cthul.parser.lexer.TokenStreamFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.cthul.parser.hamcrest.TokenMatcher.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class AnnotationScannerTest {
    
    private AnnotationScanner instance;
    
    public AnnotationScannerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        instance = new AnnotationScanner();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testScanMatch() throws NoMatchException {
        instance.add(MathLexer.class);
        List<Match> matches = instance.getMatches();
        Match intMatch = null;
        for (Match m: matches) {
            if (m.getPattern().equals("[\\d]+")) intMatch = m;
        }
        assertThat(intMatch, is(notNullValue()));
        TokenStreamFactory f = new TokenStreamFactory("12 + 34", StringToken.FACTORY, null);
        f.prepareNext(); f.setUpNext(2);
        Token<?> token = intMatch.invoke(f.getTokenBuilder(), null);
        assertThat(token, is(instanceOf(IntegerToken.class)));
        IntegerToken intToken = (IntegerToken) token;
        assertThat(intToken, keyAndValue("INT", 12));
    }
    
    @Test
    public void testScanProduction() throws NoMatchException {
        instance.add(MathGrammar.class);
        List<Production> productions = instance.getProductions();
        Production plusProd = null;
        for (Production p: productions) {
            if (p.getLeft().equals("term") && p.getRightSize() == 3 &&
                    p.getRight(1).equals("+")) {
                plusProd = p;
            }
        }
        assertThat(plusProd, is(notNullValue()));
        TokenStreamFactory f = new TokenStreamFactory("12+34", StringToken.FACTORY, null);
        
        TokenBuilder tb = f.getTokenBuilder();
        f.prepareNext(); f.setUpNext(2);
        f.pushToken(tb.newToken(IntegerToken.FACTORY));
        f.prepareNext(); f.setUpNext(1);
        f.pushToken(tb.newToken());
        f.prepareNext(); f.setUpNext(2);
        f.pushToken(tb.newToken(IntegerToken.FACTORY));
        TokenStream ts = f.getTokenStream();
        
        Object result = plusProd.invoke(null, ts.next(), ts.next(), ts.next()); 
        assertThat(result, is(instanceOf(Integer.class)));
        Integer intResult = (Integer) result;
        assertThat(intResult, is(12+34));
    }
    
    /**
     * Test of addAll method, of class AnnotationScanner.
     */
    //@Test
    public void testParse() throws NoMatchException {
        AnnotationScanner a = new AnnotationScanner();
        a.addAll(MathLexer.class, MathGrammar.class);
        Parser<Integer> parser = a.createParser();
        Integer result = parser.parse("1 + 2 * 3", "term");
        assertThat(result, is(7));
    }
    
    @Test
    public void testEmptyProduction() {
        AnnotationScanner a = new AnnotationScanner();
        a.add(EmptyProduction.class);
        Production p = a.getProductions().get(0);
        assertThat(p.getRightSize(), is(0));
    }
}
