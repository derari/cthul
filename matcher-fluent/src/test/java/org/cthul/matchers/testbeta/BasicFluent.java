package org.cthul.matchers.testbeta;

import org.cthul.matchers.fluent.custom.CustomFluent;
import org.cthul.matchers.fluent.custom.CustomFluentAssert;
import org.cthul.matchers.fluent.custom.CustomFluentMatcher;
import org.cthul.matchers.fluent.custom.Implementation;

/**
 *
 * @author Arian Treffer
 */
@Implementation(SomeMatchers.class)
public interface BasicFluent<Item, This extends BasicFluent<Item, This>>
        extends CustomFluent<Item, This> {
    
    public This equalTo(Object other);
    
    public This is(Item item);
    
    public static interface Assert<Item, This extends Assert<Item, This>>
            extends BasicFluent<Item, This>,
                    CustomFluentAssert<Item, This> {
    }
    
    public static interface Matcher<Item, This extends Matcher<Item, This>>
            extends BasicFluent<Item, This>,
                    CustomFluentMatcher<Item, This> {
    }
    
}
