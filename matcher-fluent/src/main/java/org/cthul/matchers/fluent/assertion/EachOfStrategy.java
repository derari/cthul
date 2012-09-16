package org.cthul.matchers.fluent.assertion;

import org.hamcrest.Matcher;

import java.util.Collection;

public class EachOfStrategy<T> implements AssertionStrategy<T> {

    private final Collection<T> collection;

    public EachOfStrategy(Collection<T> collection) {
        this.collection = collection;
    }

    @Override
    public Object validate(Matcher<T> matcher) {
        for (T o: collection) {
            if (!matcher.matches(o)) {
                return o;
            }
        }
        return VALID;
    }
}
