package org.cthul.matchers.fluent.assertion;

import org.hamcrest.Matcher;

public class SingleItemStrategy<T> implements AssertionStrategy<T> {

    private final T item;

    public SingleItemStrategy(T item) {
        this.item = item;
    }

    @Override
    public Object validate(Matcher<T> matcher) {
        if (matcher.matches(item)) {
            return VALID;
        } else {
            return item;
        }
    }
}
