package org.cthul.matchers.diagnose.safe;

import org.cthul.matchers.diagnose.nested.NestedMatcher;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.AtomicMismatch;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 * Combines multiple matchers.
 * When creating a (mismatch) description, {@link #getPrecedence()} is
 * used to determine if parantheses should be inserted to resolve ambiguities.
 * 
 * @param <T> 
 */
public abstract class TypesafeNestedMatcher<T> 
                extends NestedMatcher<T> {
    
    private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("matchesSafely", 2, 0);

    private final Class<?> expectedType;

    public TypesafeNestedMatcher(Class<?> expectedType) {
        this.expectedType = expectedType;
    }

    public TypesafeNestedMatcher(ReflectiveTypeFinder typeFinder) {
        this.expectedType = typeFinder.findExpectedType(getClass());
    }

    public TypesafeNestedMatcher() {
        this(TYPE_FINDER);
    }
    
    protected boolean matchesSafely(T item) {
        return matchesSafely(item, Description.NONE);
    }

    protected void describeMismatchSafely(T item, Description mismatch) {
        matchesSafely(item, mismatch);
    }
    
    protected abstract boolean matchesSafely(T item, Description mismatch);

    protected <I extends T> MatchResult<I> matchResultSafely(I item) {
        return super.matchResult(item);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean matches(Object item) {
        return Typesafe.matches(item, expectedType)
                && matchesSafely((T) item);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final boolean matches(Object item, Description description) {
        if (!Typesafe.matches(item, expectedType, description)) {
            return false;
        }
        return matchesSafely((T) item, description);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void describeMismatch(Object item, Description description) {
        if (!Typesafe.matches(item, expectedType, description)) {
            return;
        }
        describeMismatchSafely((T) item, description);
    }
    
    @Override
    public <I> MatchResult<I> matchResult(I item) {
        if (!Typesafe.matches(item, expectedType)) {
            StringDescription sd = new StringDescription();
            Typesafe.matches(item, expectedType, sd);
            return new AtomicMismatch<>(item, this, sd.toString());
        }
        return (MatchResult) matchResultSafely((T) item);
    }
}
