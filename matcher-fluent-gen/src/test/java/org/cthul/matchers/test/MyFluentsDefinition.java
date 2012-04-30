package org.cthul.matchers.test;

/**
 *
 * @author derari
 */
public interface MyFluentsDefinition {
    
    public static interface Object<Item extends java.lang.Object, This extends Object<Item, This>>
            extends ColorFluent.Object<Item, This> {

    }

    public static interface String<Item extends java.lang.String, This extends String<Item, This>>
            extends Object<Item, This>,
                    ColorFluent.String<Item, This>, 
                    OtherFluent.String<Item, This> {

    }

    public static interface Throwable<Item extends java.lang.Throwable, This extends Throwable<Item, This>>
            extends Object<Item, This>,
                    ColorFluent.Throwable<Item, This> {

    }

    
}
