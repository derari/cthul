package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.fluent.values.MatchValue;
import org.cthul.matchers.fluent.values.MatchValue.ElementMatcher;
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
        if (name != null) {
            description.appendText(name).appendText(" ");
        }
        description.appendDescriptionOf(matcher);
    }

    @Override
    protected void describeExpected(MatchValue.Element<Value> value, MatchValue.Element<Item> item, ElementMatcher<Item> matcher, Description description) {
        if (name != null) {
            description.appendText(name).appendText(" ");
        }
        matcher.describeExpected(item, description);
    }

    @Override
    protected void describeMismatch(MatchValue.Element<Value> value, MatchValue.Element<Item> item, ElementMatcher<Item> matcher, Description description) {
        if (name != null) {
            description.appendText(name).appendText(" ");
        }
        matcher.describeMismatch(item, description);
    }
}
