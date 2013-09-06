package org.cthul.matchers.diagnose;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;

/**
 * Implements {@link QuickDiagnosingMatcher} interface, 
 * but matches and describes in separate steps.
 * <p/>
 * {@link QuickDiagnosingMatcher#matches(java.lang.Object, org.hamcrest.Description)} 
 * is implemented as
 * <pre>{@code 
 *   if (matches(item)) {
 *       return true;
 *   } else {
 *       describeMismatch(item, description);
 *       return false;
 *   }
 * }<pre/>
 * <p/>
 * If you want to override {@link #matches(java.lang.Object, org.hamcrest.Description)},
 * extend {@link QuickDiagnosingMatcherBase} instead.
 */
public abstract class QuickMatcherBase<T> 
                extends BaseMatcher<T> 
                implements QuickDiagnosingMatcher<T> {

    /** {@inheritDoc} */
    @Override
    public abstract boolean matches(Object item);

    /** {@inheritDoc} */
    @Override
    public abstract void describeMismatch(Object item, Description description);

    /**
     * <pre>{@code 
     *   if (matches(item)) {
     *       return true;
     *   } else {
     *       describeMismatch(item, description);
     *       return false;
     *   }
     * }<pre/>
     * @param item
     * @param mismatch
     * @return match 
     */
    @Override
    public final boolean matches(Object item, Description mismatch) {
        if (matches(item)) {
            return true;
        } else {
            describeMismatch(item, mismatch);
            return false;
        }
    }

    @Override
    public <I> MatchResult<I> matchResult(I item) {
        StringDescription mismatch = new StringDescription();
        if (matches(item, mismatch)) {
            return MatchResultSuccess.instance();
        } else {
            return new MatchResultMismatch<>(item, this, mismatch.toString());
        }
    }
    
}
