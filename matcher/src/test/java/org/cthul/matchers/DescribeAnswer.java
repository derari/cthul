/*
 * 
 */

package org.cthul.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.mockito.Mockito.*;

/**
 * A mockito answer that appends a fixed string to
 * Hamcrest descriptions that are passed as parameters.
 * 
 * @author derari
 */
public class DescribeAnswer implements Answer {
    
    public static void mismatch(Matcher stub, String mismatch) {
        doAnswer(new DescribeAnswer(mismatch))
                .when(stub).describeMismatch(any(), (Description)any());
    }
    
    public static void describe(Matcher stub, String text) {
        doAnswer(new DescribeAnswer(text))
                .when(stub).describeTo((Description)any());
    }
    
    private String text;

    public DescribeAnswer(String text) {
        this.text = text;
    }
    
    @Override 
    public Object answer(InvocationOnMock invocation) throws Throwable {
        for (Object o: invocation.getArguments()) {
            if (o instanceof Description) {
                Description d = (Description) o;
                d.appendText(text);
            }
        }
        return null;
    }

}
