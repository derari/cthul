package org.cthul.matchers.exceptions;

import org.junit.Test;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.cthul.matchers.exceptions.ExceptionMessage.*;
import static org.cthul.matchers.exceptions.CausedBy.*;
import static org.cthul.matchers.InstanceThat.*;

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
        assertThat(o, isInstanceThat(Throwable.class, 
                                     is(causedBy(RuntimeException.class))));
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
