package org.cthul.matchers;

import org.cthul.matchers.diagnose.nested.NestedResultMatcher;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public class CNot<T> extends NestedResultMatcher<T> {
    
    private final Matcher<T> matcher;

    public CNot(Matcher<T> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(Object o) {
        return !matcher.matches(o);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("not ");
        description.appendDescriptionOf(matcher);
    }

    @Override
    public int getDescriptionPrecedence() {
        return P_UNARY;
    }

    @Override
    public <I> MatchResult<I> matchResult(I item) {
        final MatchResult<I> nested = quickMatchResult(matcher, item);
        return new NestedResult<I, CNot<T>>(item, this, !nested.matched()) {
            @Override
            public void describeTo(Description description) {
                nestedDescribeTo(getDescriptionPrecedence(), nested, description);
            }
            @Override
            public void describeMatch(Description d) {
                describeTo(d);
            }
            @Override
            public void describeExpected(Description d) {
                d.appendText("not ");
                describeMismatch(d);
            }
            @Override
            public void describeMismatch(Description d) {
                describeTo(d);
            }
        };
    }
}
