package org.cthul.matchers.diagnose;

import org.hamcrest.Description;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 *
 */
public abstract class TypesafeNestedMatcher<T> 
                extends NestedMatcher<T> {
    
    private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("matchesSafely", 2, 0);

    protected boolean matchesSafely(T item) {
        return matchesSafely(item, Description.NONE);
    }

    protected void describeMismatchSafely(T item, Description mismatch) {
        matchesSafely(item, mismatch);
    }
    
    protected abstract boolean matchesSafely(T item, Description mismatch);

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
    
    @Override
    @SuppressWarnings("unchecked")
    public final boolean matches(Object item) {
        return item != null
                && expectedType.isInstance(item)
                && matchesSafely((T) item);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final boolean matches(Object item, Description description) {
        if (item == null) {
            description.appendText("was null");
            return false;
        } else if (! expectedType.isInstance(item)) {
            description.appendText("was a ")
                       .appendText(item.getClass().getName())
                       .appendText(" (")
                       .appendValue(item)
                       .appendText(")");
            return false;
        } else {
            return matchesSafely((T) item, description);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void describeMismatch(Object item, Description description) {
        if (item == null) {
            description.appendText("was null");
        } else if (! expectedType.isInstance(item)) {
            description.appendText("was a ")
                       .appendText(item.getClass().getName())
                       .appendText(" (")
                       .appendValue(item)
                       .appendText(")");
        } else {
            describeMismatchSafely((T)item, description);
        }
    }

}
