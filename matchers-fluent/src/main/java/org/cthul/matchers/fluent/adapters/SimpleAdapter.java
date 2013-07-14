package org.cthul.matchers.fluent.adapters;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class SimpleAdapter<Value, Item> extends ConvertingAdapter<Value, Item> {
    
    private String name;

    public SimpleAdapter(String name) {
        this.name = name;
    }

    @Override
    public void describeTo(Matcher<?> matcher, Description description) {
        description.appendText(name).appendText(" ");
        description.appendDescriptionOf(matcher);
    }

    @Override
    protected void describeMismatch(Matcher<?> matcher, Value value, Item item, Description description) {
        description.appendText(name).appendText(" ");
        matcher.describeMismatch(item, description);
    }
    
}
