package org.cthul.matchers.diagnose.safe;

import org.cthul.matchers.diagnose.QuickMatcherBase;
import org.hamcrest.Description;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 * Implements {@link QuickDiagnosingMatcher} interface, 
 * but matches and describes in separate steps.
 */
public abstract class TypesafeQuickMatcher<T> 
                extends QuickMatcherBase<T> {

    private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("matchesSafely", 1, 0);

    final private Class<?> expectedType;

    public TypesafeQuickMatcher(Class<?> expectedType) {
        this.expectedType = expectedType;
    }

    public TypesafeQuickMatcher(ReflectiveTypeFinder typeFinder) {
        this.expectedType = typeFinder.findExpectedType(getClass());
    }

    public TypesafeQuickMatcher() {
        this(TYPE_FINDER);
    }
    
    protected abstract boolean matchesSafely(T item);

    protected abstract void describeMismatchSafely(T item, Description mismatch);
    
    @Override
    @SuppressWarnings("unchecked")
    public final boolean matches(Object item) {
        return Typesafe.matches(item, expectedType)
                && matchesSafely((T) item);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void describeMismatch(Object item, Description description) {
        if (!Typesafe.matches(item, expectedType, description)) {
            return;
        }
        describeMismatchSafely((T)item, description);
    }

}
