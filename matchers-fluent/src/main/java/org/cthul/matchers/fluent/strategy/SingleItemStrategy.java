package org.cthul.matchers.fluent.strategy;

import org.hamcrest.Matcher;

public class SingleItemStrategy<T> implements MatchStrategy<T, T> { 

    private static final SingleItemStrategy INSTANCE = new SingleItemStrategy();
    
    public static <T> SingleItemStrategy<T> getInstance() {
        return INSTANCE;
    }
    
    public static <T> MObject<T> applyTo(T t) {
        return SingleItemStrategy.<T>getInstance().apply(t);
    }
    
    @Override
    public MObject<T> apply(T t) {
        return new MObject<>(t);
    }

    public static class MObject<T> implements MatchingObject<T> {

        private final T item;

        public MObject(T item) {
            this.item = item;
        }

        @Override
        public Object validate(Matcher<? super T> matcher) {
            if (matcher.matches(item)) {
                return VALID;
            } else {
                return item;
            }
        }
        
    }
}
