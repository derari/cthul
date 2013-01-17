package org.cthul.matchers.exceptions;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;
import static org.cthul.matchers.exceptions.CausedBy.causedBy;
import static org.cthul.matchers.exceptions.IsThrowable.throwable;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 *
 * @author Arian Treffer
 */
public class IsThrowableTest {
    
    public IsThrowableTest() {
    }

    @Test
    public void test_mismatch_description() {
        final Matcher<Object> isThrowable = is(throwable());
        final Object o = "string";
        
        assertThat("Matching fails", isThrowable.matches(o), is(false));
        
        StringDescription desc = new StringDescription();
        isThrowable.describeMismatch(o, desc);
        assertThat(desc.toString(), 
                is("\"string\" is a java.lang.String"));
    }
    
    @Test
    public void test_mismatch_description1b() {
        final Matcher<Object> isThrowable = throwable(IllegalArgumentException.class);
        final Throwable t = new RuntimeException();
        
        assertThat("Matching fails", isThrowable.matches(t), is(false));
        
        StringDescription desc = new StringDescription();
        isThrowable.describeMismatch(t, desc);
        assertThat(desc.toString(), 
                is("<java.lang.RuntimeException> is a java.lang.RuntimeException"));
    }
    
    @Test
    public void test_mismatch_description2() {
        final Matcher<Object> isThrowable = throwable(causedBy("hello"));
        final Throwable t = new RuntimeException(
                            new RuntimeException("b"));
        
        assertThat("Matching fails", isThrowable.matches(t), is(false));
        
        StringDescription desc = new StringDescription();
        isThrowable.describeMismatch(t, desc);
        assertThat(desc.toString(), 
                is("cause message \"b\" did not contain /hello/"));
    }
    
    @Test
    public void test_mismatch_description3() {
        final Matcher<Object> isThrowable = throwable(causedBy(IllegalArgumentException.class));
        final Throwable t = new RuntimeException(
                            new RuntimeException("b"));
        
        assertThat("Matching fails", isThrowable.matches(t), is(false));
        
        StringDescription desc = new StringDescription();
        isThrowable.describeMismatch(t, desc);
        assertThat(desc.toString(), 
                is("cause <java.lang.RuntimeException: b> is a java.lang.RuntimeException"));
    }
}
