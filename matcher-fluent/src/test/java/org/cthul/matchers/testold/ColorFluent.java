package org.cthul.matchers.testold;

import org.cthul.matchers.fluent.custom.CustomFluent;
import org.cthul.matchers.fluent.custom.CustomFluentAssert;
import org.cthul.matchers.fluent.custom.CustomFluentMatcher;
import org.cthul.matchers.fluent.custom.Implementation;

/**
 * This file can be generated.
 * TODO: implement generator
 * 
 * Fluent methods for {@link ColorMatchers}.
 * 
 * @author Arian Treffer
 */
@Implementation(ColorMatchers.class)
public interface ColorFluent<Item, This extends ColorFluent<Item, This>>
            extends CustomFluent<Item, This> {

    public This red();

    public This green();

    public This blue();

    public This black();
    
    // This interface only exists to allow reusing test code.
    public static interface Matcher<Item> 
                extends CustomFluentMatcher<Item, Matcher<Item>>,
                        ColorFluent<Item, Matcher<Item>> {
    }
    
    // the new way to define these interfaces:
    
    @Implementation(ColorMatchers.class)
    public static interface String<Item extends java.lang.String, 
                                   This extends String<Item, This>>
                    extends CustomFluent<Item, This> {
        
        public This red();

        public This green();

        public This blue();        
        
    }
    
    @Implementation(ColorMatchers.class)
    public static interface Integer<Item extends java.lang.Integer, 
                                    This extends Integer<Item, This>>
                    extends CustomFluent<Item, This> {
        
        public This black();
        
    }
        
}
