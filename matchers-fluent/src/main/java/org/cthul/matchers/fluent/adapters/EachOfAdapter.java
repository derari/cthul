package org.cthul.matchers.fluent.adapters;

import java.util.Arrays;
import java.util.Iterator;
import org.cthul.matchers.fluent.values.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.values.MatchValue;
import org.cthul.matchers.fluent.values.MatchValue.Element;
import org.cthul.matchers.fluent.values.MatchValue.ElementMatcher;
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
        return INSTANCE.adapt(mo);
    }
    
    @Factory
    public static <I> EachOfAdapter<I> each() {
        return INSTANCE;
    }
    
    @Factory
    public static <I> EachOfAdapter<I> each(Class<I> c) {
        return each();
    }
    
    @Factory
    public static <I> MatchValue<I> each(Iterable<? extends I> iterable) {
        return eachOf(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> each(I... array) {
        return eachOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> each(MatchValue<? extends Iterable<? extends I>> mo) {
        return eachOf(mo);
    }
    
    @Factory
    public static EachOfAdapter<Integer> eachInt() {
        return each();
    }

    @Override
    public MatchValue<Item> adapt(MatchValue<Iterable<? extends Item>> v) {
        return new EachOfValues<>(v);
    }

    @Override
    public void describeMatcher(Matcher<?> matcher, Description description) {
        description.appendText("each ");
        description.appendDescriptionOf(matcher);
    }

    protected static class EachOfValues<Item> extends AbstractAdaptedValue<Iterable<? extends Item>, Item> {

        public EachOfValues(MatchValue<Iterable<? extends Item>> actualValue) {
            super(actualValue);
        }

        @Override
        protected Object createItem(Element<Iterable<? extends Item>> key) {
            return new EachItemIterable<>(key.value());
        }

        @Override
        protected boolean matches(Element<Iterable<? extends Item>> element, ElementMatcher<Item> matcher) {
            EachItemIterable<Item> it = cachedItem(element);
            if (it.invalid != null) return false;
            E<Item> e = it.first();
            while (e != null) {
                if (!matcher.matches(e)) {
                    e.mismatch = matcher;
                    it.invalid = e;
                    return false;
                }
                e = it.next(e);
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("each of ");
            getActualValue().describeTo(description);
        }

        @Override
        public void describeValueType(Description description) {
            description.appendText("each of ");
            getActualValue().describeValueType(description);
        }

        @Override
        protected void describeExpected(Element<Iterable<? extends Item>> element, ElementMatcher<Item> matcher, ExpectationDescription description) {
            EachItemIterable<Item> it = cachedItem(element);
            E<Item> e = it.invalid;
            assert e.mismatch == matcher : 
                    "The nested value should complain only about the matcher "
                    + "that caused this one to fail";
            matcher.describeExpected(e, description);
        }

        @Override
        protected void describeMismatch(Element<Iterable<? extends Item>> element, ElementMatcher<Item> matcher, Description description) {
            EachItemIterable<Item> it = cachedItem(element);
            E<Item> e = it.invalid;
            assert e.mismatch == matcher : 
                    "The nested value should complain only about the matcher "
                    + "that caused this one to fail";
            description.appendText("#")
                       .appendText(String.valueOf(e.i))
                       .appendText(" ");
            matcher.describeMismatch(e, description);
        }        
    }
    
    protected static class EachItemIterable<Item> {
        
        private final Iterator<? extends Item> source;
        private E<Item> first = null;
        E<Item> invalid = null;
        
        public EachItemIterable(Iterable<? extends Item> iterable) {
            this.source = iterable.iterator();
        }
        
        public E<Item> first() {
            if (first == null) {
                if (!source.hasNext()) return null;
                first = new E<>(source.next(), 0);
            }
            return first;
        }
        
        public E<Item> next(E<Item> e) {
            if (e.next == null) {
                if (!source.hasNext()) return null;
                e.next = new E<>(source.next(), e.i+1);
            }
            return e.next;
        }
    }
    
    protected static class E<Item> implements Element<Item> {
        
        private final Item value;
        final int i;
        ElementMatcher<Item> mismatch = null;
        E<Item> next = null;

        public E(Item value, int i) {
            this.value = value;
            this.i = i;
        }

        @Override
        public Item value() {
            return value;
        }
    }
}
