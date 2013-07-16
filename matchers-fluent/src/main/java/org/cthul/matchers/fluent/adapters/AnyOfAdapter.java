package org.cthul.matchers.fluent.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
        return INSTANCE.wrap(mo);
    }
    
    @Factory
    public static <I> AnyOfAdapter<I> any(Class<I> c) {
        return INSTANCE;
    }
    
    @Factory
    public static <I> MatchValue<I> any(Iterable<? extends I> iterable) {
        return INSTANCE.adapt(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> any(I... array) {
        return anyOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> any(MatchValue<? extends Iterable<? extends I>> mo) {
        return INSTANCE.wrap(mo);
    }
    
    @Factory
    public static AnyOfAdapter<Integer> anyInt() {
        return INSTANCE;
    }

    @Override
    public MatchValue<Item> wrap(MatchValue<Iterable<? extends Item>> v) {
        return new AnyOfValues<>(v);
    }

    @Override
    public void describeTo(Matcher<?> matcher, Description description) {
        description.appendText("any ");
        description.appendDescriptionOf(matcher);
    }

    protected static class AnyOfValues<Item> extends AbstractAdaptedValue<Iterable<? extends Item>, Item> {

        private final List<ElementMatcher<Item>> previousMatchers = new ArrayList<>();
        
        public AnyOfValues(MatchValue<Iterable<? extends Item>> actualValue) {
            super(actualValue);
        }

        @Override
        protected Object createItem(Element<Iterable<? extends Item>> key) {
            return new AnyItemIterable<>(key.value());
        }

        @Override
        public boolean matches(ElementMatcher<Item> matcher) {
            previousMatchers.add(matcher);
            return super.matches(matcher); 
        }

        @Override
        protected boolean matches(Element<Iterable<? extends Item>> element, ElementMatcher<Item> matcher) {
            AnyItemIterable<Item> it = cachedItem(element);
            E<Item> e = it.firstValid();
            if (e == null) return false;
            if (matcher.matches(e)) return true;
            e.mismatch = matcher;
            e = it.nextValid(e);
            while (e != null) {
                boolean match = true;
                for (ElementMatcher<Item> m: previousMatchers) {
                    if (!m.matches(e)) {
                        e.mismatch = m;
                        match = false;
                        break;
                    }
                }
                if (match) return true;
                e = it.nextValid(e);
            }
            return false;
        }

        @Override
        protected void describeExpected(Element<Iterable<? extends Item>> element, ElementMatcher<Item> matcher, Description description) {
            AnyItemIterable<Item> it = cachedItem(element);
            E<Item> first = it.first();
            E<Item> e = first;
            while (e != null) {
//                if (e != first) {
//                    description.appendText(", ");
//                }
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
                    description.appendText(", ");
                }
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
                first = new E<>(source.next());
            }
            return first;
        }
        
        public E<Item> next(E<Item> e) {
            if (e.next == null) {
                if (!source.hasNext()) return null;
                e.next = new E<>(source.next());
            }
            return e.next;
        }
    }
    
    protected static class E<Item> implements Element<Item> {
        
        private final Item value;
        ElementMatcher<Item> mismatch = null;
        E<Item> next = null;

        public E(Item value) {
            this.value = value;
        }

        @Override
        public Item value() {
            return value;
        }
    }
}
