package org.cthul.matchers.fluent.base;

import org.cthul.matchers.diagnose.NestedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class FIs<T> extends NestedMatcher<T> {

    private final Matcher<? super T> nested;
    private int p = -1;

    public FIs(Matcher<? super T> nested) {
        this.nested = nested;
    }
    
    @Override
    public boolean matches(Object o, Description d) {
        return quickMatch(nested, o, d);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is ");
        nested.describeTo(description);
    }

    @Override
    public int getPrecedence() {
        if (p < 0) p = precedenceOf(nested);
        return p;
    }
    
}
