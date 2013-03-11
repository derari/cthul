package org.cthul.matchers.fluent;

import java.util.Collection;
import org.cthul.matchers.fluent.assertion.AssertionErrorHandler;
import org.cthul.matchers.fluent.strategy.AnyOfStrategy;
import org.cthul.matchers.fluent.strategy.EachOfStrategy;
import org.cthul.matchers.fluent.strategy.SingleItemStrategy;

public class AssertUtils {

    public static <T> SingleItemStrategy.MObject<T> object(T object) {
        return SingleItemStrategy.applyTo(object);
    }

    public static <T> AnyOfStrategy.MObject<T> anyOf(Collection<T> collection) {
        return AnyOfStrategy.applyTo(collection);
    }

    public static <T> AnyOfStrategy.MObject<T> any(Collection<T> collection) {
        return anyOf(collection);
    }

    public static <T> EachOfStrategy.MObject<T> eachOf(Collection<T> collection) {
        return EachOfStrategy.applyTo(collection);
    }

    public static <T> EachOfStrategy.MObject<T> each(Collection<T> collection) {
        return eachOf(collection);
    }

    public static final AssertionErrorHandler ASSERT = AssertionErrorHandler.INSTANCE;

}
