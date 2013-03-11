package org.cthul.matchers.fluent.strategy;

import org.cthul.matchers.fluent.assertion.*;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AnyOfStrategy<T> implements MatchStrategy<Collection<T>, T> {
    
    private static final AnyOfStrategy INSTANCE = new AnyOfStrategy();
    
    public static <T> AnyOfStrategy<T> getInstance() {
        return INSTANCE;
    }
    
    public static <T> MObject<T> applyTo(Collection<T> t) {
        return AnyOfStrategy.<T>getInstance().apply(t);
    }
    
    @Override
    public MObject<T> apply(Collection<T> t) {
        return new MObject<>(t);
    }

    public static class MObject<T> implements MatchingObject<T> {

        private final Set<T> set;

        public MObject(Collection<T> collection) {
            this.set = new HashSet<>(collection);
        }

        @Override
        public Object validate(Matcher<? super T> matcher) {
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
    
}
