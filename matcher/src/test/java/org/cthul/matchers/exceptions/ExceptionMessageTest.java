package org.cthul.matchers.exceptions;

import org.junit.Test;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.cthul.matchers.exceptions.ExceptionMessage.*;

/**
 *
 * @author Arian Treffer
 */
public class ExceptionMessageTest {

    @Test
    public void test_mismatch_description_regex() {
        final Matcher<Throwable> hasValidMessage = messageContains("no value");
        final Throwable t = new RuntimeException("No value!");
        
        assertThat("Matching fails", hasValidMessage.matches(t), is(false));
        
        StringDescription desc = new StringDescription();
        hasValidMessage.describeMismatch(t, desc);
        assertThat(desc.toString(), is("message \"No value!\" did not contain /no value/"));
    }
    
    @Test
    public void test_mismatch_description_is() {
        final Matcher<Throwable> hasValidMessage = message(is("no value"));
        final Throwable t = new RuntimeException("No value!");
        
        assertThat("Matching fails", hasValidMessage.matches(t), is(false));
        
        StringDescription desc = new StringDescription();
        hasValidMessage.describeMismatch(t, desc);
        assertThat(desc.toString(), is("message was \"No value!\""));
    }
    
    @Test
    public void test_mismatch_description_messageIs() {
        final Matcher<Throwable> hasValidMessage = messageIs("no value");
        final Throwable t = new RuntimeException("No value!");
        
        assertThat("Matching fails", hasValidMessage.matches(t), is(false));
        
        StringDescription desc = new StringDescription();
        hasValidMessage.describeMismatch(t, desc);
        assertThat(desc.toString(), is("message was \"No value!\""));
    }

}
