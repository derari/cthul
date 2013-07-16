package org.cthul.matchers.fluent.values;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * A value that can be matched.
 * <p>
 * Can have an internal state that changes 
 * after calls to {@link #matches(org.hamcrest.Matcher)};
 * 
 * @param <Item> 
 */
public interface MatchValue<Item> {
    
    boolean matches(ElementMatcher<Item> matcher);

    void describeExpected(Description description);
    
    void describeMismatch(Description description);
    
    boolean matched();
    
    interface Element<Item> {
        
        Item value();
        
    }
    
    interface ElementMatcher<Item> extends Matcher<Element<Item>> {
        
        void describeExpected(Element<Item> e, Description description);
        
    }
}
