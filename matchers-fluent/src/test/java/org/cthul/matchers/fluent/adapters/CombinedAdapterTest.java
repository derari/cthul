package org.cthul.matchers.fluent.adapters;

import java.util.Arrays;
import java.util.List;
import org.cthul.matchers.fluent.FluentTestBase;
import static org.hamcrest.Matchers.*;
import static org.cthul.matchers.fluent.adapters.EachOfAdapter.*;
import static org.cthul.matchers.fluent.adapters.AnyOfAdapter.*;
import org.junit.Test;

/**
 *
 */
public class CombinedAdapterTest extends FluentTestBase {
    
    private <T> List<T> _(T... elements) {
        return Arrays.asList(elements);
    }
        
    @Test
    public void test_assert_description_each_each() {
        List<List<Integer>> list = _(_(1, 2), _(3, 4, 5));
        test_assertThat(eachOf(eachOf(list)))
                .is(lessThan(10))
                .and().isNot(equalTo(3));
        assertMismatch(
                "each of each of list is not <3>",
                "#1 #0 <3> was <3>");
    }
    
    @Test
    public void test_assert_description_each_any() {
        List<List<Integer>> list = _(_(1, 2), _(1, 0, 5));
        test_assertThat(eachOf(anyOf(list)))
                .is(lessThan(3))
                .and().isNot(equalTo(0))
                .and().isNot(equalTo(2));
        assertMismatch(
                "each of any of list is not <2>, and is a value less than <3>",
                "#0 #1 <2> was <2>, and #1 #2 <5> was greater than <3>");
    }
    
    @Test
    public void test_assert_description_any_each() {
        List<List<Integer>> list = _(_(1, 0), _(5, 2));
        test_assertThat(anyOf(eachOf(list)))
                .is(lessThan(3))
                .and().isNot(equalTo(0))
                .and().isNot(equalTo(2));
        assertMismatch(
                "any of each of list is a value less than <3>, and is not <2>",
                "#1 #0 <5> was greater than <3>, and #1 <2> was <2>");
    }
    
    @Test
    public void test_assert_description_any_any() {
        List<List<Integer>> list = _(_(5, 1), _(3, 4));
        test_assertThat(anyOf(anyOf(list)))
                .is(lessThan(3))
                .and().isNot(equalTo(4))
                .and().isNot(equalTo(1));
        assertMismatch(
                "any of any of list is a value less than <3>, and is not <1>",
                "#0 #0 <5> was greater than <3>, and #1 <1> was <1>, and #1 #0 <3> was equal to <3>, and #1 <4> was greater than <3>");
    }
    
}