package org.cthul.matchers.fluent.adapters;

import java.util.Iterator;
import org.cthul.matchers.fluent.value.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValue.Element;
import org.cthul.matchers.fluent.value.MatchValue.ElementMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class SimpleEachOfAdapter<Value, Item> extends 
                AbstractMatchValueAdapter<Value, Item> {

    protected final String name;

    public SimpleEachOfAdapter() {
        this(null);
    }

    public SimpleEachOfAdapter(String name) {
        this.name = name;
    }
    
    @Override
    public MatchValue<Item> adapt(MatchValue<Value> v) {
        return new EachOfValues(v);
    }

    @Override
    public void describeMatcher(Matcher<?> matcher, Description description) {
        description.appendText("each ");
        if (name != null) {
            description.appendText(name).appendText(" ");
        }
        description.appendDescriptionOf(matcher);
    }
    
    protected abstract Iterable<? extends Item> getElements(Value value);
    
    protected void describeElement(Value owner, Item element, int index, Description description) {
        if (name != null) {
            description.appendText(name).appendText(" ");
        } else {
            description.appendText("#");
        }
        description.appendText(String.valueOf(index))
                   .appendText(" ");
    }

    protected class EachOfValues extends AbstractAdaptedValue<Value, Item> {

        public EachOfValues(MatchValue<Value> actualValue) {
            super(actualValue);
        }

        @Override
        protected Object createItem(Element<Value> key) {
            return new EachItemIterable<>(getElements(key.value()));
        }

        @Override
        protected boolean matches(Element<Value> element, ElementMatcher<Item> matcher) {
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
            describeSelfOf(description);
            getActualValue().describeTo(description);
        }

        @Override
        public void describeValueType(Description description) {
            describeSelfOf(description);
            getActualValue().describeValueType(description);
        }
        
        protected void describeSelfOf(Description description) {
            description.appendText("each ");
            if (name != null) {
                description.appendText(name).appendText(" ");
            }
            description.appendText("of ");
        }

        @Override
        protected void describeExpected(Element<Value> element, ElementMatcher<Item> matcher, ExpectationDescription description) {
            EachItemIterable<Item> it = cachedItem(element);
            E<Item> e = it.invalid;
//          do we even care?
//            assert e.mismatch == matcher : 
//                    "The nested value should complain only about the matcher "
//                    + "that caused this one to fail";
            matcher.describeExpected(e, description);
        }

        @Override
        protected void describeMismatch(Element<Value> element, ElementMatcher<Item> matcher, Description description) {
            EachItemIterable<Item> it = cachedItem(element);
            E<Item> e = it.invalid;
//          do we even care?            
//            assert e.mismatch == matcher : 
//                    "The nested value should complain only about the matcher "
//                    + "that caused this one to fail";            
            describeElement(element.value(), e.value(), e.i, description);
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
