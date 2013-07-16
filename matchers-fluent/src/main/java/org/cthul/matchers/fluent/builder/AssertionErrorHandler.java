package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValue.ElementMatcher;

public class AssertionErrorHandler extends FailureHandlerBase {

    public static final AssertionErrorHandler INSTANCE = new AssertionErrorHandler();

    @Override
    public <T> void mismatch(String reason, MatchValue<T> actual, ElementMatcher<T> matcher) {
        throw new AssertionError(getMismatchString(reason, actual));
    }

}
