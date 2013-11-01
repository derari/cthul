package org.cthul.matchers.hamcrest;

import org.hamcrest.Matcher;
import org.junit.Test;
import static org.cthul.matchers.hamcrest.HasDescription.*;
import static org.cthul.matchers.hamcrest.MatcherAccepts.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.SelfDescribing;

/**
 *
 */
public class HasDescriptionTest {
    
    public HasDescriptionTest() {
    }
    
    private Matcher<SelfDescribing> isDescription = description(TestMatcher.Description);
    
    @Test
    public void test_description() {
        assertThat(TestMatcher.Instance, isDescription);
    }

    @Test
    public void test_description_description() {
        String theDescription = "description \"" + TestMatcher.Description + "\"";
        assertThat(isDescription, description(theDescription));
    }

    @Test
    public void test_description_match() {
        String theMatch = "description was \"" + TestMatcher.Description + "\"";
        assertThat(isDescription, accepts(TestMatcher.Instance, theMatch));
    }

    @Test
    public void test_description_mismatch() {
        String theMismatch = "description was \"" + TestResultMatcher.Description + "\"";
        assertThat(isDescription, rejects(TestResultMatcher.Instance, theMismatch));
    }

    @Test
    public void test_description_expected() {
        String theExpected = "description \"" + TestMatcher.Description + "\"";
        assertThat(isDescription, expects(TestResultMatcher.Instance, theExpected));
    }
}