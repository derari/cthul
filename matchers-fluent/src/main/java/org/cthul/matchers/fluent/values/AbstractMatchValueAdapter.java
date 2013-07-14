package org.cthul.matchers.fluent.values;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class AbstractMatchValueAdapter<Value, Item> implements MatchValueAdapter<Value, Item> {

    @Override
    public MatchValue<Item> wrap(MatchValue<Value> v) {
        return new WrappedValue<>(this, v);
    }
    
    protected static class WrappedValue<Value, Item> extends AbstractMatchValue<Item> {

        private final AbstractMatchValueAdapter<Value, Item> adapter;
        private final MatchValue<Value> actual;
        private Matcher<? super Item> lastMatcher = null;
        private AdaptingMatcher<Value, Item> lastAdapted = null;

        public WrappedValue(AbstractMatchValueAdapter<Value, Item> adapter, MatchValue<Value> actual) {
            this.adapter = adapter;
            this.actual = actual;
        }

        @Override
        public boolean matches(Matcher<? super Item> matcher) {
            return actual.matches(adapt(matcher));
        }

        @Override
        public boolean matches(Matcher<? super Item> matcher, Description mismatch) {
            return actual.matches(adapt(matcher), mismatch);
        }

        @Override
        public void describeExpected(Matcher<? super Item> matcher, Description description) {
            actual.describeExpected(adapt(matcher), description);
        }

        @Override
        public void describeMismatch(Matcher<? super Item> matcher, Description mismatch) {
            actual.describeMismatch(adapt(matcher), mismatch);
        }

        protected Matcher<Value> adapt(Matcher<? super Item> matcher) {
            if (lastMatcher != matcher) {
                return lastAdapted = new AdaptingMatcher<>(adapter, matcher);
            }
            return lastAdapted;
        }
        
    }
    
}
