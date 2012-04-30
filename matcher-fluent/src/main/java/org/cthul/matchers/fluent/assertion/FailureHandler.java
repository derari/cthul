package org.cthul.matchers.fluent.assertion;

import org.hamcrest.Matcher;

public interface FailureHandler {

    public static final AssertionErrorHandler ASSERT = AssertionErrorHandler.INSTANCE;

    void mismatch(String reason, Object item, Matcher<?> matcher);

}
