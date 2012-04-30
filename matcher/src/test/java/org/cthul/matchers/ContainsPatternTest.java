package org.cthul.matchers;

import org.junit.Test;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.cthul.matchers.ContainsPattern.*;

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
        assertThat(desc.toString(), is("\"No value!\" could not be matched"));
    }

}
