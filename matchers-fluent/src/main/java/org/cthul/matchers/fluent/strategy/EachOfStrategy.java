package org.cthul.matchers.fluent.strategy;

import org.hamcrest.Matcher;

import java.util.Collection;

public class EachOfStrategy<T> implements MatchStrategy<Collection<T>, T>{
    
    private static final EachOfStrategy INSTANCE = new EachOfStrategy();
    
    public static <T> EachOfStrategy<T> getInstance() {
        return INSTANCE;
    }
    
    public static <T> MObject<T> applyTo(Collection<T> t) {
        return EachOfStrategy.<T>getInstance().apply(t);
    }
    
    @Override
    public MObject<T> apply(Collection<T> t) {
        return new MObject<>(t);
    }

    public static class MObject<T> implements MatchingObject<T> {

        private final Collection<T> collection;

        public MObject(Collection<T> collection) {
            this.collection = collection;
        }

        @Override
        public Object validate(Matcher<? super T> matcher) {
            for (T o: collection) {
                if (!matcher.matches(o)) {
                    return o;
                }
            }
            return VALID;
        }
        
    }
    
}
