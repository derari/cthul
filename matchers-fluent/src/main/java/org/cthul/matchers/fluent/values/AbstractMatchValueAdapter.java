package org.cthul.matchers.fluent.values;

import java.util.HashMap;
import java.util.Map;
import org.cthul.matchers.fluent.values.MatchValue.Element;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class AbstractMatchValueAdapter<Value, Item> implements MatchValueAdapter<Value, Item> {

    @Override
    public MatchValue<Item> adapt(Value v) {
        SingleElement<Value> e = new SingleElement<>(v);
        return adapt(e, e);
    }
    
    @Override
    public MatchValue<Item> wrap(MatchValue<Value> v) {
        return new WrappedValue<>(this, v);
    }
    
    protected abstract MatchValue<Item> adapt(MatchValue<Value> source, Element<Value> element);
    
    protected static class AdaptedValue<Value, Item> extends AbstractMatchValue<Item> {
        
        private final MatchValue<Value> source;
        private final Element<Value> element;
        private final Map<Element<Value>, AdaptedValue<Value, Item>> adaptedValues;

        public AdaptedValue(MatchValue<Value> source, Element<Value> element) {
            this(source, element, new HashMap<Element<Value>, AdaptedValue<Value, Item>>());
        }

        @SuppressWarnings("LeakingThisInConstructor")
        public AdaptedValue(MatchValue<Value> source, Element<Value> element, Map<Element<Value>, AdaptedValue<Value, Item>> adaptedValues) {
            this.source = source;
            this.element = element;
            this.adaptedValues = adaptedValues;
            adaptedValues.put(element, this);
        }

        @Override
        public void describeExpected(Matcher<? super Item> matcher, Description description) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void describeMismatch(Matcher<? super Item> matcher, Description description) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Element<Item> elements() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean matched() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    
    
    /**
     * Wraps an adapted MatchValue to flatten the elements iterator.
     * <p>
     * This part is a bit ugly. The idea is to cache all underlying MatchValues
     * and elements to ensure their state is preserved.
     * 
     * @param <Value>
     * @param <Item> 
     */
    protected static class WrappedValue<Value, Item> extends AbstractMatchValue<Item> {

        private final AbstractMatchValueAdapter<Value, Item> adapter;
        private final MatchValue<Value> actualValue;
        private final Map<Element<Value>, AdaptedValue<Value, Item>> adaptedValues = new HashMap<>();

        public WrappedValue(AbstractMatchValueAdapter<Value, Item> adapter, MatchValue<Value> actual) {
            this.adapter = adapter;
            this.actualValue = actual;
        }

        @Override
        public void describeExpected(Matcher<? super Item> matcher, Description description) {
            actualValue.describeExpected(adapt(matcher), description);
        }

        @Override
        public void describeMismatch(Matcher<? super Item> matcher, Description mismatch) {
            actualValue.describeMismatch(adapt(matcher), mismatch);
        }

        protected Matcher<Value> adapt(Matcher<? super Item> matcher) {
            return new AdaptingMatcher<>(adapter, matcher);
        }

        @Override
        public boolean matched() {
            return actualValue.matched();
        }
        
        @Override
        public Element<Item> elements() {
            Element<Value> e = actualValue.elements();
            return valueFor(e).first();
        }

        protected AdaptedValue<Value, Item> valueFor(Element<Value> e) {
            AdaptedValue<Value, Item> v = adaptedValues.get(e);
            if (v == null) {
                v = new AdaptedValue(this, e, adapter.adapt(e.value()));
                adaptedValues.put(e, v);
            }
            return v;
        }
    }
    
    protected static class AdaptedValue<Value, Item> {
        
        private final WrappedValue<Value, Item> wrappedValue;
        private final Element<Value> source;
        private final MatchValue<Item> adaptedValue;
        private final Map<Element<Item>, WrappedElement<Item>> wrappedElements = new HashMap<>();

        public AdaptedValue(WrappedValue<Value, Item> wrappedValue, Element<Value> source, MatchValue<Item> adaptedValue) {
            this.wrappedValue = wrappedValue;
            this.source = source;
            this.adaptedValue = adaptedValue;
        }
        
        public WrappedElement<Item> first() {
            return get(adaptedValue.elements());
        }
        
        public WrappedElement<Item> get(Element<Item> e) {
            WrappedElement<Item> w = wrappedElements.get(e);
            if (w == null) {
                w = new WrappedElement<>(this, e);
                wrappedElements.put(e, w);
            }
            return w;
        }
        
        public WrappedElement<Item> nextForSource() {
            Element<Value> nextSource = source.next();
            if (nextSource == null) return null;
            return wrappedValue.valueFor(nextSource).first();
        }
    }
    
    protected static class WrappedElement<Item> implements Element<Item> {
        
        private final AdaptedValue<?, Item> adapter;
        private final Element<Item> element;

        public WrappedElement(AdaptedValue<?, Item> adapter, Element<Item> element) {
            this.adapter = adapter;
            this.element = element;
        }

        @Override
        public Item value() {
            return element.value();
        }

        @Override
        public void success() {
            element.success();
        }

        @Override
        public void fail() {
            element.fail();
        }

        @Override
        public void result(boolean match) {
            element.result(match);
        }

        @Override
        public WrappedElement<Item> next() {
            Element<Item> next = element.next();
            if (next == null) {
                return adapter.nextForSource();
            }
            return adapter.get(next);
        }
        
    }
    
    protected static class SingleElement<Value> 
                    extends AbstractMatchValue<Value>
                    implements Element<Value> {

        private final Value value;
        private boolean matched = true;

        public SingleElement(Value value) {
            this.value = value;
        }
        
        @Override
        public Value value() {
            return value;
        }

        @Override
        public void success() {
        }

        @Override
        public void fail() {
            matched = false;
        }

        @Override
        public void result(boolean match) {
            matched &= match;
        }

        @Override
        public Element<Value> next() {
            return null;
        }

        @Override
        public void describeExpected(Matcher<? super Value> matcher, Description description) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void describeMismatch(Matcher<? super Value> matcher, Description description) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Element<Value> elements() {
            return this;
        }

        @Override
        public boolean matched() {
            return matched;
        }
    }
    
}
