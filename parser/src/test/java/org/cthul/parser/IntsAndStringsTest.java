package org.cthul.parser;

import org.cthul.parser.annotation.AnnotationScanner;
import org.junit.*;
import static org.cthul.parser.hamcrest.TokenMatcher.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class IntsAndStringsTest {
    
    Lexer lexer;
    Grammar grammar;
    Parser<Object> parser;
    
    @Before
    public void setUp() {
        AnnotationScanner as = new AnnotationScanner();
        as.add(IntsAndStrings.class);
        lexer = as.createLexer();
        parser = as.createParser();
    }
    
    @Test
    public void testGroup() throws NoMatchException {
        Token<String> t = scan("\"asdasd\"").first();
        Assert.assertThat(t.getValue(), is("asdasd"));
    }
    
    @Test
    public void testImplicitTokenKey() throws NoMatchException {
        Token<String> t = scan("(").first();
        Assert.assertThat(t, matchesKey("("));
    }
    
    @Test
    public void testAutoTokenKey() throws NoMatchException {
        Token<String> t = scan("\"asdasd\"").first();
        Assert.assertThat(t, matchesKey("STRING"));
    }
    
    @Test
    public void testExplicitTokenKey() throws NoMatchException {
        Token<String> t = scan("17").first();
        Assert.assertThat(t, matchesKey("INT"));
    }
    
    @Test
    public void testConfiguredTokenKey() throws NoMatchException {
        Token<String> t = scan("0x1f").first();
        Assert.assertThat(t, matchesKey("INT"));
    }
    
    @Test
    public void testTokenRedirect() throws NoMatchException {
        Integer i = parse("17", "int");
        Assert.assertThat(i, is(17));
    }
    
    @Test
    public void testUniqueParseTreeWithPriority() throws NoMatchException {
        Integer i = parse("1 + 2 + 3", "int");
        Assert.assertThat(i, is(6));
    }
    
    @Test
    public void testPriority() throws NoMatchException {
        Integer i = parse("1 * 2 + 3 * 4 + 5", "int");
        Assert.assertThat(i, is(19));
    }
    
    @Test
    public void testMorePriority() throws NoMatchException {
        Integer i = parse("1 * (2 + 3) * -4 + 5", "int");
        Assert.assertThat(i, is(-15));
    }
    
    @Test
    public void testArg() throws NoMatchException {
        Integer i = parse("( 22 )", "int");
        Assert.assertThat(i, is(22));
    }
    
    @Test
    public void testSymbol() throws NoMatchException {
        String s = parse("2 * \"ab\" * 3 ", "string");
        Assert.assertThat(s, is("abababababab"));
    }
    
    @Test
    public void test_unexpected_token() {
        try {
            parse("22", "string");
            Assert.fail("Expected failure");
        } catch (NoMatchException e) {
            
        }
    }
    
    private TokenStream scan(String input) throws NoMatchException {
        Context ctx = new Context();
        return lexer.scan(input, ctx);
    }
    
    private <T> T parse(String input, String start) throws NoMatchException {
        Object result = parser.parse(input, start);
//        Object result = it.next();
//        Assert.assertThat("has only one result", it.hasNext(), is(false));
        return (T) result;
    }
    
}
