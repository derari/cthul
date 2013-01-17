package org.cthul.matchers;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;
import static org.cthul.matchers.ContainsPattern.containsPattern;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Arian Treffer
 */
public class ContainsPatternTest {

    @Test
    public void test_mismatch_description() {
        final Matcher<String> hasPattern = containsPattern("no value");
        final String string = "No value!";
        
        assertThat("Matching fails", hasPattern.matches(string), is(false));
        
        StringDescription desc = new StringDescription();
        hasPattern.describeMismatch(string, desc);
        assertThat(desc.toString(), is("\"No value!\" did not contain /no value/"));
    }

}
