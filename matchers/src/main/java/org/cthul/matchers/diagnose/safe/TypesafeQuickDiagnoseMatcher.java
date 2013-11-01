package org.cthul.matchers.diagnose.safe;

import org.cthul.matchers.diagnose.QuickDiagnosingMatcherBase;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.AtomicMismatch;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 * A typesafe matcher that matches and diagnoses in one step.
 * Only {@link #matches(java.lang.Object, org.hamcrest.Description)} has to be implemented.
 */
public abstract class TypesafeQuickDiagnoseMatcher<T> 
                extends QuickDiagnosingMatcherBase<T> {
    
    private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("matchesSafely", 2, 0);

    private final Class<?> expectedType;

    public TypesafeQuickDiagnoseMatcher(Class<?> expectedType) {
        this.expectedType = expectedType;
    }

    public TypesafeQuickDiagnoseMatcher(ReflectiveTypeFinder typeFinder) {
        this.expectedType = typeFinder.findExpectedType(getClass());
    }

    public TypesafeQuickDiagnoseMatcher() {
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
    public final boolean matches(Object item) {
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
    public final void describeMismatch(Object item, Description description) {
        if (!Typesafe.matches(item, expectedType, description)) {
            return;
        }
        describeMismatchSafely((T)item, description);
    }

    @Override
    public final <I> MatchResult<I> matchResult(I item) {
        if (!Typesafe.matches(item, expectedType)) {
            StringDescription sd = new StringDescription();
            Typesafe.matches(item, expectedType, sd);
            return new AtomicMismatch<>(item, this, sd.toString());
        }
        return (MatchResult) matchResultSafely((T) item);
    }
}
