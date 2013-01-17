package org.cthul.matchers.exceptions;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;
import static org.cthul.matchers.exceptions.CausedBy.causedBy;
import static org.cthul.matchers.exceptions.ExceptionMessage.messageContains;
import static org.cthul.matchers.exceptions.IsThrowable.throwable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author Arian Treffer
 */
public class CausedByTest {
    
    @Test
    public void test_syntax() {
        RuntimeException r = new RuntimeException("a", 
                             new RuntimeException("b"));
        assertThat(r, causedBy(RuntimeException.class));
        
        Object o = r;
        assertThat(o, is(throwable(causedBy(RuntimeException.class))));
    }

    @Test
    public void test_mismatch_description() {
        final Matcher<Throwable> hasCause = causedBy(messageContains("x"));
        final Throwable t = new RuntimeException("a",
                            new RuntimeException("b",
                            new RuntimeException("c")));
        
        assertThat("Matching fails", hasCause.matches(t), is(false));
        
        StringDescription desc = new StringDescription();
        hasCause.describeMismatch(t, desc);
        assertThat(desc.toString(), 
                is("cause 1 message \"b\" did not contain /x/, "
                 + "cause 2 message \"c\" did not contain /x/"));
    }

}
