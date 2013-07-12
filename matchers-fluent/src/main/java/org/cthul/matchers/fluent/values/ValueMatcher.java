package org.cthul.matchers.fluent.values;

import org.cthul.matchers.diagnose.NestedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * A hamcrest matcher that uses {@link MatchValueType} to match the value
 * against another matcher.
 * <p>
 * Example: ´length-of-string is less than 3´.matches("xa")
 */
public class ValueMatcher<Value, Item> extends NestedMatcher<Value> {
    
    private final Matcher<? super Item> nested;
    private final MatchValueType<Value, Item> valueType;

    public ValueMatcher(Matcher<? super Item> nested, MatchValueType<Value, Item> valueType) {
        this.nested = nested;
        this.valueType = valueType;
    }

    @Override
    public boolean matches(Object item, Description mismatch) {
        Value v = (Value) item;
        return valueType.matchingObject(v).validate(nested, mismatch);
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
