package org.cthul.matchers.fluent.values;

import org.cthul.matchers.diagnose.NestedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 * @author Arian Treffer
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
        MatchValues<Item> values = valueType.matchingObject(v).values();
        while (values.hasNext()) {
            boolean match = nested.matches(values.next());
            values.result(match);
        }
        if (values.matched()) {
            return true;
        } else {
            describeMismatch(item, mismatch);
            return false;
        }
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
