package org.cthul.matchers.fluent.assertion;

import org.cthul.matchers.fluent.strategy.MatchingObject;
import org.cthul.matchers.testold.ColorFluent;
import org.cthul.matchers.testold.MColorFluents;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.cthul.matchers.testold.ColorMatchers.*;

/**
 *
 * @author Arian Treffer
 */
public class FluentAssertionTest {
    
    public FluentAssertionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    protected <T> ColorFluent<T, ?> _assertThat(T object) {
        return MColorFluents.assertThat(object);
    }
    
    protected <T> ColorFluent<T, ?> _assertThat(MatchingObject<T> object) {
        return MColorFluents.assertThat(object);
    }
    
    @Test
    public void valid_expected_message() {
        boolean error = false;
        try {
            _assertThat("world").is(equalTo("hello"));
        } catch (AssertionError e) {
            error = true;
            assertThat(e.getMessage(), startsWith("Expected: \"hello\""));
        }
        assertThat("Error expected", error);
    }
    
    @Test
    public void valid_but_message() {
        boolean error = false;
        try {
            _assertThat("world").is(equalTo("hello"));
        } catch (AssertionError e) {
            error = true;
            assertThat(e.getMessage(), endsWith("but: was \"world\""));
        }
        assertThat("Error expected", error);
    }

    @Test
    public void test_as() {
        boolean error = false;
        try {
            _assertThat("world").as("greeting").is(equalTo("hello"));
        } catch (AssertionError e) {
            error = true;
            assertThat(e.getMessage(), startsWith("greeting"));
        }
        assertThat("Error expected", error);
    }
    
    @Test
    public void test_is_success() {
        _assertThat("world").is(equalTo("world"));
    }
    
    @Test
    public void test_is_fail() {
        boolean error = false;
        try {
            _assertThat("world").is(equalTo("hello"));
        } catch (AssertionError e) {
            error = true;
        }
        assertThat("Error expected", error);
    }
    
    @Test
    public void test_not_success() {
        _assertThat("world").not(equalTo("hello"));
    }
    
    @Test
    public void test_not_fail() {
        boolean error = false;
        try {
            _assertThat("world").not(equalTo("world"));
        } catch (AssertionError e) {
            error = true;
            assertThat(e.getMessage(), containsString("not \"world\""));
        }
        assertThat("Error expected", error);
    }
    
    @Test
    public void test_red() {
        _assertThat("red").is().red();
    }
    
    @Test
    public void chaining_success() {
        _assertThat("red-green").is(red()).and(green()).andNot(blue());
        _assertThat("red-green").is().red().and().green().andNot().blue();
    }
    
    @Test
    public void chaining_fail_1() {
        boolean error = false;
        try {
            _assertThat("red").is().red().and().green().andNot().blue();
        } catch (AssertionError e) {
            error = true;
            assertThat(e.getMessage(), equalTo(
                    "Expected: a string containing \"green\"\n" +
                    "     but: was \"red\""));
        }
        assertThat("Error expected", error);
    }
    
    @Test
    public void chaining_fail_2() {
        boolean error = false;
        try {
            _assertThat("red-green-blue").is().red().andNot().blue();
        } catch (AssertionError e) {
            error = true;
            assertThat(e.getMessage(), equalTo(
                    "Expected: not a string containing \"blue\"\n" +
                    "     but: was \"red-green-blue\""));
        }
        assertThat("Error expected", error);
    }
    
    @Test
    public void not_null_fail() {
        boolean error = false;
        try {
            Object obj = null;
            _assertThat(obj).isNot(nullValue());
        } catch (AssertionError e) {
            error = true;
        }
        assertThat("Error expected", error);
    }

}
