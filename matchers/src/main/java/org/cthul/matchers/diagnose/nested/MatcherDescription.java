package org.cthul.matchers.diagnose.nested;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Overrides the description of a matcher.
 */
public class MatcherDescription<T> extends NestedMatcher<T> {

    private final Matcher<T> matcher;
    private final String description;

    public MatcherDescription(Matcher<T> matcher, String description) {
        this.matcher = matcher;
        this.description = description;
    }

    @Override
    public int getDescriptionPrecedence() {
        return Nested.precedenceOf(matcher);
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object o) {
        return matcher.matches(o);
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item, Description mismatch) {
        return quickMatch(matcher, item, mismatch);
    }

    /** {@inheritDoc} */
    @Override
    public void describeTo(Description d) {
        Nested.describeTo(false, matcher, d, description);
    }

    /** {@inheritDoc} */
    @Override
    public void describeMismatch(Object item, Description description) {
        matcher.describeMismatch(item, description);
    }
    
}
