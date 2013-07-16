package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.fluent.values.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.values.MatchValue;
import org.cthul.matchers.fluent.values.MatchValue.Element;
import org.cthul.matchers.fluent.values.MatchValue.ElementMatcher;
import org.hamcrest.Description;

/**
 * 
 */
public abstract class ConvertingAdapter<Value, Item> extends AbstractMatchValueAdapter<Value, Item> {

    @Override
    public MatchValue<Item> wrap(MatchValue<Value> v) {
        return new ConvertedMatchValue(v);
    }
    
    protected abstract Item getValue(Value v);
    
    protected abstract void describeExpected(Element<Value> value, Element<Item> item, ElementMatcher<Item> matcher, Description description);
    
    protected abstract void describeMismatch(Element<Value> value, Element<Item> item, ElementMatcher<Item> matcher, Description description);
    
    protected class ConvertedMatchValue extends AbstractAdaptedValue<Value, Item> {

        public ConvertedMatchValue(MatchValue<Value> actualValue) {
            super(actualValue);
        }

        @Override
        protected Object createItem(Element<Value> key) {
            Item i = getValue(key.value());
            return new Converted<>(i);
        }

        @Override
        protected boolean matches(Element<Value> element, ElementMatcher<Item> matcher) {
            return matcher.matches(cachedItem(element));
        }

        @Override
        protected void describeExpected(Element<Value> element, ElementMatcher<Item> matcher, Description description) {
            Element<Item> item = cachedItem(element);
            ConvertingAdapter.this.describeExpected(element, item, matcher, description);
        }

        @Override
        protected void describeMismatch(Element<Value> element, ElementMatcher<Item> matcher, Description description) {
            Element<Item> item = cachedItem(element);
            ConvertingAdapter.this.describeMismatch(element, item, matcher, description);
        }
    }
    
    protected static class Converted<Item> implements Element<Item> {
        
        private final Item item;

        public Converted(Item item) {
            this.item = item;
        }

        @Override
        public Item value() {
            return item;
        }
    }
}
