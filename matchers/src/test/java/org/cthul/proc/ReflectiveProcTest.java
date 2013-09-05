package org.cthul.proc;

import org.junit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class ReflectiveProcTest {
    
    private static int counter;
    
    public ReflectiveProcTest() {
    }
    
    @Before
    public void setUp() {
        counter = 0;
    }
    
    @After
    public void tearDown() {
    }
    
    public static void count(int i) {
        counter += i;
    }
    
    // To test the results of the proc, it is recommended to use the
    // proc matchers Returns and Raises. They are not used here to
    // seperate the units for better testing.
    
    @Test
    public void test_callClass() {
        Proc proc = Procs.invoke(ReflectiveProcTest.class, "count", int.class);
        assertThat(proc.call(7).hasResult(), is(true));
        assertThat(counter, is(7));
    }
    
    @Test
    public void test_findWithBoxedType() {
        Proc proc = Procs.invoke(ReflectiveProcTest.class, "count", Integer.class);
        assertThat(proc.call(7).hasResult(), is(true));
        assertThat(counter, is(7));
    }
    
    @Test
    public void test_callClass_AnyParam() {
        Proc proc = Procs.invoke(ReflectiveProcTest.class, "count", ReflectiveProc.ANY_PARAMETERS);
        assertThat(proc.call(9).hasResult(), is(true));
        assertThat(counter, is(9));
    }
    
    @Test
    public void test_callInstance() {
        Proc proc = Procs.invoke(new Inc(3), "inc", ReflectiveProc.NO_PARAMETERS);
        assertThat(proc.hasResult(), is(true));
        assertThat(counter, is(3));
    }
    
    @Test
    public void test_callInstance_AnyParam() {
        Proc proc = Procs.invoke(new Inc(4), "inc", ReflectiveProc.ANY_PARAMETERS);
        assertThat(proc.hasResult(), is(true));
        assertThat(counter, is(4));
    }
    
    @Test
    public void test_implicitClass_invoke() {
        Proc proc = Procs.invoke("count");
        assertThat(proc.call(11).hasResult(), is(true));
        assertThat(counter, is(11));
    }
    
    @Test
    public void test_unboundInstance_invoke() {
        Proc proc = Procs.invoke(Inc.class, "inc");
        assertThat(proc.call(new Inc(3)).hasResult(), is(true));
        assertThat(counter, is(3));
        assertThat(proc.call(new Inc(4)).hasResult(), is(true));
        assertThat(counter, is(7));
    }
    
    @Test
    public void test_implicitClass_withArg_invoke() {
        Proc proc = Procs.invokeWith("count", 3);
        assertThat(proc.hasResult(), is(true));
        assertThat(counter, is(3));
    }
    
    @Test
    public void test_varArgs_1() throws Exception {
        Proc sum = Procs.invoke(new VarArgs(), "sum").call(1, 2, 3);
        assertThat(sum.getException(), is(nullValue()));
        assertThat(sum.getResult(), is((Object) 6));
    }
    
    @Test
    public void test_varArgs_2() throws Exception {
        Proc sum = Procs.invoke(new VarArgs(), "sum").call(1);
        assertThat(sum.getException(), is(nullValue()));
        assertThat(sum.getResult(), is((Object) 1));
    }
    
    @Test
    public void test_varArgs_3() throws Exception {
        Proc sum = Procs.invoke(new VarArgs(), "sum").call();
        assertThat(sum.getException(), is(nullValue()));
        assertThat(sum.getResult(), is((Object) 0));
    }

    private static class Inc {
        private final int value;
        public Inc(int value) {
            this.value = value;
        }
        public void inc() {
            count(value);
        }
    }
    
    
    private static class VarArgs {    
        
        public int sum(int... values) {
            int s = 0;
            for (int i: values) {
                s += i;
            }
            return s;
        }
        
    }
    
}
