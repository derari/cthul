package org.cthul.matchers.diagnose.result;

import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribing;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public interface MatchResult<T> extends PrecedencedSelfDescribing {
    
    boolean matched();
    
    Match<T> getMatch();
    
    Mismatch<T> getMismatch();
    
    T getValue();

    Matcher<?> getMatcher();

    void describeMatcher(Description description);

    int getMatcherPrecedence();

    PrecedencedSelfDescribing getMatcherDescription();
    
    static interface Match<T> extends MatchResult<T> {
        
        void describeMatch(Description description);
        
        int getMatchPrecedence();
        
        PrecedencedSelfDescribing getMatchDescription();
        
    }
    
    static interface Mismatch<T> extends MatchResult<T> {
        
        void describeExpected(Description description);
        
        int getExpectedPrecedence();
        
        PrecedencedSelfDescribing getExpectedDescription();
        
        void describeMismatch(Description description);
        
        int getMismatchPrecedence();
        
        PrecedencedSelfDescribing getMismatchDescription();
        
    }
}
