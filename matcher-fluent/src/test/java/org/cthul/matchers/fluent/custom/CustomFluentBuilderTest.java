package org.cthul.matchers.fluent.custom;

import org.cthul.matchers.fluent.builder.FluentBuilderTest;
import org.cthul.matchers.test.ColorFluents;
import org.cthul.matchers.test.ColorFluent;

/**
 *
 * @author derari
 */
public class CustomFluentBuilderTest extends FluentBuilderTest {

    @Override
    protected <Item> ColorFluent.Matcher<Item> _is() {
        return ColorFluents.is();
    }
    
}
