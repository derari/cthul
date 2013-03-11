package org.cthul.matchers.fluent.values;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public interface MatchingObject<T> {
    
    MatchValues<T> values();
    
    boolean validate(Matcher<? super T> matcher);
    
    boolean validate(Matcher<? super T> matcher, Description mismatch);
    
}
