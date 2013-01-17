package org.cthul.proc;

import org.junit.*;
import static org.cthul.matchers.proc.Returns.returns;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Arian Treffer
 */
public class CurryProcTest {
    
    public CurryProcTest() {
    }

    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    private Proc combine = new PN(5){
        {name("combine");}
        @Override
        protected Object runN(Object[] args) throws Throwable {
            return combine((Integer) args[0], 
                        (Integer) args[1],
                        (Integer) args[2],
                        (Integer) args[3],
                        (Integer) args[4]);
        }
    };
    
    public int combine(int a, int b, int c, int d, int e) {
        return a * 10000 +
               b *  1000 +
               c *   100 +
               d *    10 +
               e;
    }
    
    @Test
    public void test_basic() {
        assertThat(combine.call(1,2,3,4,5), returns(12345));
    }
    
    @Test
    public void test_curry1() {
        Proc c1 = combine.curry(9, 8);
        assertThat(c1.call(1, 2, 3), returns(98123));
    }
    
    @Test
    public void test_curry2() {
        Proc c1 = combine.curry(9).curry(8);
        assertThat(c1.call(1, 2, 3), returns(98123));
    }
    
    @Test
    public void test_curryAt1() {
        Proc c1 = combine.curryAt(1, 9, 8);
        assertThat(c1.call(1, 2, 3), returns(19823));
    }
    
    @Test
    public void test_curryAt2() {
        Proc c1 = combine.curryAt(1, 9).curryAt(2, 8);
        assertThat(c1.call(1, 2, 3), returns(19283));
    }

}
