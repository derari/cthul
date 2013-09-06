package org.cthul.matchers.diagnose.result;

import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribing;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public interface MatchResult<T> extends PrecedencedSelfDescribing {
    
    boolean isSuccess();
    
    Match<T> getMatch();
    
    Mismatch<T> getMismatch();
    
    T getValue();

    Matcher<?> getMatcher();

    void describeMatcher(Description d);

    int getMatcherPrecedence();

    PrecedencedSelfDescribing getMatcherDescription();
    
    static interface Match<T> extends MatchResult<T> {
        
        void describeMatch(Description d);
        
        int getMatchPrecedence();
        
        PrecedencedSelfDescribing getMatchDescription();
        
    }
    
    static interface Mismatch<T> extends MatchResult<T> {
        
        void describeExpected(Description d);
        
        int getExpectedPrecedence();
        
        PrecedencedSelfDescribing getExpectedDescription();
        
        void describeMismatch(Description d);
        
        int getMismatchPrecedence();
        
        PrecedencedSelfDescribing getMismatchDescription();
        
    }
}
