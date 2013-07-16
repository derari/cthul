package org.cthul.matchers.fluent.values;

import org.cthul.matchers.diagnose.NestedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * A hamcrest matcher that uses {@link MatchValueAdapter} to match the value
 * against another matcher.
 * <p>
 * Example: ´length-of-string is less than 3´.matches("xa")
 */
public class AdaptingMatcher<Value, Item> extends NestedMatcher<Value> {
    
    private final ElementMatcher<Item> matcher;
    private final MatchValueAdapter<Value, Item> adapter;

    public AdaptingMatcher(MatchValueAdapter<Value, Item> adapter, Matcher<? super Item> matcher) {
        this.adapter = adapter;
        this.matcher = new ElementMatcher<>(matcher);
    }

    @Override
    public boolean matches(Object item) {
        Value v = (Value) item;
        return adapter.adapt(v).matches(matcher);
    }

    @Override
    public boolean matches(Object item, Description mismatch) {
        Value v = (Value) item;
        MatchValue<Item> mv = adapter.adapt(v);
        if (mv.matches(matcher)) {
            return true;
        }
        mv.describeMismatch(mismatch);
        return false;
    }

    @Override
    public void describeTo(Description description) {
        adapter.describeTo(matcher, description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        matches(item, description);
    }
    
    @Override
    public int getPrecedence() {
        return precedenceOf(matcher);
    }
}
