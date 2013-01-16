package org.cthul.matchers.diagnose;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 * Combines multiple matchers.
 * When creating a (mismatch) description, {@link #getPrecedence()} is
 * used to determine if parantheses should be inserted to resolve ambiguities.
 * 
 * @author Arian Treffer
 * @param <T> 
 * @see Matcher
 */
public abstract class NestedMatcher<T> 
                extends QuickDiagnosingMatcherBase<T>
                implements PrecedencedSelfDescribing {

    /**
     * If {@code o} is a {@link PrecedencedSelfDescribing},
     * calls {@link PrecedencedSelfDescribing#getPrecedence()},
     * otherwise returns {@link PrecedencedSelfDescribing#P_ATOMIC}.
     * @param o
     * @return precedence value
     */
    protected int precedenceOf(Object o) {
        if (o instanceof PrecedencedSelfDescribing) {
            return ((PrecedencedSelfDescribing) o).getPrecedence();
        } else {
            return PrecedencedSelfDescribing.P_ATOMIC;
        }
    }
    
    /**
     * Appends description of {@code s} to {@code d},
     * enclosed in parantheses if necessary.
     * @param d
     * @param s 
     */
    protected void nestedDescribe(Description d, SelfDescribing s) {
        boolean paren = precedenceOf(s) < getPrecedence();
        if (paren) d.appendText("(");
        s.describeTo(d);
        if (paren) d.appendText(")");
    }
    
    /**
     * Appends mismatch description of {@code m} to {@code d},
     * enclosed in parantheses if necessary.
     * @param d
     * @param m
     * @param item 
     */
    protected void nestedDescribeMismatch(Description d, Matcher<?> m, Object item) {
        boolean paren = precedenceOf(m) < getPrecedence();
        if (paren) d.appendText("(");
        m.describeMismatch(item, d);
        if (paren) d.appendText(")");
    }
    
    /**
     * Invokes {@link #quickMatch(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description)}
     * for {@code m}, 
     * enclosed in parantheses if necessary.
     * @param matcher
     * @param item
     * @param mismatch
     * @return 
     */
    protected boolean nestedQuickMatch(Matcher<?> matcher, Object item, Description mismatch) {
        boolean paren = precedenceOf(matcher) < getPrecedence();
        if (paren) {
            return quickMatch(matcher, item, mismatch, "($1)");
        } else {
            return quickMatch(matcher, item, mismatch);
        }
    }
    
    /**
     * Invokes {@link #quickMatch(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description, java.lang.String)}
     * for {@code m}, 
     * enclosed in parantheses if necessary.
     * @param matcher
     * @param item
     * @param mismatch
     * @param message
     * @return 
     */
    protected boolean nestedQuickMatch(Matcher<?> matcher, Object item, Description mismatch, String message) {
        boolean paren = precedenceOf(matcher) < getPrecedence();
        if (paren) message = "(" + message + ")";
        return quickMatch(matcher, item, mismatch, message);
    }
    
}
