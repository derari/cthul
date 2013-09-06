package org.cthul.matchers.diagnose.nested;

import org.cthul.matchers.diagnose.QuickResultMatcherBase;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Redirects all calls to another matcher.
 * 
 * @param <T> 
 */
public abstract class MatcherProxy<T> 
                extends QuickResultMatcherBase<T>
                implements PrecedencedMatcher<T> {

    public MatcherProxy() {
    }
    
    protected abstract Matcher<T> matcher();
    
    @Override
    public boolean matches(Object o) {
        return matcher().matches(o);
    }
    
    @Override
    public boolean matches(Object item, Description mismatch) {
        return quickMatch(matcher(), item, mismatch);
    }

    @Override
    public void describeTo(Description description) {
        matcher().describeTo(description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        matcher().describeMismatch(item, description);
    }

    @Override
    public <I> MatchResult<I> matchResult(I item) {
        return quickMatchResult(matcher(), item);
    }

    @Override
    public int getDescriptionPrecedence() {
        return Nested.precedenceOf(matcher());
    }

    @Override
    public int getMismatchPrecedence() {
        return Nested.mismatchPrecedenceOf(matcher());
    }
}
