package org.cthul.matchers.diagnose.result;

import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribing;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Provides detailed information about the result of a match.
 * <p>
 * Implementations should extends {@link MatchResultBase}
 */
public interface MatchResult<T> extends PrecedencedSelfDescribing {
    
    /**
     * Returns whether the value was matched.
     * @return true iff the value was matched.
     */
    boolean matched();
    
    /**
     * Returns details about a successful match.
     * @return match or {@code null}
     */
    Match<T> getMatch();
    
    
    /**
     * Returns details about a failed match.
     * @return mismatch or {@code null}
     */
    Mismatch<T> getMismatch();
    
    /**
     * Returns the value that was matched.
     * @return value
     */
    T getValue();

    /**
     * Returns the matcher.
     * @return matcher
     */
    Matcher<?> getMatcher();

    /**
     * Describes the matcher.
     * @param description 
     */
    void describeMatcher(Description description);

    /**
     * Returns the matcher's message precedence.
     * @return precedence
     * @see PrecedencedSelfDescribing
     */
    int getMatcherPrecedence();

    /**
     * Returns an object that will describe itself as the matcher.
     * @return matcher description
     */
    PrecedencedSelfDescribing getMatcherDescription();
    
    /**
     * Provides detailed information about a match.
     * @param <T> 
     */
    interface Match<T> extends MatchResult<T> {
        
        /**
         * Describes the match (e.g., describes the matcher).
         * @param description 
         */
        void describeMatch(Description description);
        
        /**
         * Returns the precedence of the match message
         * @return precdence
         * @see PrecedencedSelfDescribing
         */
        int getMatchPrecedence();
        
        /**
         * Returns an object that will describe itself as the match.
         * @return match description
         */
        PrecedencedSelfDescribing getMatchDescription();
        
    }
    
    static interface Mismatch<T> extends MatchResult<T> {
        
        /**
         * Describes what was expected (e.g., describes the matcher).
         * @param description 
         */
        void describeExpected(Description description);
        
        /**
         * Returns the precedence of the expected message
         * @return precdence
         * @see PrecedencedSelfDescribing
         */
        int getExpectedPrecedence();
        
        /**
         * Returns an object that will describe itself as the expected message.
         * @return match description
         */
        PrecedencedSelfDescribing getExpectedDescription();
        
        /**
         * Describes the mismatch (e.g., uses the matcher's mismatch description).
         * @param description 
         */
        void describeMismatch(Description description);
        
        /**
         * Returns the precedence of the mismatch
         * @return precdence
         * @see PrecedencedSelfDescribing
         */
        int getMismatchPrecedence();
        
        /**
         * Returns an object that will describe itself as the mismatch.
         * @return match description
         */
        PrecedencedSelfDescribing getMismatchDescription();
    }
}
