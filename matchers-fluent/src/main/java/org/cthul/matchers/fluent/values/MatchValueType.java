package org.cthul.matchers.fluent.values;

/**
 *
 * @author Arian Treffer
 */
public interface MatchValueType<Value, Item> {
    
    MatchingObject<Item> matchingObject(Value v);
    
    MatchingObject<Item> cast(MatchingObject<Value> v);
    
}
