package org.cthul.matchers.fluent.custom;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.assertion.AssertionStrategy;
import org.cthul.matchers.fluent.FluentMatcher;

/**
 *
 * @author derari
 */
public class CustomFluents extends CustomFluentsBase {
    
    public static <T, F extends FluentAssert<T>> F assertThat(Class<F> clazz, T object) {
        return fluentAssert(ASSERT, object, clazz);
    }

    public static <T, F extends FluentAssert<T>> F assertThat(Class<F> clazz, AssertionStrategy<T> object) {
        return fluentAssert(ASSERT, object, clazz);
    }

    public static <T, F extends FluentAssert<T>> F assertThat(Class<F> clazz, String reason, T object) {
        return (F) assertThat(clazz, object).as(reason);
    }

    public static <T, F extends FluentAssert<T>> F assertThat(Class<F> clazz, String reason, AssertionStrategy<T> object) {
        return (F) assertThat(clazz, object).as(reason);
    }

    public static <T, F extends FluentMatcher<T>> F matcher(Class<F> clazz) {
        return fluentMatcher(clazz);
    }
    
}
