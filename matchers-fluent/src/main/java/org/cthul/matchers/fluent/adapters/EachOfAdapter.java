package org.cthul.matchers.fluent.adapters;

import java.util.Arrays;
import org.cthul.matchers.fluent.values.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.values.AbstractMatchValue;
import org.cthul.matchers.fluent.values.MatchValue;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 *
 */
public class EachOfAdapter<Item> extends 
                AbstractMatchValueAdapter<Iterable<? extends Item>, Item> {

    private static final EachOfAdapter INSTANCE = new EachOfAdapter<>();

    @Factory
    public static <I> EachOfAdapter<I> eachOf() {
        return INSTANCE;
    }
    
    @Factory
    public static <I> MatchValue<I> eachOf(Iterable<? extends I> iterable) {
        return (MatchValue) INSTANCE.adapt(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> eachOf(I... array) {
        return eachOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> eachOf(MatchValue<? extends Iterable<? extends I>> mo) {
        return INSTANCE.wrap(mo);
    }
    
    @Factory
    public static <I> EachOfAdapter<I> each(Class<I> c) {
        return INSTANCE;
    }
    
    @Factory
    public static <I> MatchValue<I> each(Iterable<? extends I> iterable) {
        return INSTANCE.adapt(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> each(I... array) {
        return eachOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> each(MatchValue<? extends Iterable<? extends I>> mo) {
        return INSTANCE.wrap(mo);
    }
    
    @Factory
    public static EachOfAdapter<Integer> eachInt() {
        return INSTANCE;
    }

    @Override
    public MatchValue<Item> adapt(Iterable<? extends Item> v) {
        return new EachOfValues<>(v);
    }
    
    @Override
    public void describeTo(Matcher<?> matcher, Description description) {
        description.appendText("each ");
        description.appendDescriptionOf(matcher);
    }

    protected static class EachOfValues<I> extends AbstractMatchValue<I> {
        
        private final Iterable<? extends I> iterable;
        
        public EachOfValues(Iterable<? extends I> iterable) {
            this.iterable = iterable;
        }

        @Override
        public boolean matches(Matcher<? super I> matcher) {
            for (I item: iterable) {
                if (!matcher.matches(item)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean matches(Matcher<? super I> matcher, Description mismatch) {
            int i = 0;
            for (I item: iterable) {
                if (!matcher.matches(item)) {
                    mismatch.appendText("#")
                            .appendText(String.valueOf(i))
                            .appendText(" ");
                    matcher.describeMismatch(item, mismatch);
                    return false;
                }
                i++;
            }
            return true;
        }

        @Override
        public void describeExpected(Matcher<? super I> matcher, Description description) {
            description.appendText("each ");
            description.appendDescriptionOf(matcher);
        }

        @Override
        public void describeMismatch(Matcher<? super I> matcher, Description description) {
            matches(matcher, description);
        }

    }
}
