package org.cthul.objects.reflection;

import java.lang.reflect.Method;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 */
public class SignaturesTest {

    @Test
    public void test_bestMatch() {
        Class<?>[] args = sig(String.class, Integer.class);
        Class<?>[][] params = sigs(
                sig(String.class), 
                sig(String.class, Double.class), 
                sig(String.class, Object.class),
                sig(String.class, Number.class));
        boolean[] varArgs = {false, false, false, false};
        int i = Signatures.bestMatch(params, varArgs, args);
        assertThat(i, is(3));
    }
    
    @Test
    public void test_bestMatch_ambiguous() {
        Class<?>[] args = sig(String.class, Integer.class);
        Class<?>[][] params = sigs(
                sig(String.class), 
                sig(String.class, Object.class),
                sig(Object.class, Number.class));
        boolean[] varArgs = {false, false, false, false};
        try {
            int i = Signatures.bestMatch(params, varArgs, args);
            assertThat("Expected exception, got " + i, false);
        } catch (AmbiguousSignatureMatchException e) {
            assertThat(e.getAllSignatures(), is(params));
            assertThat(e.getAllVarArgs(),    is(varArgs));
        }
    }
    
    @Test
    public void test_candidateMatches() {
        Class<?>[] args = sig(String.class, Integer.class);
        Class<?>[][] params = sigs(
                sig(Object.class, Object.class), 
                sig(String.class, Double.class), 
                sig(String.class, Object.class),
                sig(Object.class, Number.class),
                sig(Object.class, Integer.class));
        boolean[] varArgs = {false, false, false, false, false};
        int[] indices = Signatures.candidateMatches(params, varArgs, args);
        assertThat(indices, is(new int[]{2, 4}));
    }
    
    @Test
    public void test_specific_match() {
        Class<?>[] args = sig(StringBuilder.class);
        Class<?>[][] params = sigs(
                sig(CharSequence.class),
                sig(Object.class),
                sig(Appendable.class),
                sig(StringBuilder.class));
        boolean[] varArgs = {false, false, false, false};
        int i = Signatures.bestMatch(params, varArgs, args);
        assertThat(i, is(3));
    }
    
    private static final Class<?>[] mVarArgsParams = {String.class, String[].class};
    
    public int mVarArgs(String s, String... moreStrings) {
        return moreStrings.length;
    }
    
    @Test
    public void test_fixVarArgs_0() throws Exception {
        Method mVarArgs = getClass().getMethod("mVarArgs", mVarArgsParams);
        Object[] args = {"a"};
        args = Signatures.fixVarArgs(mVarArgsParams, args);
        int n = (Integer) mVarArgs.invoke(this, args);
        assertThat(n, is(0));
    }
    
    @Test
    public void test_fixVarArgs_1() throws Exception {
        Method mVarArgs = getClass().getMethod("mVarArgs", mVarArgsParams);
        Object[] args = {"a", "b"};
        args = Signatures.fixVarArgs(mVarArgsParams, args);
        int n = (Integer) mVarArgs.invoke(this, args);
        assertThat(n, is(1));
    }
    
    @Test
    public void test_fixVarArgs_2() throws Exception {
        Method mVarArgs = getClass().getMethod("mVarArgs", mVarArgsParams);
        Object[] args = {"a", "b", "c"};
        args = Signatures.fixVarArgs(mVarArgsParams, args);
        int n = (Integer) mVarArgs.invoke(this, args);
        assertThat(n, is(2));
    }
    
    private static final Class<?>[] mVarArgsPrimParams = {int.class, int[].class};
    
    public int mVarArgsPrim(int i, int... moreInts) {
        return moreInts.length;
    }
    
    @Test
    public void test_fixVarArgs_prim_0() throws Exception {
        Method mVarArgs = getClass().getMethod("mVarArgsPrim", mVarArgsPrimParams);
        Object[] args = {1};
        args = Signatures.fixVarArgs(mVarArgsPrimParams, args);
        int n = (Integer) mVarArgs.invoke(this, args);
        assertThat(n, is(0));
    }
    
    @Test
    public void test_fixVarArgs_prim_1() throws Exception {
        Method mVarArgs = getClass().getMethod("mVarArgsPrim", mVarArgsPrimParams);
        Object[] args = {1, 2};
        args = Signatures.fixVarArgs(mVarArgsPrimParams, args);
        int n = (Integer) mVarArgs.invoke(this, args);
        assertThat(n, is(1));
    }
    
    @Test
    public void test_fixVarArgs_prim_2() throws Exception {
        Method mVarArgs = getClass().getMethod("mVarArgsPrim", mVarArgsPrimParams);
        Object[] args = {1, 2, 3};
        args = Signatures.fixVarArgs(mVarArgsPrimParams, args);
        int n = (Integer) mVarArgs.invoke(this, args);
        assertThat(n, is(2));
    }
    
    @Test
    public void test_collect_public() {
        Method[] m = Signatures.collectMethods(TestPublic.class, "m", Signatures.PUBLIC, Signatures.NONE);
        assertThat(m, arrayWithSize(2));
    }
    
    @Test
    public void test_collect_non_public() {
        Method[] m = Signatures.collectMethods(TestPublic.class, "m", Signatures.ANY, Signatures.PUBLIC);
        assertThat(m, arrayWithSize(3));
    }

    private static class TestPublic {
        private void m(byte b) {}
        void m(short s) {}
        protected void m(int i) {}
        public void m(long l) {}
        public void m(Long l) {}
    }

    @Test
    public void test_collect_static() {
        Method[] m = Signatures.collectMethods(TestStatic.class, "m", Signatures.STATIC, Signatures.NONE);
        assertThat(m, arrayWithSize(2));
    }
    
    @Test
    public void test_collect_non_static() {
        Method[] m = Signatures.collectMethods(TestStatic.class, "m", Signatures.ANY, Signatures.STATIC);
        assertThat(m, arrayWithSize(1));
    }

    private static class TestStatic {
        public void m(short s) {}
        static void m(long l) {}
        public static void m(Long l) {}
    }

    private static Class<?>[] sig(Class<?>... sig) {
        return sig;
    }
    
    private static Class<?>[][] sigs(Class<?>[]... sig) {
        return sig;
    }
    
}