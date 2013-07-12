package org.cthul.matchers.fluent.custom;

import org.cthul.matchers.fluent.assertion.FluentAssertionTest;
import org.cthul.matchers.testold.ColorFluents;
import org.cthul.matchers.testold.ColorFluent;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author derari
 */
public class CustomFluentAssertionTest extends FluentAssertionTest {
    
    public CustomFluentAssertionTest() {
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

    @Override
    protected <T> ColorFluent<T, ?> _assertThat(T object) {
        return ColorFluents.assertThat(object);
    }

}
