package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentMatcher;
import org.hamcrest.Matcher;
import org.cthul.matchers.testold.ColorFluent;
import org.cthul.matchers.testold.ColorMatchers;
import org.cthul.matchers.testold.MColorFluents;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author derari
 */
public class FluentBuilderTest {
    
    public FluentBuilderTest() {
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
    
    protected <Item> ColorFluent.Matcher<Item> _is() {
        return MColorFluents.is();
    }
    
    protected <Item> ColorFluent.Matcher<String> matcher() {
        return (ColorFluent.Matcher) _is();
    }

    /**
     * Test of modify method, of class FluentBuilder.
     */
    @Test
    public void testModify() {
        assertThat(null, nullValue());
    }
    
    @Test
    public void test_as() {
        Matcher<String> m = matcher().as("really red");
        Description d = new StringDescription();
        m.describeTo(d);
        assertThat(d.toString(), equalTo("really red"));
    }
    
    @Test
    public void test_is() {
        Matcher<String> m = matcher().is(ColorMatchers.red());
        assertThat("dark red", is(m));
        assertThat("dark green", not(m));
        
    }
    
    @Test
    public void test_getMatcher() {
        FluentMatcher<String> fm = matcher().is(ColorMatchers.red());
        Matcher<String> m = fm.getMatcher();
        assertThat(m, not(equalTo((Object) fm)));
    }
            
}
