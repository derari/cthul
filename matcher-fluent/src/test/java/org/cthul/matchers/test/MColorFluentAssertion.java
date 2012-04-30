package org.cthul.matchers.test;

import org.cthul.matchers.fluent.assertion.AssertionStrategy;
import org.cthul.matchers.fluent.assertion.FailureHandler;
import org.cthul.matchers.fluent.custom.CustomFluentAssertion;

/**
 * Manual implementation of Color fluent assertion.
 * 
 * @author derari
 */
public class MColorFluentAssertion<Item>
        extends CustomFluentAssertion<Item, MColorFluents.Assert<Item>> 
        implements MColorFluents.Assert<Item> {

    public MColorFluentAssertion(FailureHandler failureHandler, Object object) {
        super(failureHandler, object);
    }

    public MColorFluentAssertion(FailureHandler failureHandler, AssertionStrategy assertionStrategy) {
        super(failureHandler, assertionStrategy);
    }

    @Override
    public MColorFluents.Assert<Item> red() {
        _is(ColorMatchers.red());
        return this;
    }

    @Override
    public MColorFluents.Assert<Item> green() {
        _is(ColorMatchers.green());
        return this;
    }

    @Override
    public MColorFluents.Assert<Item> blue() {
        _is(ColorMatchers.blue());
        return this;
    }

    @Override
    public MColorFluents.Assert<Item> black() {
        _is(ColorMatchers.black());
        return this;
    }
    
}
