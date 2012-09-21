package org.cthul.matchers.fluent.assertion;

import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AnyOfStrategy<T> implements AssertionStrategy<T> {

    private final Set<T> set;

    public AnyOfStrategy(Collection<T> collection) {
        this.set = new HashSet<>(collection);
    }

    @Override
    public Object validate(Matcher<T> matcher) {
        T lastMismatch = null;
        Iterator<T> it = set.iterator();
        while (it.hasNext()) {
            final T o = it.next();
            if (!matcher.matches(o)) {
                it.remove();
                lastMismatch = o;
            }
        }
        if (set.isEmpty()) {
            return lastMismatch;
        } else {
            return VALID;
        }
    }
}
