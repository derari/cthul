package org.cthul.matchers.chain;

import org.hamcrest.Matcher;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public class SomeOfChainMatcherTest {
    
    public SomeOfChainMatcherTest() {
    }

    @Test
    public void test_match_2() {
        assertThat(6, SomeOfChainMatcher
                .matches(2).of(
                    notNullValue(),
                    lessThan(3),
                    greaterThan(4)));
    }
    
    @Test
    public void test_match_lt2() {
        Matcher<Integer> m = SomeOfChainMatcher
                .matches(lessThan(2)).of(
                    notNullValue(),
                    is(6),
                    lessThan(3),
                    greaterThan(4));
        assertThat(6, not(m));
    }
    
    @Test
    public void test_match_lt2_description() {
        Matcher<Integer> m = SomeOfChainMatcher
                .matches(lessThan(2)).of(
                    notNullValue(),
                    OrChainMatcher.or(is(6), is(7)),
                    lessThan(3),
                    greaterThan(4));
        assertThat(m.toString(), 
                is("a value less than <2> of: not null, (is <6> or is <7>), a value less than <3>, and a value greater than <4>"));
    }
    
}