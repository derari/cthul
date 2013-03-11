package org.cthul.matchers.fluent.values;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public interface MatchValues<Item> {
    
    boolean hasNext();
    
    Item next();
    
    void success();
    
    void fail();
    
    void result(boolean success);
    
    boolean matched();

    void describeMismatch(Matcher<? super Item> matcher, Description mismatch);
    
}
