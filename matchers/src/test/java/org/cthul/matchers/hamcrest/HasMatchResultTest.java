package org.cthul.matchers.hamcrest;

import static org.cthul.matchers.hamcrest.HasDescription.description;
import org.hamcrest.Matcher;
import org.junit.Test;
import static org.cthul.matchers.hamcrest.HasMatchResult.*;
import static org.cthul.matchers.hamcrest.MatcherAccepts.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.SelfDescribing;

/**
 *
 */
public class HasMatchResultTest {
    
    public HasMatchResultTest() {
    }
    
    private HasMatchResult<Boolean> isPositive = matchResult(true, TestResultMatcher.Instance);
    private HasMatchResult<Boolean> isNegative = matchResult(false, TestResultMatcher.Instance);
    
    @Test
    public void test_matchResult() {
        assertThat(TestMatcher.Instance, isPositive);
    }

    @Test
    public void test_matchResult_description() {
        String theDescription = "a matcher matching <true> with result " + TestResultMatcher.Description;
        assertThat(isPositive, description(theDescription));
    }
    
    @Test
    public void test_matchResult_match() {
        String theMatch = "match result of <true> " + TestResultMatcher.Match;
        assertThat(isPositive, accepts(TestMatcher.Instance, theMatch));
    }

    @Test
    public void test_matchResult_mismatch() {
        String theMismatch = "match result of <false> " + TestResultMatcher.Mismatch;
        assertThat(isNegative, rejects(TestMatcher.Instance, theMismatch));
    }

    @Test
    public void test_matchResult_expect() {
        String theExpected = "a matcher matching <false> with result " + TestResultMatcher.Expected;
        assertThat(isNegative, expects(TestMatcher.Instance, theExpected));
    }
}