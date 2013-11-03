package org.cthul.matchers;

import org.cthul.matchers.chain.AndChainMatcher;
import org.junit.Test;
import static org.cthul.matchers.hamcrest.HasDescription.*;
import static org.cthul.matchers.hamcrest.MatcherAccepts.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CIsTest {
    
    private CIs<Integer> is_lt_6 = CIs.is(lessThan(6));
    private CIs<Integer> has_lt_6 = CIs.has(lessThan(6));
    private CIs<Integer> not_lt_6 = CIs.not(lessThan(6));
    private CIs<Integer> is_not_lt_6 = CIs.isNot(lessThan(6));
    private CIs<Integer> between_3_and_6 = CIs.is(AndChainMatcher.all(greaterThan(3), lessThan(6)));

    @Test
    public void test_description_is() {
        assertThat(is_lt_6, description("is a value less than <6>"));
    }

    @Test
    public void test_match_description_is() {
        assertThat(is_lt_6, accepts(3, "<3> was a value less than <6>"));
    }

    @Test
    public void test_mismatch_description_is() {
        assertThat(is_lt_6, rejects(7, "<7> was greater than <6>"));
    }

    @Test
    public void test_expected_description_is() {
        assertThat(is_lt_6, expects(7, "is a value less than <6>"));
    }

    @Test
    public void test_description_has() {
        assertThat(has_lt_6, description("has a value less than <6>"));
    }

    @Test
    public void test_match_description_has() {
        assertThat(has_lt_6, accepts(3, "<3> was a value less than <6>"));
    }

    @Test
    public void test_mismatch_description_has() {
        assertThat(has_lt_6, rejects(7, "<7> was greater than <6>"));
    }

    @Test
    public void test_expected_description_has() {
        assertThat(has_lt_6, expects(7, "has a value less than <6>"));
    }

    @Test
    public void test_description_not() {
        assertThat(not_lt_6, description("not a value less than <6>"));
    }

    @Test
    public void test_match_description_not() {
        assertThat(not_lt_6, accepts(7, "<7> was greater than <6>"));
    }

    @Test
    public void test_mismatch_description_not() {
        assertThat(not_lt_6, rejects(3, "<3> was a value less than <6>"));
    }

    @Test
    public void test_expected_description_not() {
        assertThat(not_lt_6, expects(3, "not a value less than <6>"));
    }

    @Test
    public void test_description_is_not() {
        assertThat(is_not_lt_6, description("is not a value less than <6>"));
    }

    @Test
    public void test_match_description_is_not() {
        assertThat(is_not_lt_6, accepts(7, "<7> was greater than <6>"));
    }

    @Test
    public void test_mismatch_description_is_not() {
        assertThat(is_not_lt_6, rejects(3, "<3> was a value less than <6>"));
    }

    @Test
    public void test_expected_description_is_not() {
        assertThat(is_not_lt_6, expects(3, "is not a value less than <6>"));
    }
    
    @Test
    public void test_description_is_and() {
        assertThat(between_3_and_6, description("is a value greater than <3> and a value less than <6>"));
    }
}