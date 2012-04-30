package org.cthul.matchers.fluent.assertion;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public class AssertionErrorHandler implements FailureHandler {

    public static final AssertionErrorHandler INSTANCE = new AssertionErrorHandler();

    public void mismatch(String reason, Object actual, Matcher<?> matcher) {
        Description description = new StringDescription();
        if (reason != null) {
            description.appendText(reason).appendText("\n");
        }
        description.appendText("Expected: ")
                   .appendDescriptionOf(matcher)
                   .appendText("\n     but: ");
        matcher.describeMismatch(actual, description);

        throw new AssertionError(description.toString());
    }

}
