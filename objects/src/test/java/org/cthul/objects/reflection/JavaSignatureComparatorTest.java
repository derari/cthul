package org.cthul.objects.reflection;

import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 */
public class JavaSignatureComparatorTest {
    
    @Test
    public void test_applicability_empty() {
        Class[] args = {};
        Class[] params = args;
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, false);
        assertThat(appl, is(JavaSignatureComparator.MATCH));
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
    public void test_applicability_boxing2() {
        Class[] args = {int.class, Object.class};
        Class[] params = {Integer.class, Object.class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, false);
        assertThat(appl, is(JavaSignatureComparator.MATCH_BOXING));
    }
    
    @Test
    public void test_applicability_unboxing2() {
        Class[] args = {Integer.class, Object.class};
        Class[] params = {int.class, Object.class};
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
    public void test_applicability_varArgs_direct_match() {
        Class[] args = {Number[].class};
        Class[] params = args;
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, true);
        assertThat(appl, is(JavaSignatureComparator.MATCH));
    }

    @Test
    public void test_applicability_any() {
        Class[] args = null;
        Class[] params = {int[].class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int appl = jsCmp.applicability(params, true);
        assertThat(appl, is(JavaSignatureComparator.MATCH_WILDCARD));
    }

    @Test
    public void test_specificness_simple() {
        Class[] args = {Integer.class};
        Class[] params1 = {Object.class};
        Class[] params2 = {Number.class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int spec = jsCmp.compareSpecificness(params1, false, params2, false);
        assertThat(spec, is(greaterThan(0)));
    }
    
    @Test
    public void test_specificness_ambiguous() {
        Class[] args = {Integer.class, Integer.class};
        Class[] params1 = {Object.class, Number.class};
        Class[] params2 = {Number.class, Object.class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int spec = jsCmp.compareSpecificness(params1, false, params2, false);
        assertThat(spec, is(0));
    }
    
    // note: non-varargs have better applicability, 
    // which is not considered for specificness
    
    @Test
    public void test_specificness_varArgs() {
        Class[] args = {String.class, Integer.class};
        Class[] params1 = {String.class, Object[].class};
        Class[] params2 = {String.class, Number[].class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int spec = jsCmp.compareSpecificness(params1, true, params2, true);
        assertThat(spec, is(greaterThan(0)));
    }
    
    @Test
    public void test_specificness_varArgs_unused_ambiguous() {
        Class[] args = {String.class};
        Class[] params1 = {String.class, Object[].class};
        Class[] params2 = {String.class, Number[].class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int spec = jsCmp.compareSpecificness(params1, true, params2, true);
        assertThat(spec, is(0));
    }
    
    @Test
    public void test_specificness_varArgs_useMoreFixed() {
        Class[] args = {String.class, String.class, String.class};
        Class[] params1 = {String.class, String.class, String[].class};
        Class[] params2 = {String.class, String[].class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int spec = jsCmp.compareSpecificness(params1, true, params2, true);
        assertThat(spec, is(lessThan(0)));
    }
    
    @Test
    public void test_specificness_any() {
        Class[] args = null;
        Class[] params1 = {Object.class};
        Class[] params2 = {Number.class};
        JavaSignatureComparator jsCmp = new JavaSignatureComparator(args);
        int spec = jsCmp.compareSpecificness(params1, false, params2, false);
        assertThat(spec, is(greaterThan(0)));
    }
}