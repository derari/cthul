/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cthul.strings;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.cthul.strings.RegEx.quote;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 *
 * @author C5173086
 */
public class RegExTest {
    
    public RegExTest() {
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

    @Test
    public void test_single_quote_specials() {
        assertThat(quote(".()"), is("\\.\\(\\)"));
        assertThat(quote("+[]"), is("\\+\\[\\]"));
        assertThat(quote("*{}"), is("\\*\\{\\}"));
        assertThat(quote("?\\|"), is("\\?\\\\\\|"));
    }

    @Test
    public void test_block_quote_space() {
        assertThat(quote("( )"), is("\\Q( )\\E"));
    }
    
    @Test
    public void test_single_quote() {
        assertThat(quote("foo..bar"), is("foo\\.\\.bar"));
        assertThat(quote("a.b.c"), is("a\\.b\\.c"));
    }
    
    @Test
    public void test_block_quote() {
        assertThat(quote("foo..bar..baz"), is("foo\\Q..bar..\\Ebaz"));
    }
    
    @Test
    public void test_single_quote_lookahead() {
        assertThat(quote("foo.bar..baz"), is("foo\\.bar\\.\\.baz"));
        assertThat(quote("a.b.bar..baz"), is("a\\Q.b.bar..\\Ebaz"));
    }
    
    @Test
    public void test_slash_E() {
        assertThat(quote("...\\E..."), is("\\Q...\\E\\\\E\\Q...\\E"));
        assertThat(quote("\\E\\E\\E\\E"), is("\\\\E\\\\E\\\\E\\\\E"));
    }
    
}
