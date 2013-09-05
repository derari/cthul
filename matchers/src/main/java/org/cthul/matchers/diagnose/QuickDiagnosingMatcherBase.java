package org.cthul.matchers.diagnose;

import org.hamcrest.*;

/**
 * A {@link Matcher} that is able to match and diagnose in one step, but, unlike
 * {@link DiagnosingMatcher}, also provides an efficient implementation for the
 * simple match.
 */
public abstract class QuickDiagnosingMatcherBase<T> 
                extends BaseMatcher<T> 
                implements QuickDiagnosingMatcher<T> {

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object o) {
        return matches(o, Description.NONE);
    }

    /** {@inheritDoc} */
    @Override
    public void describeMismatch(Object item, Description description) {
        matches(item, description);
    }
    
    /** {@inheritDoc} */
    @Override
    public abstract boolean matches(Object item, Description mismatch);

    /**
     * Uses the {@code matcher} to validate {@code item}.
     * If validation fails, an error message is appended to {@code mismatch}.
     * <p/>
     * The code is equivalent to
     * <pre>{@code
     * if (matcher.matches(item)) {
     *     return true;
     * } else {
     *     matcher.describeMismatch(item, mismatch);
     *     return false;
     * }
     * }</pre>
     * but uses optimizations for diagnosing matchers.
     * 
     * @param matcher
     * @param item
     * @param mismatch
     * @return {@code true} iif {@code item} was matched
     * @see DiagnosingMatcher
     * @see QuickDiagnosingMatcher
     */
    protected static boolean quickMatch(Matcher<?> matcher, Object item, Description mismatch) {
        return QuickDiagnose.matches(matcher, item, mismatch);
    }
    
    /**
     * Similar to {@link #quickMatch(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description)},
     * but allows to override the mismatch message.
     * <p/>
     * If matching fails, {@code message} will be appended to {@code mismatch}.
     * Any occurrence of {@code "$1"} in (@code message} will be replaced with
     * the actual mismatch description of {@code matcher}.
     * 
     * @param matcher
     * @param item
     * @param mismatch
     * @param message
     * @return {@code true} iif {@code item} was matched
     */
    protected static boolean quickMatch(Matcher<?> matcher, Object item, Description mismatch, String message) {
        return QuickDiagnose.matches(matcher, item, mismatch, message);
    }
    
}
