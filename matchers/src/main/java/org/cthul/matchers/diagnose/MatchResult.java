package org.cthul.matchers.diagnose;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 *
 */
public interface MatchResult<T> {
    
    boolean isSuccess();
    
    Mismatch<T> getMismatch();
    
    static interface Mismatch<T> {
        
        T getValue();
        
        Matcher<?> getMatcher();
        
        void describeMatcher(Description d);
        
        int getMatcherPrecedence();
        
        SelfDescribing getMatcherDescription();
        
        void describeExpected(Description d);
        
        int getExpectedPrecedence();
        
        SelfDescribing getExpectedDescription();
        
        void describeMismatch(Description d);
        
        int getMismatchPrecedence();
        
        SelfDescribing getMismatchDescription();
        
    }
}
