package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.builder.FluentBuilder;
import org.hamcrest.Matcher;
import org.cthul.matchers.fluent.strategy.MatchingObject;
import org.cthul.matchers.fluent.assertion.FluentAssertBuilder;


public class CoreFluents extends AssertUtils {

    public static <T> FluentAssert<T> assertThat(T object) {
        return new FluentAssertBuilder<>(ASSERT, object);
    }

    public static <T> FluentAssert<T> assertThat(MatchingObject<T> object) {
        return new FluentAssertBuilder<>(ASSERT, object);
    }

    public static <T> FluentAssert<T> assertThat(String reason, T object) {
        return assertThat(object).as(reason);
    }

    public static <T> FluentAssert<T> assertThat(String reason, MatchingObject<T> object) {
        return new FluentAssertBuilder<>(ASSERT, object).as(reason);
    }

    public static <T> FluentMatcher<T> matcher() {
        return new FluentBuilder<>();
    }

    public static <T> FluentMatcher<T> is() {
        return matcher();
    }

    public static <T> FluentMatcher<T> is(Matcher<T> matcher) {
        return CoreFluents.<T>matcher().is(matcher);
    }

    public static <T> FluentMatcher<T> either() {
        return CoreFluents.<T>matcher().either();
    }

    public static <T> FluentMatcher<T> either(Matcher<T> matcher) {
        return CoreFluents.<T>matcher().either(matcher);
    }

}
