package org.cthul.matchers.diagnose;

import java.util.Collection;
import org.cthul.matchers.diagnose.QuickDiagnosingMatcherBase;
import org.hamcrest.*;

/**
 * Combines multiple matchers.
 * 
 * @author Arian Treffer
 * @param <T> 
 * @see Matcher
 */
public abstract class NestedMatcher<T> 
                extends QuickDiagnosingMatcherBase<T>
                implements PrecedencedSelfDescribing {

    protected int precedenceOf(Object o) {
        if (o instanceof PrecedencedSelfDescribing) {
            return ((PrecedencedSelfDescribing) o).getPrecedence();
        } else {
            return PrecedencedSelfDescribing.P_ATOMIC;
        }
    }
    
    protected void nestedDescribe(Description d, SelfDescribing s) {
        boolean paren = precedenceOf(s) < getPrecedence();
        if (paren) d.appendText("(");
        s.describeTo(d);
        if (paren) d.appendText(")");
    }
    
    protected void nestedDescribeMismatch(Description d, Matcher<?> m, Object item) {
        boolean paren = precedenceOf(m) < getPrecedence();
        if (paren) d.appendText("(");
        m.describeMismatch(item, d);
        if (paren) d.appendText(")");
    }
    
    protected boolean nestedQuickMatch(Matcher<?> matcher, Object item, Description mismatch) {
        boolean paren = precedenceOf(matcher) < getPrecedence();
        if (paren) {
            return quickMatch(matcher, item, mismatch, "($1)");
        } else {
            return quickMatch(matcher, item, mismatch);
        }
    }
    
    protected boolean nestedQuickMatch(Matcher<?> matcher, Object item, Description mismatch, String message) {
        boolean paren = precedenceOf(matcher) < getPrecedence();
        if (paren) message = "(" + message + ")";
        return quickMatch(matcher, item, mismatch, message);
    }
    
}
