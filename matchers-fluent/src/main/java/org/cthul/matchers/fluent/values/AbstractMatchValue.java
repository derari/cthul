package org.cthul.matchers.fluent.values;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class AbstractMatchValue<Item> implements MatchValue<Item> {

    @Override
    public boolean matches(Matcher<? super Item> matcher) {
        Element<Item> e = elements();
        while (e != null) {
            Item i = e.value();
            e.result(matcher.matches(i));
        }
        return matched();
    }

    @Override
    public boolean matches(Matcher<? super Item> matcher, Description mismatch) {
        if (matches(matcher)) {            
            return true;
        }
        describeMismatch(matcher, mismatch);
        return false;
    }
    
}
