package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.diagnose.NestedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class FNot<T> extends NestedMatcher<T> {

    private final String prefix;
    private final Matcher<? super T> nested;
    private int p = -1;
    
    public FNot(Matcher<? super T> nested) {
        this(null, nested);
    }

    public FNot(String prefix, Matcher<? super T> nested) {
        this.prefix = prefix;
        this.nested = nested;
    }
    
    @Override
    public boolean matches(Object o, Description d) {
        if (nested.matches(o)) {
            FIs.pastPrefix(prefix, d);
            d.appendDescriptionOf(nested);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean matches(Object o) {
        return !nested.matches(o);
    }

    @Override
    public void describeTo(Description description) {
        if (prefix != null) {
            description.appendText(prefix).appendText(" ");
        }
        description.appendText("not ");
        description.appendDescriptionOf(nested);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendValue(item).appendText(" ");
        FIs.pastPrefix(prefix, description);
        description.appendDescriptionOf(nested);
    }

    @Override
    public int getPrecedence() {
        if (p < 0) p = precedenceOf(nested);
        return p;
    }
    
}
