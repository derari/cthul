package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.values.MatchValues;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public class AssertionErrorHandler implements FailureHandler {

    public static final AssertionErrorHandler INSTANCE = new AssertionErrorHandler();

    @Override
    public <T> void mismatch(String reason, MatchValues<T> actual, Matcher<? super T> matcher) {
        Description description = new StringDescription();
        if (reason != null) {
            description.appendText(reason).appendText("\n");
        }
        description.appendText("Expected: ")
                   .appendDescriptionOf(matcher)
                   .appendText("\n     but: ");
        actual.describeMismatch(matcher, description);

        throw new AssertionError(description.toString());
    }

}
