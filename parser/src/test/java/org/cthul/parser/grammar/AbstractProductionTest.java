package org.cthul.parser.grammar;

import org.cthul.parser.grammar.earley.AbstractProduction;
import org.cthul.parser.Context;
import org.cthul.parser.NoMatchException;
import org.cthul.parser.Value;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Arian Treffer
 */
public class AbstractProductionTest {
    
    public AbstractProductionTest() {
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
    public void testEmptyProduction() {
        AbstractProductionImpl i = new AbstractProductionImpl("x", 1, "");
        assertThat(i.getRightSize(), is(0));
    }
    
    public static class AbstractProductionImpl extends AbstractProduction {

        public AbstractProductionImpl(String left, int priority, String right) {
            super(left, priority, right);
        }

        @Override
        public Object invoke(Context context, Value<?>... args) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
