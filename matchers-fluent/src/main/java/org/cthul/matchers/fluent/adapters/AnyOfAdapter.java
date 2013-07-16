package org.cthul.matchers.fluent.adapters;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
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
public class AnyOfAdapter<Item> extends 
                AbstractMatchValueAdapter<Iterable<? extends Item>, Item> {

    private static final AnyOfAdapter INSTANCE = new AnyOfAdapter<>();

    @Factory
    public static <I> AnyOfAdapter<I> anyOf() {
        return INSTANCE;
    }
    
    @Factory
    public static <I> MatchValue<I> anyOf(Iterable<? extends I> iterable) {
        return (MatchValue) INSTANCE.adapt(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> anyOf(I... array) {
        return anyOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> anyOf(MatchValue<? extends Iterable<? extends I>> mo) {
        return INSTANCE.adapt(mo);
    }
    
    @Factory
    public static <I> AnyOfAdapter<I> any() {
        return INSTANCE;
    }
    
    @Factory
    public static <I> AnyOfAdapter<I> any(Class<I> c) {
        return any();
    }
    
    @Factory
    public static <I> MatchValue<I> any(Iterable<? extends I> iterable) {
        return anyOf(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> any(I... array) {
        return anyOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> any(MatchValue<? extends Iterable<? extends I>> mo) {
        return anyOf(mo);
    }
    
    @Factory
    public static AnyOfAdapter<Integer> anyInt() {
        return any();
    }

    @Override
    public MatchValue<Item> adapt(MatchValue<Iterable<? extends Item>> v) {
        return new AnyOfValues<>(v);
    }

    @Override
    public void describeMatcher(Matcher<?> matcher, Description description) {
        description.appendText("any ");
        description.appendDescriptionOf(matcher);
    }

    protected static class AnyOfValues<Item> extends AbstractAdaptedValue<Iterable<? extends Item>, Item> {

        private final LinkedList<ElementMatcher<Item>> previousSuccessful = new LinkedList<>();
        private final LinkedList<ElementMatcher<Item>> previousFailed = new LinkedList<>();
        
        public AnyOfValues(MatchValue<Iterable<? extends Item>> actualValue) {
            super(actualValue);
        }

        @Override
        protected Object createItem(Element<Iterable<? extends Item>> key) {
            return new AnyItemIterable<>(key.value());
        }

        @Override
        protected boolean matches(Element<Iterable<? extends Item>> element, ElementMatcher<Item> matcher) {
            AnyItemIterable<Item> it = cachedItem(element);
            E<Item> e = it.firstValid();
            if (e == null) return false;
            if (matcher.matches(e)) {
                previousSuccessful.add(matcher);
                return true;
            }
            previousFailed.add(matcher);
            e.mismatch = matcher;
            e = it.nextValid(e);
            while (e != null) {
                boolean match = matchAgainstPrevious(e);
                if (match) return true;
                e = it.nextValid(e);
            }
            return false;
        }
        
        protected boolean matchAgainstPrevious(E<Item> e) {
            for (ElementMatcher<Item> m: previousFailed) {
                if (!m.matches(e)) {
                    e.mismatch = m;
                    return false;
                }
            }
            Iterator<ElementMatcher<Item>> prev = previousSuccessful.iterator();
            while (prev.hasNext()) {
                ElementMatcher<Item> m = prev.next();
                if (!m.matches(e)) {
                    prev.remove();
                    previousFailed.add(m);
                    e.mismatch = m;
                    return false;
                }
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("any of ");
            getActualValue().describeTo(description);
        }

        @Override
        public void describeValueType(Description description) {
            description.appendText("any of ");
            getActualValue().describeValueType(description);
        }

        @Override
        protected void describeExpected(Element<Iterable<? extends Item>> element, ElementMatcher<Item> matcher, ExpectationDescription description) {
            AnyItemIterable<Item> it = cachedItem(element);
            E<Item> first = it.first();
            E<Item> e = first;
            while (e != null) {
                e.mismatch.describeExpected(e, description);
                e = it.next(e);
            }
        }

        @Override
        protected void describeMismatch(Element<Iterable<? extends Item>> element, ElementMatcher<Item> matcher, Description description) {
            AnyItemIterable<Item> it = cachedItem(element);
            E<Item> first = it.first();
            E<Item> e = first;
            while (e != null) {
                if (e != first) {
                    if (e.next == null) {
                        description.appendText(", and ");
                    } else {
                        description.appendText(", ");
                    }
                }
                description.appendText("#")
                       .appendText(String.valueOf(e.i))
                       .appendText(" ");
                e.mismatch.describeMismatch(e, description);
                e = it.next(e);
            }
        }
        
    }
    
    protected static class AnyItemIterable<Item> {
        
        private final Iterator<? extends Item> source;
        private E<Item> first = null;
        E<Item> firstValid = null;
        
        public AnyItemIterable(Iterable<? extends Item> iterable) {
            this.source = iterable.iterator();
        }
        
        public E<Item> firstValid() {
            if (firstValid == null) {
                if (first != null) {
                    // no valid elements
                    return null;
                }
                firstValid = first();
            }
            return firstValid;
        }
        
        public E<Item> nextValid(E<Item> e) {
            return firstValid = next(e);
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
        private final int i;
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
