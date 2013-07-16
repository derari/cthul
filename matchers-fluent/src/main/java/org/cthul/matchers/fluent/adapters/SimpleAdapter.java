package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.fluent.values.MatchValue;
import org.cthul.matchers.fluent.values.MatchValue.ElementMatcher;
import org.cthul.matchers.fluent.values.MatchValue.ExpectationDescription;
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
    public void describeMatcher(Matcher<?> matcher, Description description) {
        describeSelf(description);
        description.appendDescriptionOf(matcher);
    }

    @Override
    protected void describeTo(MatchValue<Value> actual, Description description) {
        describeSelfOf(description);
        actual.describeTo(description);
    }

    @Override
    protected void describeValueType(MatchValue<Value> actual, Description description) {
        describeSelfOf(description);
        actual.describeValueType(description);
    }

    @Override
    protected void describeExpected(MatchValue.Element<Value> value, MatchValue.Element<Item> item, ElementMatcher<Item> matcher, ExpectationDescription description) {
        describeSelf(description);
        matcher.describeExpected(item, description);
    }

    @Override
    protected void describeMismatch(MatchValue.Element<Value> value, MatchValue.Element<Item> item, ElementMatcher<Item> matcher, Description description) {
        describeSelf(description);
        matcher.describeMismatch(item, description);
    }

    protected void describeSelf(Description description) {
        if (name != null) {
            description.appendText(name).appendText(" ");
        }
    }
    
    protected void describeSelfOf(Description description) {
        if (name != null) {
            description.appendText(name).appendText(" of ");
        }
    }
}
