package org.cthul.matchers.fluent.strategy;

import org.hamcrest.Matcher;

public interface MatchingObject<T> {
    
    /**
     * As a result of {@link #validate(org.hamcrest.Matcher)},
     * indicates that the assertion was successful.
     */
    public static final Object VALID = new Object();

    /**
     * Evaluate the matcher.
     * @param matcher
     * @return Invalid value or {@link #VALID}
     */
    Object validate(Matcher<? super T> matcher);

}
