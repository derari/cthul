package org.cthul.matchers.fluent.assertion;

import java.util.List;
import java.util.ArrayList;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.cthul.matchers.fluent.CoreFluents.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author derari
 */
public class EachOfStrategyTest {
    
    public EachOfStrategyTest() {
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

    /**
     * Test of validate method, of class EachOfStrategy.
     */
    @Test
    public void testValidate() {
        List<String> strings = new ArrayList<>();
        assertThat(eachOf(strings)).is(equalToIgnoringCase("world"));
        
        strings.add("world");
        strings.add("World");
        
        assertThat(eachOf(strings)).is(equalToIgnoringCase("world"));
    }
}
