package org.cthul.matchers.diagnose;

import org.hamcrest.Description;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 *
 * @author Arian Treffer
 */
public abstract class TypesafeQuickDiagnoseMatcherBase<T> 
                extends QuickDiagnosingMatcherBase<T> {
    
    private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("matchesSafely", 1, 0);

    protected abstract boolean matchesSafely(T item);

    protected abstract boolean matchesSafely(T item, Description mismatchDescription);

    protected abstract void describeMismatchSafely(T item, Description mismatchDescription);
    
    final private Class<?> expectedType;

    public TypesafeQuickDiagnoseMatcherBase(Class<?> expectedType) {
        this.expectedType = expectedType;
    }

    public TypesafeQuickDiagnoseMatcherBase(ReflectiveTypeFinder typeFinder) {
        this.expectedType = typeFinder.findExpectedType(getClass());
    }

    public TypesafeQuickDiagnoseMatcherBase() {
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
