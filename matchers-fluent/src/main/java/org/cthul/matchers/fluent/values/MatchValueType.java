package org.cthul.matchers.fluent.values;

/**
 * Creates a {@link MatchingObject} from a plain value.
 * <p>
 * A MatchValueType represents an aspect of some type, 
 * like the length of strings or each element of lists.
 * It can then be used to create a {@link MatchingObject} that represents
 * this aspect of a concrete value.
 */
public interface MatchValueType<Value, Item> {
    
    MatchingObject<Item> matchingObject(Value v);
    
    MatchingObject<Item> adapt(MatchingObject<Value> v);
    
}
