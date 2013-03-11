package org.cthul.matchers.fluent.base;

import org.cthul.matchers.diagnose.NestedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class FNot<T> extends NestedMatcher<T> {

    private final boolean prependIS;
    private final Matcher<? super T> nested;
    private int p = -1;
    
    public FNot(Matcher<? super T> nested) {
        this(false, nested);
    }

    public FNot(boolean prependIs, Matcher<? super T> nested) {
        this.prependIS = prependIs;
        this.nested = nested;
    }
    
    @Override
    public boolean matches(Object o, Description d) {
        if (nested.matches(o)) {
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
        if (prependIS)
            description.appendText("is ");
        description.appendText("not ");
        nested.describeTo(description);
    }

    @Override
    public int getPrecedence() {
        if (p < 0) p = precedenceOf(nested);
        return p;
    }
    
}
