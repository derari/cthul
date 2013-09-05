package org.cthul.matchers.diagnose;

import org.hamcrest.Description;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 *
 */
public abstract class TypesafeQuickMatcherBase<T> 
                extends QuickMatcherBase<T> {

    private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("matchesSafely", 1, 0);

    protected abstract boolean matchesSafely(T item);

    protected abstract void describeMismatchSafely(T item, Description mismatch);
    
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
    
    @Override
    @SuppressWarnings("unchecked")
    public final boolean matches(Object item) {
        return item != null
                && expectedType.isInstance(item)
                && matchesSafely((T) item);
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
