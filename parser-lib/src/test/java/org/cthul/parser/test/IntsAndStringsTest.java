package org.cthul.parser.test;

import org.cthul.parser.NoMatchException;
import org.cthul.parser.ParserBuilder;
import org.cthul.parser.SimpleParser;
import org.cthul.parser.annotation.scan.AnnotationScanner;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.grammar.Grammar;
import org.cthul.parser.lexer.Lexer;
import org.cthul.parser.token.Token;
import org.cthul.parser.token.TokenStream;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.cthul.parser.test.TokenMatcher.*;

/**
 *
 * @author Arian Treffer
 */
public class IntsAndStringsTest {
    
    private static Matcher<Object> is(Object o) {
        return Matchers.is(o);
    }
    
    Lexer<TokenStream<Token<String>>> lexer;
    Grammar<TokenStream<Token<String>>> grammar;
    SimpleParser<Object> parser;
    
    @Before
    public void setUp() {
        ParserBuilder pb = createParserBuilder();
        AnnotationScanner.apply(pb, IntsAndStrings.class);
        lexer = pb.createLexer();
        grammar = pb.createGrammar();
        parser = SimpleParser.a2sFactory().create(lexer, grammar);
    }
    
    protected ParserBuilder<?,?> createParserBuilder() {
        return SimpleParser.buildEarleyWithLexer();
    }
    
    @Test
    public void testGroup() {
        Token<String> t = scan("\"asdasd\"").get(0);
        assertThat(t.eval(), is("asdasd"));
    }
    
    @Test
    public void testImplicitTokenKey() {
        Token<String> t = scan("(").get(0);
        Assert.assertThat(t, matchesKey("("));
    }
    
    @Test
    public void testAutoTokenKey() {
        Token<String> t = scan("\"asdasd\"").get(0);
        Assert.assertThat(t, matchesKey("STRING"));
    }
    
    @Test
    public void testExplicitTokenKey() {
        Token<String> t = scan("17").get(0);
        Assert.assertThat(t, matchesKey("INT"));
    }
    
    @Test
    public void testConfiguredTokenKey() {
        Token<String> t = scan("0x1f").get(0);
        Assert.assertThat(t, matchesKey("INT"));
    }
    
    @Test
    public void testTokenRedirect() {
        Integer i = parse("17", "int");
        Assert.assertThat(i, is(17));
    }
    
    @Test
    public void testUniqueParseTreeWithPriority() {
        Integer i = parse("1 + 2 + 3", "int");
        Assert.assertThat(i, is(6));
    }
    
    @Test
    public void testPriority() {
        Integer i = parse("1 * 2 + 3 * 4 + 5", "int");
        Assert.assertThat(i, is(19));
    }
    
    @Test
    public void testMorePriority() {
        Integer i = parse("1 * (2 + 3) * -4 + 5", "int");
        Assert.assertThat(i, is(-15));
    }
    
    @Test
    public void testArg() {
        Integer i = parse("( 22 )", "int");
        Assert.assertThat(i, is(22));
    }
    
    @Test
    public void testSymbol() {
        String s = parse("2 * \"ab\" * 3 ", "string");
        Assert.assertThat(s, is("abababababab"));
    }
    
    @Test
    public void test_unexpected_token() {
        try {
            parse("22+2+\"asd\"", "int");
            Assert.fail("Expected failure");
        } catch (NoMatchException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private TokenStream<Token<String>> scan(String input) {
        Context<StringInput> ctx = Context.forString(input);
        return lexer.scan(ctx);
    }
    
    private <T> T parse(String input, String start) {
        Object result = parser.parse(input, start);
//        Object result = it.next();
//        Assert.assertThat("has only one result", it.hasNext(), is(false));
        return (T) result;
    }
    
}
