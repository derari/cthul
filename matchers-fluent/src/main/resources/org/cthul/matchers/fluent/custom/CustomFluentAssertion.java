package org.cthul.matchers.fluent.custom;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.strategy.MatchingObject;
import org.cthul.matchers.fluent.assertion.FailureHandler;
import org.cthul.matchers.fluent.assertion.FluentAssertion;
import org.hamcrest.Matcher;

/**
 *
 * @author derari
 */
public class CustomFluentAssertion<Item, This extends CustomFluentAssert<Item, This>> 
                extends FluentAssertion<Item>
                implements CustomFluentAssert<Item, This> {

    public CustomFluentAssertion(FailureHandler failureHandler, Object object) {
        super(failureHandler, object);
    }

    public CustomFluentAssertion(FailureHandler failureHandler, MatchingObject assertionStrategy) {
        super(failureHandler, assertionStrategy);
    }

    @Override
    public This as(String reason) {
        return (This) super.as(reason);
    }

    @Override
    public This as(String reason, Object... args) {
        return (This) super.as(reason, args);
    }

    @Override
    public This is(Matcher<? super Item> matcher) {
        return (This) super.is(matcher);
    }

    @Override
    public This is() {
        return (This) super.is();
    }

    @Override
    public This and(Matcher<? super Item> matcher) {
        return (This) super.and(matcher);
    }

    @Override
    public This and() {
        return (This) super.and();
    }

    @Override
    public This isNot(Matcher<? super Item> matcher) {
        return (This) super.isNot(matcher);
    }

    @Override
    public This not(Matcher<? super Item> matcher) {
        return (This) super.not(matcher);
    }

    @Override
    public This not() {
        return (This) super.not();
    }

    @Override
    public This andNot(Matcher<? super Item> matcher) {
        return (This) super.andNot(matcher);
    }

    @Override
    public This andNot() {
        return (This) super.andNot();
    }
    
}
