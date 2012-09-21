package org.cthul.matchers.diagnose;

import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;

/**
 * A {@link Matcher} that is able to match and diagnose in one step, but, unlike
 * {@link DiagnosingMatcher}, also provides an efficient implementation for the
 * simple match.
 * 
 * Implementations of this interface should extend 
 * {@link QuickDiagnoseMatcherBase}. 
 * See this class for more implementation tips.
 * 
 * @author Arian Treffer
 */
public interface QuickDiagnosingMatcher<T> extends Matcher<T> {
    
    /**
     * Evaluates the matcher for argument {@code item}.
     * <p/>
     * A mismatch description is appended to {@code mismatch} 
     * if and only if the match fails.
     *
     * @param item The object against which the matcher is evaluated.
     * @param nismatch The description to be built or appended to.
     * @return {@code true} if {@code item} matches, otherwise {@code false}.
     */
    public boolean matches(Object item, Description mismatch);
    
}
