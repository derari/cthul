package org.cthul.matchers.fluent.values;

/**
 *
 * @author Arian Treffer
 */
public abstract class SimpleValueType<Value, Item> implements MatchValueType<Value, Item> {
    
    protected abstract Item convert(Value v);

    @Override
    public MatchingObject<Item> matchingObject(Value v) {
        return new ValueObject<>(convert(v));
    }

    @Override
    public MatchingObject<Item> cast(MatchingObject<Value> v) {
        return new ProxyObject<>(v, this);
    }
    
    public static class ValueObject<Item> implements MatchingObject<Item> {
        private final Item item;

        public ValueObject(Item item) {
            this.item = item;
        }
        
        @Override
        public MatchValues<Item> values() {
            return new SingleMatchValue<>(item);
        }
    }
    
    public static class ProxyObject<Value, Item> implements MatchingObject<Item> {
        
        private final MatchingObject<Value> actual;
        private final SimpleValueType<Value, Item> valueType;

        public ProxyObject(MatchingObject<Value> actual, SimpleValueType<Value, Item> valueType) {
            this.actual = actual;
            this.valueType = valueType;
        }

        @Override
        public MatchValues<Item> values() {
            return new ProxyValues<>(valueType, actual.values());
        }
        
    }
    
    public static class ProxyValues<Value, Item> extends ProxyMatchValues<Value, Item> {

        private final SimpleValueType<Value, Item> valueType;

        public ProxyValues(SimpleValueType<Value, Item> valueType, MatchValues<Value> actual) {
            super(actual);
            this.valueType = valueType;
        }
        
        @Override
        protected Item convert(Value v) {
            return valueType.convert(v);
        }
        
    }
    
}
