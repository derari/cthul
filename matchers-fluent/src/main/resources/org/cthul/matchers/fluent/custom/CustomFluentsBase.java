package org.cthul.matchers.fluent.custom;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.strategy.MatchingObject;
import org.cthul.matchers.fluent.AssertUtils;
import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.assertion.FailureHandler;
import org.cthul.matchers.fluent.assertion.FluentAssertion;
import org.cthul.matchers.fluent.builder.FluentBuilder;

/**
 *
 * @author derari
 */
public class CustomFluentsBase extends AssertUtils {
    
    protected static <Item, F extends FluentAssert<Item>> F fluentAssert(
                        FailureHandler failureHandler, 
                        Item object, Class<F> clazz) {
        return FluentProxyInvocationHandler.createCustomFluent(
                new FluentAssertion<>(failureHandler, object), 
                classLoader(), clazz);
    }
    
    protected static <Item, F extends FluentAssert<Item>> F fluentAssert(
                        FailureHandler failureHandler, 
                        MatchingObject<Item> assertion, Class<F> clazz) {
        return FluentProxyInvocationHandler.createCustomFluent(
                new FluentAssertion<>(failureHandler, assertion), 
                classLoader(), clazz);
    }
    
    protected static <Item, F extends FluentMatcher<Item>> F fluentMatcher(
                        Class<F> clazz) {
        return FluentProxyInvocationHandler.createCustomFluent(
                new FluentBuilder<>(), 
                classLoader(), clazz);
    }
    
    protected static ClassLoader classLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
    
}
