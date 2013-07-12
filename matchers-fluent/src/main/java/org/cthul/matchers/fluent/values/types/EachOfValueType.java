package org.cthul.matchers.fluent.values.types;

import java.util.Arrays;
import java.util.Iterator;
import org.cthul.matchers.fluent.values.AbstractMatchValues;
import org.cthul.matchers.fluent.values.MatchValues;
import org.cthul.matchers.fluent.values.MatchingObject;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 *
 */
public class EachOfValueType<Item>
                extends IterableValueType<Iterable<? extends Item>, Item> {

    private static final EachOfValueType INSTANCE = new EachOfValueType<>();

    @Factory
    public static <I> EachOfValueType<I> eachOf() {
        return INSTANCE;
    }
    
    @Factory
    public static <I> MatchingObject<I> eachOf(Iterable<I> iterable) {
        return INSTANCE.matchingObject(iterable);
    }
    
    @Factory
    public static <I> MatchingObject<I> eachOf(I... array) {
        return eachOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchingObject<I> eachOf(MatchingObject<? extends Iterable<I>> mo) {
        return INSTANCE.adapt(mo);
    }
    
    @Factory
    public static <I> EachOfValueType<I> each(Class<I> c) {
        return INSTANCE;
    }
    
    @Factory
    public static <I> MatchingObject<I> each(Iterable<I> iterable) {
        return INSTANCE.matchingObject(iterable);
    }
    
    @Factory
    public static <I> MatchingObject<I> each(I... array) {
        return eachOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchingObject<I> each(MatchingObject<? extends Iterable<I>> mo) {
        return INSTANCE.adapt(mo);
    }
    
    @Factory
    public static EachOfValueType<Integer> eachInt() {
        return INSTANCE;
    }
    
    @Override
    protected MatchValues<Item> convert(Iterable<? extends Item> v) {
        return new EachOfValues<>(v);
    }
    
    protected static class EachOfValues<I> extends AbstractMatchValues<I> {
        
//        private Iterable<I> iterable;
        private Iterator<? extends I> iterator;
        private boolean success = true;
        private I current = null;
        
        public EachOfValues(Iterable<? extends I> iterable) {
//            this.iterable = iterable;
            this.iterator = iterable.iterator();
        }

        @Override
        public boolean hasNext() {
            return success && iterator.hasNext();
        }

        @Override
        public I next() {
            return current = iterator.next();
        }

        @Override
        public void success() {
        }

        @Override
        public void fail() {
            success = false;
        }

        @Override
        public boolean matched() {
            return success;
        }

        @Override
        public void describeMismatch(Matcher<? super I> matcher, Description mismatch) {
            matcher.describeMismatch(current, mismatch);
        }   
    }
}
