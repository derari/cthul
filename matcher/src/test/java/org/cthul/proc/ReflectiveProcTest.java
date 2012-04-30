package org.cthul.proc;

import org.cthul.proc.Proc;
import org.cthul.proc.ReflectiveProc;
import org.cthul.proc.Procs;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

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
    
    private static void count(int i) {
        counter += i;
    }

    @Test
    public void callClass() {
        Proc proc = Procs.invoke(ReflectiveProcTest.class, "count", int.class);
        assertThat(proc.call(7).hasResult(), is(true));
        assertThat(counter, is(7));
    }
    
    @Test
    public void findWithBoxedType() {
        Proc proc = Procs.invoke(ReflectiveProcTest.class, "count", Integer.class);
        assertThat(proc.call(7).hasResult(), is(true));
        assertThat(counter, is(7));
    }
    
    @Test
    public void callClass_AnyParam() {
        Proc proc = Procs.invoke(ReflectiveProcTest.class, "count", ReflectiveProc.ANY_PARAMETERS);
        assertThat(proc.call(9).hasResult(), is(true));
        assertThat(counter, is(9));
    }
    
    @Test
    public void callInstance() {
        Proc proc = Procs.invoke(new Inc(3), "inc", ReflectiveProc.NO_PARAMETERS);
        assertThat(proc.hasResult(), is(true));
        assertThat(counter, is(3));
    }
    
    @Test
    public void callInstance_AnyParam() {
        Proc proc = Procs.invoke(new Inc(4), "inc", ReflectiveProc.ANY_PARAMETERS);
        assertThat(proc.hasResult(), is(true));
        assertThat(counter, is(4));
    }
    
    @Test
    public void implicitClass_invoke() {
        Proc proc = Procs.invoke("count");
        assertThat(proc.call(11).hasResult(), is(true));
        assertThat(counter, is(11));
    }
    
    @Test
    public void unboundInstance_invoke() {
        Proc proc = Procs.invoke(Inc.class, "inc");
        assertThat(proc.call(new Inc(3)).hasResult(), is(true));
        assertThat(counter, is(3));
        assertThat(proc.call(new Inc(4)).hasResult(), is(true));
        assertThat(counter, is(7));
    }
    
    @Test
    public void implicitClass_withArg_invoke() {
        Proc proc = Procs.invokeWith("count", 3);
        assertThat(proc.hasResult(), is(true));
        assertThat(counter, is(3));
    }
    
    @Test
    public void test_varArgs_1() throws Exception {
        Proc sum = Procs.invoke(new T(), "sum").call(1, 2, 3);
        assertThat(sum.getException(), is(nullValue()));
        assertThat(sum.getResult(), is((Object) 6));
    }
    
    @Test
    public void test_varArgs_2() throws Exception {
        Proc sum = Procs.invoke(new T(), "sum").call(1);
        assertThat(sum.getException(), is(nullValue()));
        assertThat(sum.getResult(), is((Object) 1));
    }
    
    @Test
    public void test_varArgs_3() throws Exception {
        Proc sum = Procs.invoke(new T(), "sum").call();
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
    
    
    private static class T {    
        
        public int sum(int... values) {
            int s = 0;
            for (int i: values) {
                s += i;
            }
            return s;
        }
        
    }
    
}
