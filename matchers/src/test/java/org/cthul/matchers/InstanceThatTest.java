package org.cthul.matchers;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.cthul.matchers.InstanceThat.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Arian Treffer
 */
public class InstanceThatTest {
    
    public InstanceThatTest() {
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

    @Test
    public void test_syntax() {
        Object o = Integer.valueOf(2);
        // assertThat(o, greaterThan(1)); should not work
        assertThat(o, isInstanceThat(Integer.class, greaterThan(1)));    
    }
        
    @Test
    public void testInstanceThat() {
        Matcher<Object> m = instanceThat(Integer.class, greaterThan(1));
        assertThat(m.matches(2), is(true));
        assertThat(m.matches(0), is(false));
        assertThat(m.matches("2"), is(false));
        assertThat(m.matches(2.0), is(false));
    }
    
    @Test
    public void testDescription() {
        Matcher<Object> m = instanceThat(Integer.class, greaterThan(1));
        StringDescription d = new StringDescription();
        m.describeTo(d);
        assertThat(d.toString(), is("an instance of java.lang.Integer and a value greater than <1>"));
    }
    
    @Test
    public void testMismatchInstance() {
        Matcher<Object> m = instanceThat(Integer.class, greaterThan(1));
        StringDescription d = new StringDescription();
        m.describeMismatch(2.0, d);
        assertThat(d.toString(), is("<2.0> is a java.lang.Double"));
    }
    
    @Test
    public void testMismatchValue() {
        Matcher<Object> m = instanceThat(Integer.class, greaterThan(1));
        StringDescription d = new StringDescription();
        m.describeMismatch(0, d);
        assertThat(d.toString(), is("<0> was less than <1>"));
    }
    
}
