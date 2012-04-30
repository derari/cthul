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
    
    public boolean matches(Object item, Description mismatch);
    
}
