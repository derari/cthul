package org.cthul.matchers.fluent.values.types;

import org.cthul.matchers.fluent.values.AbstractMatchValues;
import org.cthul.matchers.fluent.values.AbstractMatchingObject;
import org.cthul.matchers.fluent.values.MatchValueType;
import org.cthul.matchers.fluent.values.MatchValues;
import org.cthul.matchers.fluent.values.MatchingObject;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * 
 */
public abstract class ConvertingValueType<Value, Item> implements MatchValueType<Value, Item> {
    
    protected abstract Item convert(Value v);

    @Override
    public MatchingObject<Item> matchingObject(Value v) {
        return new ValueObject<>(convert(v));
    }

    @Override
    public MatchingObject<Item> adapt(MatchingObject<Value> v) {
        return new ProxyObject<>(v, this);
    }
    
    protected static class ValueObject<Item> extends AbstractMatchingObject<Item> {
        private final Item item;

        public ValueObject(Item item) {
            this.item = item;
        }
        
        @Override
        public MatchValues<Item> values() {
            return new SingleMatchValue<>(item);
        }
    }
    
    protected static class SingleMatchValue<Item> extends AbstractMatchValues<Item> {
    
        private final Item value;
        private boolean next = true;
        private boolean success = true;

        public SingleMatchValue(Item value) {
            this.value = value;
        }

        @Override
        public boolean hasNext() {
            return next;
        }

        @Override
        public Item next() {
            next = false;
            return value;
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
        public void describeMismatch(Matcher<? super Item> matcher, Description mismatch) {
            matcher.describeMismatch(value, mismatch);
        }

    }
    
    protected static class ProxyObject<Value, Item> extends AbstractMatchingObject<Item> {
        
        private final MatchingObject<Value> actual;
        private final ConvertingValueType<Value, Item> valueType;

        public ProxyObject(MatchingObject<Value> actual, ConvertingValueType<Value, Item> valueType) {
            this.actual = actual;
            this.valueType = valueType;
        }

        @Override
        public MatchValues<Item> values() {
            return new ProxyValues<>(valueType, actual.values());
        }
        
    }
    
    protected static class ProxyValues<Value, Item> extends AbstractMatchValues<Item> {

        private final ConvertingValueType<Value, Item> valueType;
        private final MatchValues<Value> actual;

        public ProxyValues(ConvertingValueType<Value, Item> valueType, MatchValues<Value> actual) {
            this.actual = actual;
            this.valueType = valueType;
        }
        
        protected Item convert(Value v) {
            return valueType.convert(v);
        }
        
        @Override
        public boolean hasNext() {
            return actual.hasNext();
        }

        @Override
        public Item next() {
            return convert(actual.next());
        }

        @Override
        public void success() {
            actual.success();
        }

        @Override
        public void fail() {
            actual.fail();
        }

        @Override
        public void result(boolean success) {
            actual.result(success);
        }

        @Override
        public boolean matched() {
            return actual.matched();
        }

        @Override
        public void describeMismatch(Matcher<? super Item> matcher, Description mismatch) {
            // TODO
        }
    }
    
}
