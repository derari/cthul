package org.cthul.matchers.fluent.values;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Represents an object of which an aspect can be matched.
 *
 * @param <T> type of matchable aspect
 */
public interface MatchingObject<T> {
    
    /**
     * Returns new {@link MatchValues}
     * @return new MatchValues
     */
    MatchValues<T> values();
    
    /**
     * Validates this object's {@linkplain #values() match values} against
     * the given matcher.
     * @param matcher
     * @return match result
     */
    boolean validate(Matcher<? super T> matcher);
    
    /**
     * Validates this object's {@linkplain #values() match values} against
     * the given matcher.
     * @param matcher
     * @return match result
     */
    boolean validate(Matcher<? super T> matcher, Description mismatch);
    
}
