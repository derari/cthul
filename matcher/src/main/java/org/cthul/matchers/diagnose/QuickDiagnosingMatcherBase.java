package org.cthul.matchers.diagnose;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 * @author Arian Treffer
 */
public abstract class QuickDiagnosingMatcherBase<T> 
                extends BaseMatcher<T> 
                implements QuickDiagnosingMatcher<T> {

    @Override
    public abstract boolean matches(Object o);

    @Override
    public abstract boolean matches(Object item, Description mismatch);

    @Override
    public abstract void describeMismatch(Object item, Description description);
    
    protected static boolean matches(Matcher<?> matcher, Object item, Description mismatch) {
        return QuickDiagnose.matches(matcher, item, mismatch);
    }
    
    protected static boolean matches(Matcher<?> matcher, Object item, Description mismatch, String message) {
        return QuickDiagnose.matches(matcher, item, mismatch, message);
    }
    
}
