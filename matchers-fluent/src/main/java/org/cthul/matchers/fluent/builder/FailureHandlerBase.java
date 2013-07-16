package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.values.MatchValue;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;

public abstract class FailureHandlerBase implements FailureHandler {

    protected String getMismatchString(String reason, MatchValue<?> actual) {
        Description description = new StringDescription();
        if (reason != null) {
            description.appendText(reason).appendText("\n");
        }
        description.appendText("Expected: ");
        actual.describeExpected(description);
        description.appendText("\n     but: ");
        actual.describeMismatch(description);
        String s = description.toString();
        return s;
    }

}
