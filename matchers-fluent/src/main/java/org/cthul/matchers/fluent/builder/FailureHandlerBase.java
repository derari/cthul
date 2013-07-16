package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.value.Expectation;
import java.util.LinkedHashSet;
import java.util.Set;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValue.ExpectationDescription;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

public abstract class FailureHandlerBase implements FailureHandler {

    protected String getMismatchString(String reason, MatchValue<?> actual) {
        Description description = new StringDescription();
        if (reason != null) {
            description.appendText(reason).appendText("\n");
        } else {
//            actual.describeTo(description).appendText("\n");
        }
        description.appendText("Expected ");
        actual.describeValueType(description);
        description.appendText(" ");
        Expectation e = new Expectation();
        actual.describeExpected(e);
        description.appendDescriptionOf(e);
        
        description.appendText("\n      but ");
        actual.describeMismatch(description);
        
        return description.toString();
    }
    
}
