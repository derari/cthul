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
public abstract class IterableValueType<Value, Item> implements MatchValueType<Value, Item> {
    
    protected abstract MatchValues<Item> convert(Value v);

    @Override
    public MatchingObject<Item> matchingObject(Value v) {
        return new ValueObject<>(v, this);
    }

    @Override
    public MatchingObject<Item> adapt(MatchingObject<Value> v) {
        return new ProxyObject<>(v, this);
    }
    
    public static class ValueObject<Value, Item> extends AbstractMatchingObject<Item> {
        private final Value item;
        private final IterableValueType<Value, Item> valueType;

        public ValueObject(Value item, IterableValueType<Value, Item> valueType) {
            this.item = item;
            this.valueType = valueType;
        }

        @Override
        public MatchValues<Item> values() {
            return valueType.convert(item);
        }
    }
    
    protected static class ProxyObject<Value, Item> extends AbstractMatchingObject<Item> {
        
        private final MatchingObject<Value> actual;
        private final IterableValueType<Value, Item> valueType;

        public ProxyObject(MatchingObject<Value> actual, IterableValueType<Value, Item> valueType) {
            this.actual = actual;
            this.valueType = valueType;
        }

        @Override
        public MatchValues<Item> values() {
            return new ProxyValues<>(valueType, actual.values());
        }
        
    }
    
    protected static class ProxyValues<Value, Item> extends AbstractMatchValues<Item> {

        private final IterableValueType<Value, Item> valueType;
        private final MatchValues<Value> actual;
        private MatchValues<Item> current;

        public ProxyValues(IterableValueType<Value, Item> valueType, MatchValues<Value> actual) {
            this.valueType = valueType;
            this.actual = actual;
        }
        
        protected boolean currentHasNext() {
            while (current == null || !current.hasNext()) {
                if (current != null) {
                    actual.result(current.matched());
                }
                if (!actual.hasNext()) return false;
            }
            return true;
        }

        @Override
        public boolean hasNext() {
            return currentHasNext();
        }

        @Override
        public Item next() {
            if (!currentHasNext()) {
                throw new IllegalStateException();
            }
            return current.next();
        }

        @Override
        public void success() {
            current.success();
        }

        @Override
        public void fail() {
            current.fail();
        }

        @Override
        public void result(boolean success) {
            current.result(success);
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
