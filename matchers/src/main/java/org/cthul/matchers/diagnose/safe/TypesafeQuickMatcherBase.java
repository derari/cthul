package org.cthul.matchers.diagnose.safe;

import org.cthul.matchers.diagnose.QuickMatcherBase;
import org.hamcrest.Description;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 *
 */
public abstract class TypesafeQuickMatcherBase<T> 
                extends QuickMatcherBase<T> {

    private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("matchesSafely", 1, 0);

    final private Class<?> expectedType;

    public TypesafeQuickMatcherBase(Class<?> expectedType) {
        this.expectedType = expectedType;
    }

    public TypesafeQuickMatcherBase(ReflectiveTypeFinder typeFinder) {
        this.expectedType = typeFinder.findExpectedType(getClass());
    }

    public TypesafeQuickMatcherBase() {
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
