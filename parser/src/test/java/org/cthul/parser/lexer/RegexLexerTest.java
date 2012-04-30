/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cthul.parser.lexer;

import org.cthul.parser.Lexer;
import org.cthul.parser.Context;
import org.cthul.parser.TokenStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.cthul.parser.hamcrest.TokenMatcher.*;

/**
 *
 * @author Arian Treffer
 */
public class RegexLexerTest {
    
    private Lexer regexLexer;
    
    public RegexLexerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        regexLexer = RegexLexer.FACTORY.create(
                NumberMatch.INSTANCE, 
                OperationMatch.INSTANCE, 
                WhitespaceMatch.INSTANCE);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of scan method, of class RegexLexer.
     */
    @Test
    public void testScan() throws Exception {
        final String input = "1 + 2 *3";
        TokenStream stream = regexLexer.scan(input, new Context());
        assertThat(stream.<String>next(), keyAndValue("INT", "1"));
        assertThat(stream.<String>next(), keyAndValue("+"));
        assertThat(stream.<String>next(), keyAndValue("INT", "2"));
        assertThat(stream.<String>next(), keyAndValue("*"));
        assertThat(stream.<String>next(), keyAndValue("INT", "3"));
    }
    
}
