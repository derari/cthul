package org.cthul.matchers.hamcrest;

import org.cthul.matchers.object.CIs;
import org.cthul.matchers.diagnose.result.MatchResult;
import static org.cthul.matchers.hamcrest.HasDescription.description;
import static org.cthul.matchers.hamcrest.MatcherAccepts.*;
import org.junit.Test;
import static org.cthul.matchers.hamcrest.IsMatchResult.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 */
public class IsMatchResultTest {
    
    public IsMatchResultTest() {
    }
    
    private IsMatchResult<Boolean> isMatch = match(TestResultMatcher.Instance);
    private IsMatchResult<Boolean> isOtherMatch = match("other reason");
    private IsMatchResult<Boolean> isMismatch = mismatch(CIs.not(TestResultMatcher.Instance));
    private IsMatchResult<Boolean> isOtherMismatch = mismatch("other reason");
    private MatchResult<Boolean> match = TestMatcher.Instance.matchResult(true);
    private MatchResult<Boolean> mismatch = TestMatcher.Instance.matchResult(false);
    
    @Test
    public void test_match() {
        assertThat(match, isMatch);
    }

    @Test
    public void test_match_description() {
        String theDescription = "match " + TestResultMatcher.Description;
        assertThat(isMatch, description(theDescription));
    }
    
    @Test
    public void test_match_match() {
        String theMatch = "match " + TestResultMatcher.Match;
        assertThat(isMatch, accepts(match, theMatch));
    }

    @Test
    public void test_match_value_mismatch() {
        String theMismatch = "was mismatch <" + mismatch + ">";
        assertThat(isMatch, rejects(mismatch, theMismatch));
        String theExpected = "match " + TestResultMatcher.Description;
        assertThat(isMatch, expects(mismatch, theExpected));
    }

    @Test
    public void test_match_message_mismatch() {
        String theMismatch = "match message was \"" + match + "\"";
        assertThat(isOtherMatch, rejects(match, theMismatch));
        String theExpected = "match message \"other reason\"";
        assertThat(isOtherMatch, expects(match, theExpected));
    }
    
    @Test
    public void test_mismatch() {
        assertThat(mismatch, isMismatch);
    }

    @Test
    public void test_mismatch_description() {
        String theDescription = "mismatch not " + TestResultMatcher.Description;
        assertThat(isMismatch, description(theDescription));
    }
    
    @Test
    public void test_mismatch_match() {
        String theMatch = "mismatch " + TestResultMatcher.Mismatch;
        assertThat(isMismatch, accepts(mismatch, theMatch));
    }

    @Test
    public void test_mismatch_value_mismatch() {
        String theMismatch = "was match <" + match + ">";
        assertThat(isMismatch, rejects(match, theMismatch));
        String theExpected = "mismatch not " + TestResultMatcher.Description;
        assertThat(isMismatch, expects(match, theExpected));
    }

    @Test
    public void test_mismatch_message_mismatch() {
        String theMismatch = "mismatch message was \"" + mismatch + "\"";
        assertThat(isOtherMismatch, rejects(mismatch, theMismatch));
        String theExpected = "mismatch message \"other reason\"";
        assertThat(isOtherMismatch, expects(mismatch, theExpected));
    }
}