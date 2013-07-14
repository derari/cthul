package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.fluent.values.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.values.AbstractMatchValue;
import org.cthul.matchers.fluent.values.MatchValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * 
 */
public abstract class ConvertingAdapter<Value, Item> extends AbstractMatchValueAdapter<Value, Item> {
    
    protected abstract Item getValue(Value v);

    @Override
    public MatchValue<Item> adapt(Value v) {
        return new SingleMatchValue(v);
    }
    
    protected abstract void describeMismatch(Matcher<?> matcher, Value value, Item item, Description description);
    
    protected class SingleMatchValue extends AbstractMatchValue<Item> {
    
        private final Value value;
        private final Item item;

        public SingleMatchValue(Value value) {
            this.value = value;
            this.item = getValue(value);
        }

        @Override
        public boolean matches(Matcher<? super Item> matcher) {
            return matcher.matches(item);
        }

        @Override
        public boolean matches(Matcher<? super Item> matcher, Description mismatch) {
            return QuickDiagnose.matches(matcher, item, mismatch);
        }

        @Override
        public void describeExpected(Matcher<? super Item> matcher, Description description) {
            ConvertingAdapter.this.describeTo(matcher, description);
        }

        @Override
        public void describeMismatch(Matcher<? super Item> matcher, Description description) {
            ConvertingAdapter.this.describeMismatch(matcher, value, item, description);
        }

    }

}
