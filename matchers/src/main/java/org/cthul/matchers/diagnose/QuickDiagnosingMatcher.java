package org.cthul.matchers.diagnose;

import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.*;

/**
 * A {@link Matcher} that is able to match and diagnose in one step, but, unlike
 * {@link DiagnosingMatcher}, also provides an efficient implementation for the
 * simple match.
 * <p>
 * The matcher is also able to provide a {@link MatchResult} that caches
 * all relevant information about the match.
 * <p>
 * Implementations of this interface should extend 
 * {@link QuickDiagnosingMatcherBase}. 
 * See this class for more implementation tips.
 * @param <T>
 */
public interface QuickDiagnosingMatcher<T> extends Matcher<T> {
    
    /**
     * Evaluates the matcher for argument {@code item}.
     * <p>
     * A mismatch description is appended to {@code mismatch} 
     * if and only if the match fails.
     *
     * @param item The object against which the matcher is evaluated.
     * @param mismatch The description to be built or appended to.
     * @return {@code true} if {@code item} matches, otherwise {@code false}.
     */
    boolean matches(Object item, Description mismatch);
 
    /**
     * Returns a {@link MatchResult} for {@code item}
     * @param <I>
     * @param item
     * @return match result
     */
    <I> MatchResult<I> matchResult(I item);
    
}
