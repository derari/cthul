package org.cthul.matchers.fluent.value;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Creates {@link MatchValue} from a plain value.
 * <p>
 * A MatchValueAdapter represents an aspect of some type, 
 * like the length of strings or each element of lists.
 * It can then be used to create {@link MatchValue} that represent
 * this aspect of a concrete value.
 */
public interface MatchValueAdapter<Value, Item> {
    
    MatchValue<Item> adapt(Value v);
    
    MatchValue<Item> adapt(MatchValue<Value> v);
    
    void describeMatcher(Matcher<?> matcher, Description description);
    
}