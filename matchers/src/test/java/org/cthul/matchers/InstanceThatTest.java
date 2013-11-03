package org.cthul.matchers;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.cthul.matchers.hamcrest.HasDescription.*;
import static org.cthul.matchers.hamcrest.MatcherAccepts.*;
import static org.cthul.matchers.InstanceThat.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
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
    public void test_instanceThat() {
        Matcher<Object> int_gt_1 = instanceThat(Integer.class, greaterThan(1));
        assertThat(int_gt_1, accepts(2));
        assertThat(int_gt_1, rejects(0));
        assertThat(int_gt_1, rejects("2"));
        assertThat(int_gt_1, rejects(2.0));
    }
    
    @Test
    public void test_description() {
        Matcher<Object> int_gt_1 = instanceThat(Integer.class, greaterThan(1));
        assertThat(int_gt_1, description("an instance of java.lang.Integer and a value greater than <1>"));
    }
    
    @Test
    public void test_mismatch_instance() {
        Matcher<Object> int_gt_1 = instanceThat(Integer.class, greaterThan(1));
        assertThat(int_gt_1, rejects(2.0, "<2.0> is a java.lang.Double"));
    }
    
    @Test
    public void test_mismatch_value() {
        Matcher<Object> int_gt_1 = instanceThat(Integer.class, greaterThan(1));
        assertThat(int_gt_1, rejects(0, "<0> was less than <1>"));
    }
}
