package org.cthul.objects.reflection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 */
public class JavaSignatureComparatorTest {
    
    public JavaSignatureComparatorTest() {
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
    public void test_applicability_exact() {
        Class[] args = {Object.class, String.class, int.class};
        Class[] params = args;
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, false);
        assertThat(appl, is(JavaSignatureComparator.MATCH));
    }
    
    @Test
    public void test_applicability_more_generic() {
        Class[] args = {Object.class, String.class, Integer.class};
        Class[] params = {Object.class, CharSequence.class, Number.class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, false);
        assertThat(appl, is(JavaSignatureComparator.MATCH));
    }
    
    @Test
    public void test_applicability_boxing() {
        Class[] args = {int.class};
        Class[] params = {Integer.class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, false);
        assertThat(appl, is(JavaSignatureComparator.MATCH_BOXING));
    }
    
    @Test
    public void test_applicability_unboxing() {
        Class[] args = {Integer.class};
        Class[] params = {int.class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, false);
        assertThat(appl, is(JavaSignatureComparator.MATCH_BOXING));
    }
    
    
    @Test
    public void test_applicability_varArgs0() {
        Class[] args = {String.class};
        Class[] params = {String.class, String[].class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, true);
        assertThat(appl, is(JavaSignatureComparator.MATCH_VARARGS));
    }
    
    @Test
    public void test_applicability_varArgs1() {
        Class[] args = {String.class, String.class};
        Class[] params = {String.class, String[].class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, true);
        assertThat(appl, is(JavaSignatureComparator.MATCH_VARARGS));
    }
    
    @Test
    public void test_applicability_varArgs2() {
        Class[] args = {String.class, String.class, String.class};
        Class[] params = {String.class, String[].class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, true);
        assertThat(appl, is(JavaSignatureComparator.MATCH_VARARGS));
    }
    
    @Test
    public void test_applicability_varArgs_boxing() {
        Class[] args = {int.class};
        Class[] params = {Integer[].class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, true);
        assertThat(appl, is(JavaSignatureComparator.MATCH_VARARGS));
    }
    
    @Test
    public void test_applicability_varArgs_unboxing() {
        Class[] args = {Integer.class};
        Class[] params = {int[].class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, true);
        assertThat(appl, is(JavaSignatureComparator.MATCH_VARARGS));
    }

    @Test
    public void test_specifity_simple() {
        Class[] args = {Integer.class};
        Class[] params1 = {Object.class};
        Class[] params2 = {Number.class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int spec = jsCmp.compareSpecificity(params1, false, params2, false);
        assertThat(spec, is(greaterThan(0)));
    }
    
    @Test
    public void test_specifity_ambiguous() {
        Class[] args = {Integer.class, Integer.class};
        Class[] params1 = {Object.class, Number.class};
        Class[] params2 = {Number.class, Object.class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int spec = jsCmp.compareSpecificity(params1, false, params2, false);
        assertThat(spec, is(0));
    }
    
    // note: non-varargs are have better applicability, 
    // which is not considered for specifity
    
    @Test
    public void test_specifity_varArgs() {
        Class[] args = {String.class, Integer.class};
        Class[] params1 = {String.class, Object[].class};
        Class[] params2 = {String.class, Number[].class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int spec = jsCmp.compareSpecificity(params1, true, params2, true);
        assertThat(spec, is(greaterThan(0)));
    }
    
    @Test
    public void test_specifity_varArgs_unused_ambiguous() {
        Class[] args = {String.class};
        Class[] params1 = {String.class, Object[].class};
        Class[] params2 = {String.class, Number[].class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int spec = jsCmp.compareSpecificity(params1, true, params2, true);
        assertThat(spec, is(0));
    }
    
    @Test
    public void test_specifity_varArgs_useMoreFixed() {
        Class[] args = {String.class, String.class, String.class};
        Class[] params1 = {String.class, String.class, String[].class};
        Class[] params2 = {String.class, String[].class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int spec = jsCmp.compareSpecificity(params1, true, params2, true);
        assertThat(spec, is(lessThan(0)));
    }
    
    
}