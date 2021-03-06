package org.cthul.matchers.diagnose;

import org.cthul.matchers.diagnose.nested.NestedResultMatcher;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.safe.TypesafeQuickResultMatcher;
import org.hamcrest.*;

/**
 * A matcher that focuses on returning a {@link MatchResult}.
 * It is recommended that {@link #matches(java.lang.Object)} will be overridden
 * for better performance, but it is not necessary.
 * @param <T>
 * @see NestedResultMatcher
 * @see TypesafeQuickResultMatcher
 */
public abstract class QuickResultMatcherBase<T> 
                extends QuickDiagnosingMatcherBase<T> 
                implements QuickDiagnosingMatcher<T> {

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object o) {
        return matchResult(o).matched();
    }

    /** {@inheritDoc} */
    @Override
    public void describeMismatch(Object item, Description description) {
        MatchResult.Mismatch<?> mismatch = matchResult(item).getMismatch();
        if (mismatch != null) {
            mismatch.describeMismatch(description);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item, Description description) {
        MatchResult.Mismatch<?> mismatch = matchResult(item).getMismatch();
        if (mismatch != null) {
            mismatch.describeMismatch(description);
            return false;
        }
        return true;
    }

    @Override
    public abstract <I> MatchResult<I> matchResult(I item);

//    /**
//     * Uses the {@code matcher} to validate {@code item}.
//     * If validation fails, an error message is appended to {@code mismatch}.
//     * <p/>
//     * The code is equivalent to
//     * <pre>{@code
//     * if (matcher.matches(item)) {
//     *     return true;
//     * } else {
//     *     matcher.describeMismatch(item, mismatch);
//     *     return false;
//     * }
//     * }</pre>
//     * but uses optimizations for diagnosing matchers.
//     * 
//     * @param matcher
//     * @param item
//     * @param mismatch
//     * @return {@code true} iff {@code item} was matched
//     * @see DiagnosingMatcher
//     * @see QuickDiagnosingMatcher
//     */
//    protected static boolean quickMatch(Matcher<?> matcher, Object item, Description mismatch) {
//        return QuickDiagnose.matches(matcher, item, mismatch);
//    }
//    
//    /**
//     * Similar to {@link #quickMatch(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description)},
//     * but allows to override the mismatch message.
//     * <p/>
//     * If matching fails, {@code message} will be appended to {@code mismatch}.
//     * Any occurrence of {@code "$1"} in (@code message} will be replaced with
//     * the actual mismatch description of {@code matcher}.
//     * 
//     * @param matcher
//     * @param item
//     * @param mismatch
//     * @param message
//     * @return {@code true} iff {@code item} was matched
//     */
//    protected static boolean quickMatch(Matcher<?> matcher, Object item, Description mismatch, String message) {
//        return QuickDiagnose.matches(matcher, item, mismatch, message);
//    }
//    
//    protected static <T> MatchResult<T> quickMatchResult(Matcher<?> matcher, T item) {
//        return QuickDiagnose.matchResult(matcher, item);
//    }
}
