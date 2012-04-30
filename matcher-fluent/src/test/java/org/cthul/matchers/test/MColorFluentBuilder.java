package org.cthul.matchers.test;

import org.cthul.matchers.fluent.custom.CustomFluentBuilder;
import org.cthul.matchers.test.MColorFluents.Matcher;

/**
 *
 * @author derari
 */
public class MColorFluentBuilder<Item>
        extends CustomFluentBuilder<Item, ColorFluent.Matcher<Item>>
        implements MColorFluents.Matcher<Item> {

    @Override
    public Matcher<Item> red() {
        _is(ColorMatchers.red());
        return this;
    }

    @Override
    public Matcher<Item> green() {
        _is(ColorMatchers.green());
        return this;
    }

    @Override
    public Matcher<Item> blue() {
        _is(ColorMatchers.blue());
        return this;
    }

    @Override
    public Matcher<Item> black() {
        _is(ColorMatchers.black());
        return this;
    }
    
}
