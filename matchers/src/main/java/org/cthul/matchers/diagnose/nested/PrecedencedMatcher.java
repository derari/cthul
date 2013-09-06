package org.cthul.matchers.diagnose.nested;

import org.hamcrest.Matcher;

public interface PrecedencedMatcher<T> extends Matcher<T>, PrecedencedSelfDescribing {
    
    int getMismatchPrecedence();
    
}
