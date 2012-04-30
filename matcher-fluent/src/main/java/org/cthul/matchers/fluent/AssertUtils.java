package org.cthul.matchers.fluent;

import java.util.Collection;
import org.cthul.matchers.fluent.assertion.AnyOfStrategy;
import org.cthul.matchers.fluent.assertion.AssertionErrorHandler;
import org.cthul.matchers.fluent.assertion.EachOfStrategy;
import org.cthul.matchers.fluent.assertion.SingleItemStrategy;

public class AssertUtils {

    public static <T> SingleItemStrategy<T> object(T object) {
        return new SingleItemStrategy<>(object);
    }

    public static <T> AnyOfStrategy<T> anyOf(Collection<T> collection) {
        return new AnyOfStrategy<>(collection);
    }

    public static <T> AnyOfStrategy<T> any(Collection<T> collection) {
        return anyOf(collection);
    }

    public static <T> EachOfStrategy<T> eachOf(Collection<T> collection) {
        return new EachOfStrategy<>(collection);
    }

    public static <T> EachOfStrategy<T> each(Collection<T> collection) {
        return eachOf(collection);
    }

    public static final AssertionErrorHandler ASSERT = AssertionErrorHandler.INSTANCE;

}
