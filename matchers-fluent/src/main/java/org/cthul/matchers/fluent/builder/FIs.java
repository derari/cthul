package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.diagnose.NestedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class FIs<T> extends NestedMatcher<T> {

    private final Matcher<? super T> nested;
    private final String prefix;
    private int p = -1;

    public FIs(String prefix, Matcher<? super T> nested) {
        this.nested = nested;
        this.prefix = prefix;
    }

    public FIs(Matcher<? super T> nested) {
        this("is", nested);
    }
    
    @Override
    public boolean matches(Object o, Description d) {
        return quickMatch(nested, o, d);
    }

    @Override
    public void describeTo(Description description) {
        if (prefix != null) {
            description.appendText(prefix).appendText(" ");
        }
        nested.describeTo(description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
//        pastPrefix(prefix, description);
        nested.describeMismatch(item, description);
    }

    @Override
    public int getPrecedence() {
        if (p < 0) p = precedenceOf(nested);
        return p;
    }

    static void pastPrefix(String prefix, Description description) {
        if (prefix != null) {
            switch (prefix) {
                case "is":
                    description.appendText("was ");
                    break;
                case "has":
                    description.appendText("had ");
                    break;
                default:
                    description.appendText(prefix).appendText(" ");
            }
        }
    }
    
}
