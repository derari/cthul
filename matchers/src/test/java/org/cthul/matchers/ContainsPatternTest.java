package org.cthul.matchers;

import org.hamcrest.Matcher;
import org.junit.Test;
import static org.cthul.matchers.ContainsPattern.containsPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.cthul.matchers.hamcrest.MatcherAccepts.*;

/**
 *
 */
public class ContainsPatternTest {

    @Test
    public void test_mismatch_description() {
        final Matcher<CharSequence> hasPattern = containsPattern("no value");
        
        assertThat(hasPattern, rejects("No value!", "\"No value!\" did not contain /no value/"));        
    }

}
