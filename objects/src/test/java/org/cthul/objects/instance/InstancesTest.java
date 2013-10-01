package org.cthul.objects.instance;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.cthul.matchers.CthulMatchers.*;

/**
 *
 */
public class InstancesTest {
    
    public InstancesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    @TestInstance(impl=Impl1.class)
    public void test_impl_class() {
        Object o = newInstance("test_impl_class");
        assertThat(o, _isA(Impl1.class));
    }
    
    static class Impl1 {
        public Impl1() {
        }
    }

    @Test
    @TestInstance(impl=I.class, factory="expectInt1", args=@Arg(i=1))
    public void test_int() {
        Object o = newInstance("test_int");
        assertThat(o, is(SUCCESS));
    }
    
    @Test
    @TestInstance(impl=I.class, factory="expectEmptyBytes", args=@Arg(arrayOf=byte.class))
    public void test_empty() {
        Object o = newInstance("test_empty");
        assertThat(o, is(SUCCESS));
    }
    
    @Test
    @TestInstance(impl=I.class, factory="expectLongs23", args=@Arg(b={2, 3}, arrayOf=Long.class))
    public void test_box() {
        Object o = newInstance("test_box");
        assertThat(o, is(SUCCESS));
    }
    
    public static class I {
    
        public static Object expectInt1(int i) {
            assertThat(i, is(1));
            return SUCCESS;
        }
        
        public static Object expectEmptyBytes(byte[] b) {
            assertThat(b.length, is(0));
            return SUCCESS;
        }
        
        public static Object expectLongs23(Long[] l) {
            assertThat(l, arrayContaining(2L, 3L));
            return SUCCESS;
        }
    }
    
    private static final Object SUCCESS = new Object();
    
    
    private Object newInstance(String method) {
        return newInstance(getTI(method));
    }
    
    private Object newInstance(TestInstance ti) {
        return Instances.newInstance(ti.impl(), ti.factory(), ti.args());
    }
    
    private TestInstance getTI(String method) {
        try {
            return getClass().getMethod(method).getAnnotation(TestInstance.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }    
}