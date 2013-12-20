package org.cthul.objects.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 */
public class Signatures2Test {
    
    public void no_args() {
        called("no_args");
    }
    
    public void no_args(Object o) {
        called("no_args_O");
    }
    
    @Test
    public void test_no_args() {
        test()._("no_args");
        validate().no_args();
    }
    
    public void single_arg(Object o) {
        called("single_arg_O");
    }
    
    @Test
    public void test_single_arg_O() {
        test()._("single_arg", 1);
        validate().single_arg(1);
    }
    
    @Test
    public void test_single_arg_null() {
        test()._("single_arg", NULL);
        validate().single_arg(null);
    }
    
    public void specific_arg(Object o) {
        called("specific_arg_O");
    }
    
    public void specific_arg(CharSequence o) {
        called("specific_arg_CS");
    }
    
    public void specific_arg(Appendable o) {
        called("specific_arg_A");
    }
    
    public void specific_arg(StringBuilder o) {
        called("specific_arg_SB");
    }
    
    @Test
    public void test_specific_arg_O() {
        test()._("specific_arg", new Object());
        validate().specific_arg(new Object());
    }
    
    @Test
    public void test_specific_arg_CS() {
        test()._("specific_arg", "");
        validate().specific_arg("");
    }
    
    @Test
    public void test_specific_arg_SB() {
        test()._("specific_arg", new StringBuilder());
        validate().specific_arg(new StringBuilder());
    }
    
    @Test
    public void test_specific_arg_ambiguous() {
        assert_ambiguous("specific_arg", new StringBuffer());
//        validate().specific_arg(new StringBuffer());
    }
    
    @Test
    public void test_specific_arg_null() {
        test()._("specific_arg", NULL);
        validate().specific_arg(null);
    }
    
    public void iface_arg(CharSequence o) {
        called("iface_arg_CS");
    }
    
    public void iface_arg(Appendable o) {
        called("iface_arg_A");
    }
    
    @Test
    public void test_iface_arg_null_ambiguous() {
        assert_ambiguous("iface_arg", NULL);
//        validate().iface_arg(null);
    }
    
    public void specific_args(Object o1, Object o2) {
        called("specific_args_O_O");
    }
    
    public void specific_args(Number o1, Object o2) {
        called("specific_args_N_O");
    }
    
    public void specific_args(Number o1, Number o2) {
        called("specific_args_N_N");
    }
    
    public void specific_args(Number o1, Integer o2) {
        called("specific_args_N_I");
    }
    
    public void specific_args(Long o1, Number o2) {
        called("specific_args_L_N");
    }
    
    @Test
    public void test_specific_args_O_O() {
        test()._("specific_args", new Object(), new Object());
        validate().specific_args(new Object(), new Object());
    }
    
    @Test
    public void test_specific_args_N_O() {
        test()._("specific_args", 1.0, new Object());
        validate().specific_args(1.0, new Object());
    }
    
    @Test
    public void test_specific_args_N_N() {
        test()._("specific_args", 1.0, 1.0);
        validate().specific_args(1.0, 1.0);
    }
    
    @Test
    public void test_specific_args_N_null() {
        test()._("specific_args", 1.0, NULL);
        validate().specific_args(1.0, null);
    }
    
    @Test
    public void test_specific_args_N_I() {
        test()._("specific_args", 1.0, 1);
        validate().specific_args(1.0, 1);
    }
    
    @Test
    public void test_specific_args_L_N() {
        test()._("specific_args", 1L, 1.0);
        validate().specific_args(1L, 1.0);
    }
    
    @Test
    public void test_specific_args_L_I_ambiguous() {
        assert_ambiguous("specific_args", 1L, 1);
//        validate().specific_args(1L, 1);
    }
    
    public void box2(int i, Object o) {
        called("box2_i_O");
    }
    
    public void box2(Integer i, Object o) {
        called("box2_I_O");
    }
    
    @Test
    public void test_boxing() {
        Integer i = 1;
        test()._("box2", i, new Object());
        validate().box2(i, new Object());
    }
    
    @Test
    public void test_boxing_null() {
        Integer i = null;
        test()._("box2", i, new Object());
        validate().box2(i, new Object());
    }
    
    public void varargs(Object[] args) {
        called("varargs_O[]");
    }
    
    public void varargs(Number... args) {
        called("varargs_N...");
    }
    
    @Test
    public void test_varargs_II() {
        test()._("varargs", 1, 1);
        validate().varargs(1, 1);
    }
    
    @Test
    public void test_varargs_explicit() {
        Number[] ary = {1, 1};
        test()._("varargs", (Object) ary);
        validate().varargs(ary);
    }
    
    public void box_varargs(Integer... args) {
        called("box_varargs_I...");
    }
    
    public void box_varargs(int... args) {
        called("box_varargs_i...");
    }
    
    @Test
    public void test_box_varargs() {
        Integer i = 1;
        assert_ambiguous("box_varargs", i);
//        validate().box_varargs(i);
    }
    
    public void box_varargs2(Integer i, Object... args) {
        called("box_varargs2_I_O...");
    }
    
    public void box_varargs2(int i, Object... args) {
        called("box_varargs_i_O...");
    }
    
    @Test
    public void test_box_varargs2() {
        test()._("box_varargs2", NULL, new Object());
        validate().box_varargs2(null, new Object());
    }
    
    // -----------------------------------------------------------------
    
    protected static final Object NULL = null;
    
    protected void _(String method, Object... args) {
        try {
            Method m = Signatures.bestMethod(getClass(), method, args);
            assertThat("method", m, is(notNullValue()));
            Signatures.invoke(this, m, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void _c(String method, Class... types) {
        try {
            Object[] args = new Object[types.length];
            Method m = Signatures.bestMethod(getClass(), method, types);
            assertThat("method", m, is(notNullValue()));
            Signatures.invoke(this, m, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void assert_ambiguous(String method, Object... args) {
        try {
            Signatures.bestMethod(getClass(), method, args).invoke(this, args);
            assertThat("Expected amiguity exception", false);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (AmbiguousSignatureMatchException e) {
            // passed
        }
    }
    
    protected void assert_ambiguous_c(String method, Class... types) {
        try {
            Object[] args = new Object[types.length];
            Signatures.bestMethod(getClass(), method, types).invoke(this, args);
            assertThat("Expected amiguity exception", false);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (AmbiguousSignatureMatchException e) {
            // passed
        }
    }
    
    private String method = null;
    private boolean testMode;
    
    protected Signatures2Test test() {
        testMode = true;
        return this;
    }
    
    protected Signatures2Test validate() {
        testMode = false;
        return this;
    }
    
    protected void called(String m) {
        if (method == null) {
            method = m;
            return;
        }
        if (testMode) {
            assertThat(m, is(method));
        } else {
            assertThat(method, is(m));
        }
    }
    
    
}