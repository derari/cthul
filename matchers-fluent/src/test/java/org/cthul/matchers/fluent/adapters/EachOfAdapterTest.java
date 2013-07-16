package org.cthul.matchers.fluent.adapters;

import java.util.Arrays;
import java.util.List;
import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.FluentTestBase;
import org.cthul.matchers.fluent.builder.FluentAssertBuilder;
import org.cthul.matchers.fluent.builder.FluentMatcherBuilder;
import org.cthul.matchers.fluent.values.MatchValue;
import org.cthul.matchers.fluent.values.MatchValueAdapter;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.cthul.matchers.fluent.adapters.EachOfAdapter.*;
import org.junit.Test;

/**
 *
 */
public class EachOfAdapterTest extends FluentTestBase {
        
    @Test
    public void test_simple_match() {
        test_assertThat(eachOf(1, 3, 5))._(lessThan(2));
        assertMismatch("greater than <2>");
    }
    
    @Test
    public void test_simple_match_success() {
        test_assertThat(eachOf(1, 3, 5))._(lessThan(10));
        assertSuccess();
    }
    
    @Test
    public void test_chained_match() {
        test_assertThat(eachOf(1, 3, 4))
                ._(lessThan(5))
                ._(lessThan(2));
        assertMismatch("greater than <2>");
    }
    
    @Test
    public void test_as_matcher() {
        
    }
    
    @Test
    public void test_chained_each() {
        List<Integer> list = Arrays.asList(1, 3, 5);
        test_assertThat(list)
                ._(eachInt()).is(greaterThan(0));
    }
    
    @Test
    public void test_matcher_description() {
        Matcher<?> m = test_matcher(eachInt()).is(lessThan(3));
        assertThat(m.toString(), is("each is a value less than <3>"));
    }
    
    @Test
    public void test_assert_description() {
        test_assertThat(eachOf(1, 3, 4)).is(lessThan(3));
        
        assertMismatch(
                "each of list is a value less than <3>",
                "#1 <3> was equal to <3>");
    }
    
    @Test
    public void test_assert_description_2() {
        test_assertThat(eachOf(1, 3, 4))
                .is(greaterThan(0))
                .and(lessThan(3));
        
        assertMismatch(
                "each of list a value less than <3>",
                "#1 <3> was equal to <3>");
    }
    
    @Test
    public void test_assert_description_not(){
        test_assertThat(eachOf(1, 3, 4)).isNot(greaterThan(3));
        
        assertMismatch(
                "each of list is not a value greater than <3>",
                "#2 <4> was a value greater than <3>");
    }
    
    @Test
    public void test_assert_description_chained(){
        List<Integer> list = Arrays.asList(1, 3, 5);
        test_assertThat(list)._(eachInt()).is(greaterThan(3));
        
        assertMismatch(
                "each is a value greater than <3>",
                "#0 <1> was less than <3>");
    }
    
    MatchValueAdapter<Iterable<? extends Integer>, Integer> eachInt() {
        return eachOf();
    }
    
}