package org.cthul.matchers.testold;

import org.cthul.matchers.fluent.custom.CustomFluentBuilder;
import org.cthul.matchers.testold.MColorFluents.Matcher;

/**
 *
 * @author derari
 */
public class MColorFluentBuilder<Item>
        extends CustomFluentBuilder<Item, ColorFluent.Matcher<Item>>
        implements MColorFluents.Matcher<Item> {

    @Override
    public Matcher<Item> red() {
        _is((Matcher) ColorMatchers.red());
        return this;
    }

    @Override
    public Matcher<Item> green() {
        _is((Matcher) ColorMatchers.green());
        return this;
    }

    @Override
    public Matcher<Item> blue() {
        _is((Matcher) ColorMatchers.blue());
        return this;
    }

    @Override
    public Matcher<Item> black() {
        _is((Matcher) ColorMatchers.black());
        return this;
    }
    
}
