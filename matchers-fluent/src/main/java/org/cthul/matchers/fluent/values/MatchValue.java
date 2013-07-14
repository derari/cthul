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
    
    boolean matches(Matcher<? super Item> matcher);
    
    boolean matches(Matcher<? super Item> matcher, Description mismatch);

    void describeExpected(Matcher<? super Item> matcher, Description description);
    
    void describeMismatch(Matcher<? super Item> matcher, Description description);
    
    Element<Item> elements();
    
    boolean matched();
    
    interface Element<Item> {
        
        Item value();
        
        void success();
        
        void fail();
        
        void result(boolean match);
        
        Element<Item> next();
    }
}
