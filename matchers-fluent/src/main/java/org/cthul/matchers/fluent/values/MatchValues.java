package org.cthul.matchers.fluent.values;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * A set of values that can be matched.
 * <p>
 * After each call to {@link #next()}; either {@link #success()}, 
 * {@link #fail()}, or {@link #result(boolean)} has to be called.
 * Finally, {@link #matched()} and 
 * {@link #describeMismatch(org.hamcrest.Matcher, org.hamcrest.Description)}
 * provide the result of the matching.
 * @param <Item> 
 */
public interface MatchValues<Item> {
    
    boolean hasNext();
    
    Item next();
    
    void success();
    
    void fail();
    
    void result(boolean success);
    
    boolean matched();

    void describeMismatch(Matcher<? super Item> matcher, Description mismatch);
    
}
