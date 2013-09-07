package org.cthul.matchers.diagnose.safe;

import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 *
 */
public abstract class TypesafeNestedResultMatcher<T> 
                extends TypesafeNestedMatcher<T> {
    
    private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("matchResultSafely", 2, 0);

    public TypesafeNestedResultMatcher(Class<?> expectedType) {
        super(expectedType);
    }

    public TypesafeNestedResultMatcher(ReflectiveTypeFinder typeFinder) {
        super(typeFinder);
    }

    public TypesafeNestedResultMatcher() {
        super(TYPE_FINDER);
    }
    
    @Override
    protected abstract <I extends T> MatchResult<I> matchResultSafely(I item);
    
    @Override
    protected boolean matchesSafely(T item) {
        return matchResultSafely(item).matched();
    }

    @Override
    protected boolean matchesSafely(T item, Description mismatch) {
        MatchResult.Mismatch<T> m = matchResultSafely(item).getMismatch();
        if (m == null) {
            return true;
        }
        m.describeMismatch(mismatch);
        return false;
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatch) {
        matchesSafely(item, mismatch);
    }
}
