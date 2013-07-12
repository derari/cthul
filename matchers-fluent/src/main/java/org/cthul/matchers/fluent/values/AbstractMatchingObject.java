package org.cthul.matchers.fluent.values;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class AbstractMatchingObject<T> implements MatchingObject<T> {

    @Override
    public boolean validate(Matcher<? super T> matcher) {
        MatchValues<T> values = values();
        while (values.hasNext()) {
            boolean match = matcher.matches(values.next());
            values.result(match);
        }
        return values.matched();
    }

    @Override
    public boolean validate(Matcher<? super T> matcher, Description mismatch) {
        MatchValues<T> values = values();
        while (values.hasNext()) {
            boolean match = matcher.matches(values.next());
            values.result(match);
        }
        if (values.matched()) {
            return true;
        } else {
            values.describeMismatch(matcher, mismatch);
            return false;
        }
    }
    
}
