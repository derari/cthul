package org.cthul.matchers.test;

import org.cthul.matchers.fluent.custom.Implementation;

/**
 *
 * @author derari
 */
public interface OtherFluent {
    
    public static interface String<Item extends java.lang.String, This extends String<Item, This>>
            extends org.cthul.matchers.fluent.custom.CustomFluent<Item, This> {

        @Implementation(Void.class) // not really implemented
        This darkGray();

    }
    
}
