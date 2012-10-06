package org.cthul.strings;

import org.junit.Test;
import static org.cthul.strings.RegEx.quote;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


/**
 *
 * @author Arian Treffer
 */
public class RegExTest {

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
        assertThat(quote("a.."), is("a\\.\\."));
    }
    
    @Test
    public void test_block_quote() {
        assertThat(quote("foo..bar..baz"), is("foo\\Q..bar..\\Ebaz"));
        assertThat(quote("foo..bar.."), is("foo\\Q..bar..\\E"));
        assertThat(quote("foo..bar..bazbazbaz"), is("foo\\Q..bar..\\Ebazbazbaz"));
    }
    
    @Test
    public void test_single_quote_lookahead() {
        assertThat(quote(".ba.."), is("\\Q.ba..\\E"));
        assertThat(quote(".bar.."), is("\\.bar\\.\\."));
        assertThat(quote("..bar."), is("\\Q..bar.\\E"));
    }
    
    @Test
    public void test_slash_E() {
        assertThat(quote("...\\E..."), is("\\Q...\\E\\\\E\\Q...\\E"));
        assertThat(quote("\\E\\E\\E\\E"), is("\\\\E\\\\E\\\\E\\\\E"));
    }
    
    @Test
    public void test_unquote_length() {
        assertThat(quote("..foobar."), is("\\Q..foobar.\\E"));
        assertThat(quote("..foobarz."), is("\\.\\.foobarz\\."));
    }
    
}
