package org.cthul.matchers.proc;

import org.cthul.proc.P0;
import org.junit.Test;
import org.hamcrest.Matcher;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;
import static org.cthul.matchers.proc.Raises.*;
import static org.cthul.matchers.ContainsPattern.*;

/**
 *
 * @author Arian Treffer
 */
public class RaisesTest {
    
    @Test
    public void test_error_description() {
//        Matcher<Throwable> tMatcher = mock(Matcher.class);
//        when(tMatcher.matches(anyObject())).thenReturn(true);
//        DescribeAnswer.describe(tMatcher, "throwable-matcher msg");
//        
//        Matcher<String> sMatcher = mock(Matcher.class);
//        when(sMatcher.matches(anyObject())).thenReturn(false);
//        DescribeAnswer.describe(sMatcher, "message-matcher msg");
//        DescribeAnswer.mismatch(sMatcher, "message-mismatch");
//        
//        Matcher<Throwable> matcher = throwable(tMatcher).whoseMessage(sMatcher);
//        
//        AssertionError ae = null;
//        
//        P0 throwIllegalArgument = new P0() {
//            @Override protected Object run() throws Throwable {
//                throw new IllegalArgumentException("No value!");
//            }
//        };
//        
//        try {
//            assertThat(throwIllegalArgument, raises(matcher));
//        } catch (AssertionError e) {
//            ae = e;
//        }
//        
//        assertThat("Matching failed", ae, not(nullValue()));
//        assertThat(ae.getMessage(), containsPattern("Expected"
//                + ".*throwable-matcher msg whose message message-matcher msg.*"));
//        assertThat(ae.getMessage(),
//                   containsPattern("but: message message-mismatch"));
    }

}
