package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValue.ElementMatcher;

public interface FailureHandler {

    public static final AssertionErrorHandler ASSERT = AssertionErrorHandler.INSTANCE;

    <T> void mismatch(String reason, MatchValue<T> item, ElementMatcher<T> matcher);

}
