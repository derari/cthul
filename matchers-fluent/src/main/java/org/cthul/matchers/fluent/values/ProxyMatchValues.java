package org.cthul.matchers.fluent.values;

import org.cthul.matchers.diagnose.NestedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 * @author Arian Treffer
 */
public abstract class ProxyMatchValues<Value, Item> implements MatchValues<Item> {

    protected final MatchValues<Value> actual;

    public ProxyMatchValues(MatchValues<Value> actual) {
        this.actual = actual;
    }
    
    protected abstract Item convert(Value v);
    
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
        actual.describeMismatch(new MismatchProxy(matcher), mismatch);
    }
    
    protected class MismatchProxy extends NestedMatcher<Value> {
        
        private final Matcher<? super Item> nested;

        public MismatchProxy(Matcher<? super Item> nested) {
            this.nested = nested;
        }

        @Override
        public boolean matches(Object item, Description mismatch) {
            return nestedQuickMatch(nested, convert((Value) item), mismatch);
        }

        @Override
        public boolean matches(Object o) {
            return nested.matches(convert((Value) o));
        }

        @Override
        public void describeTo(Description description) {
            nested.describeTo(description);
        }

        @Override
        public int getPrecedence() {
            return precedenceOf(nested);
        }
        
    }
}
