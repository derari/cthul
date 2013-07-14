package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.values.MatchValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public class AssertionErrorHandler implements FailureHandler {

    public static final AssertionErrorHandler INSTANCE = new AssertionErrorHandler();

    @Override
    public <T> void mismatch(String reason, MatchValue<T> actual, Matcher<? super T> matcher) {
        Description description = new StringDescription();
        if (reason != null) {
            description.appendText(reason).appendText("\n");
        }
        description.appendText("Expected: ");
        actual.describeExpected(matcher, description);
        description.appendText("\n     but: ");
        actual.describeMismatch(matcher, description);

        throw new AssertionError(description.toString());
    }

}
