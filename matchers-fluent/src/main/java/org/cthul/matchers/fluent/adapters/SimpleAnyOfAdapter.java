package org.cthul.matchers.fluent.adapters;

import java.util.Iterator;
import java.util.LinkedList;
import org.cthul.matchers.fluent.value.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValue.Element;
import org.cthul.matchers.fluent.value.MatchValue.ElementMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class SimpleAnyOfAdapter<Value, Item> extends 
                AbstractMatchValueAdapter<Value, Item> {

    private final String name;

    public SimpleAnyOfAdapter() {
        name = null;
    }

    public SimpleAnyOfAdapter(String name) {
        this.name = name;
    }
    
    @Override
    public MatchValue<Item> adapt(MatchValue<Value> v) {
        return new AnyOfValues(v);
    }

    @Override
    public void describeMatcher(Matcher<?> matcher, Description description) {
        description.appendText("any ");
        if (name != null) {
            description.appendText(name).appendText(" ");
        }
        description.appendDescriptionOf(matcher);
    }
    
    protected abstract Iterable<? extends Item> getElements(Value value);
    
    protected void describeElement(Value owner, Item element, int index, Description description) {
//        // since the any-adapter mismatch prints every item anyway,
//        // print the name only once and don't print indices
//        if (index == 0 && name != null) {
//            description.appendText(name).appendText(" ");
//        }
        
        if (name != null) {
            description.appendText(name).appendText(" ");
        } else {
            description.appendText("#");
        }
        description.appendText(String.valueOf(index))
                   .appendText(" ");
    }

    protected class AnyOfValues extends AbstractAdaptedValue<Value, Item> {

        private final LinkedList<ElementMatcher<Item>> previousSuccessful = new LinkedList<>();
        private final LinkedList<ElementMatcher<Item>> previousFailed = new LinkedList<>();

        public AnyOfValues(MatchValue<Value> actualValue) {
            super(actualValue);
        }

        @Override
        protected Object createItem(Element<Value> key) {
            return new AnyItemIterable<>(getElements(key.value()));
        }

        @Override
        protected boolean matches(Element<Value> element, ElementMatcher<Item> matcher) {
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
            describeSelfOf(description);
            getActualValue().describeTo(description);
        }

        @Override
        public void describeValueType(Description description) {
            describeSelfOf(description);
            getActualValue().describeValueType(description);
        }

        protected void describeSelfOf(Description description) {
            description.appendText("any ");
            if (name != null) {
                description.appendText(name).appendText(" ");
            }
            description.appendText("of ");
        }
        
        @Override
        protected void describeExpected(Element<Value> element, ElementMatcher<Item> matcher, ExpectationDescription description) {
            AnyItemIterable<Item> it = cachedItem(element);
            E<Item> first = it.first();
            E<Item> e = first;
            while (e != null) {
                e.mismatch.describeExpected(e, description);
                e = it.next(e);
            }
        }

        @Override
        protected void describeMismatch(Element<Value> element, ElementMatcher<Item> matcher, Description description) {
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
                describeElement(element.value(), e.value(), e.i, description);
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
