package org.cthul.matchers;

import org.hamcrest.StringDescription;
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
        String s = StringDescription.toString(has_lt_6);
        assertThat(s, is("has a value less than <6>"));
    }

    @Test
    public void test_match_description_has() {
        String s = StringDescription.toString(has_lt_6.matchResult(3));
        assertThat(s, is("<3> was a value less than <6>"));
    }

    @Test
    public void test_mismatch_description_has() {
        String s = StringDescription.toString(has_lt_6.matchResult(7));
        assertThat(s, is("<7> was greater than <6>"));
    }

    @Test
    public void test_expected_description_has() {
        String s = StringDescription.toString(has_lt_6.matchResult(7).getMismatch().getExpectedDescription());
        assertThat(s, is("has a value less than <6>"));
    }

    @Test
    public void test_description_not() {
        String s = StringDescription.toString(not_lt_6);
        assertThat(s, is("not a value less than <6>"));
    }

    @Test
    public void test_match_description_not() {
        String s = StringDescription.toString(not_lt_6.matchResult(7));
        assertThat(s, is("<7> was greater than <6>"));
    }

    @Test
    public void test_mismatch_description_not() {
        String s = StringDescription.toString(not_lt_6.matchResult(3));
        assertThat(s, is("<3> was a value less than <6>"));
    }

    @Test
    public void test_expected_description_not() {
        String s = StringDescription.toString(not_lt_6.matchResult(3).getMismatch().getExpectedDescription());
        assertThat(s, is("not a value less than <6>"));
    }

    @Test
    public void test_description_is_not() {
        String s = StringDescription.toString(is_not_lt_6);
        assertThat(s, is("is not a value less than <6>"));
    }

    @Test
    public void test_match_description_is_not() {
        String s = StringDescription.toString(is_not_lt_6.matchResult(7));
        assertThat(s, is("<7> was greater than <6>"));
    }

    @Test
    public void test_mismatch_description_is_not() {
        String s = StringDescription.toString(is_not_lt_6.matchResult(3));
        assertThat(s, is("<3> was a value less than <6>"));
    }

    @Test
    public void test_expected_description_is_not() {
        String s = StringDescription.toString(is_not_lt_6.matchResult(3).getMismatch().getExpectedDescription());
        assertThat(s, is("is not a value less than <6>"));
    }
}