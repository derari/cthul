package org.cthul.matchers.diagnose.nested;

import org.cthul.matchers.diagnose.QuickResultMatcherBase;
import org.hamcrest.*;

/**
 * Combines multiple matchers.
 * When creating a (mismatch) description, {@link #getPrecedence()} is
 * used to determine if parenthesis should be inserted to resolve ambiguities.
 * 
 * @param <T> 
 * @see Matcher
 */
public abstract class NestedResultMatcher<T> 
                extends QuickResultMatcherBase<T>
                implements PrecedencedSelfDescribing {
   
    /**
     * Appends description of {@code s} to {@code d},
     * enclosed in parenthesis if necessary.
     * @param d
     * @param s 
     */
    protected void nestedDescribeTo(SelfDescribing s, Description d) {
        Nested.describeTo(this, s, d);
    }
    
    /**
     * Appends description of {@code s} to {@code d},
     * enclosed in parantheses if necessary.
     * @param d
     * @param s 
     * @param message
     */
    protected void nestedDescribe(SelfDescribing s, Description d, String message) {
        Nested.describeTo(this, s, d, message);
    }
    
    /**
     * Invokes {@link #quickMatch(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description)}
     * for {@code m}, 
     * enclosed in parenthesis if necessary.
     * @param matcher
     * @param item
     * @param mismatch
     * @return 
     */
    protected boolean nestedMatch(Matcher<?> matcher, Object item, Description mismatch) {
        return Nested.matches(this, matcher, item, mismatch);
    }
    
    /**
     * Invokes {@link #quickMatch(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description, java.lang.String)}
     * for {@code m}, 
     * enclosed in parenthesis if necessary.
     * @param matcher
     * @param item
     * @param mismatch
     * @param message
     * @return 
     */
    protected boolean nestedMatch(Matcher<?> matcher, Object item, Description mismatch, String message) {
        return Nested.matches(this, matcher, item, mismatch, message);
    }
    
    protected static class NestedMatch<T, M extends Matcher<?>>
                     extends Nested.Match<T, M> {

        public NestedMatch(T value, M matcher) {
            super(value, matcher);
        }
    }

    protected static class NestedMismatch<T, M extends Matcher<?>> 
                     extends Nested.Mismatch<T, M> {

        public NestedMismatch(T value, M matcher) {
            super(value, matcher);
        }
    }

    protected static class NestedResult<T, M extends Matcher<?>> 
                     extends Nested.Result<T, M> {

        public NestedResult(T value, M matcher) {
            super(value, matcher);
        }

        public NestedResult(T value, M matcher, boolean success) {
            super(value, matcher, success);
        }
    }
}
