package org.cthul.matchers.diagnose;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Implements {@link QuickDiagnosingMatcher} interface.
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
 * 
 * @author Arian Treffer
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
    
}
