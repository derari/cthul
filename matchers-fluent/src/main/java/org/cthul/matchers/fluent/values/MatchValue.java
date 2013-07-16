package org.cthul.matchers.fluent.values;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 * A value that can be matched.
 * <p>
 * Has an internal state that changes 
 * after calls to {@link #matches(ElementMatcher)};
 * 
 * @param <Item> 
 */
public interface MatchValue<Item> extends SelfDescribing {
    
    boolean matches(ElementMatcher<Item> matcher);
    
    boolean matched();
    
    @Override
    void describeTo(Description description);
    
    void describeValueType(Description description);

    void describeExpected(ExpectationDescription description);
    
    void describeMismatch(Description description);
    
    interface Element<Item> {
        
        Item value();
        
    }
    
    interface ElementMatcher<Item> extends Matcher<Element<Item>> {
        
        void describeExpected(Element<Item> e, ExpectationDescription description);
        
    }
    
    interface ExpectationDescription extends Description {
        
        void addedExpectation();
        
    }
}
