package org.cthul.matchers.fluent.adapters;

import java.util.Arrays;
import java.util.List;
import org.cthul.matchers.fluent.FluentTestBase;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.cthul.matchers.fluent.adapters.AnyOfAdapter.*;
import org.junit.Test;

/**
 *
 */
public class AnyOfAdapterTest extends FluentTestBase {
        
    @Test
    public void test_simple_match() {
        test_assertThat(anyOf(1, 3, 5))._(lessThan(1));
        assertMismatch("greater than <1>");
    }
    
    @Test
    public void test_simple_match_success() {
        test_assertThat(anyOf(1, 3, 5))._(greaterThan(3));
        assertSuccess();
    }
    
    @Test
    public void test_chained_match() {
        test_assertThat(anyOf(1, 3, 4))
                ._(lessThan(5))
                ._(lessThan(1));
        assertMismatch("greater than <1>");
    }
    
    @Test
    public void test_as_matcher() {
        
    }
    
    @Test
    public void test_chained_any() {
        List<Integer> list = Arrays.asList(1, 3, 5);
        test_assertThat(list)
                ._(anyInt()).is(greaterThan(3));
    }
    
    @Test
    public void test_matcher_description() {
        Matcher<?> m = test_matcher(anyInt()).is(lessThan(3));
        assertThat(m.toString(), is("any is a value less than <3>"));
    }
    
    @Test
    public void test_assert_description() {
        test_assertThat(anyOf(1, 3, 4)).is(lessThan(1));
        
        assertMismatch(
                "any of list is a value less than <1>",
                "#0 <1> was equal to <1>, #1 <3> was greater than <1>, and #2 <4> was greater than <1>");
    }
    
    @Test
    public void test_assert_description_chained() {
        test_assertThat(anyOf(1, 4, 3))
                .is(greaterThan(1))
                .and(greaterThan(3))
                .and(greaterThan(4));
        
        assertMismatch(
                "any of list is a value greater than <1>, and a value greater than <4>",
                "#0 <1> was equal to <1>, #1 <4> was equal to <4>, and #2 <3> was less than <4>");
    }
    
    @Test
    public void test_assert_description_not(){
        test_assertThat(anyOf(1, 3, 4)).isNot(greaterThan(0));
        
        assertMismatch(
                "any of list is not a value greater than <0>",
                "#0 <1> was a value greater than <0>, #1 <3> was a value greater than <0>, and #2 <4> was a value greater than <0>");
    }
    
    MatchValueAdapter<Iterable<? extends Integer>, Integer> anyInt() {
        return anyOf();
    }
    
}