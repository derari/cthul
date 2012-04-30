package org.cthul.matchers.test;

/**
 *
 * @author Arian Treffer
 */
public interface ColorFluent {

    public static interface Object<Item extends java.lang.Object, This extends Object<Item, This>>
            extends org.cthul.matchers.fluent.custom.CustomFluent<Item, This> {

        @org.cthul.matchers.fluent.custom.Implementation(org.cthul.matchers.test.ColorMatchers.class)
        This stuff(java.lang.String name);

        @org.cthul.matchers.fluent.custom.Implementation(org.cthul.matchers.test.ColorMatchers.class)
        This stuff(java.util.List<java.lang.Integer> ints);

    }

    public static interface String<Item extends java.lang.String, This extends String<Item, This>>
            extends Object<Item, This> {

        @org.cthul.matchers.fluent.custom.Implementation(org.cthul.matchers.test.ColorMatchers.class)
        This gray();

        @org.cthul.matchers.fluent.custom.Implementation(org.cthul.matchers.test.ColorMatchers.class)
        This red();

    }

    public static interface Throwable<Item extends java.lang.Throwable, This extends Throwable<Item, This>>
            extends Object<Item, This> {

        @org.cthul.matchers.fluent.custom.Implementation(org.cthul.matchers.test.ColorMatchers.class)
        This stuff2(Item t);

    }
    
}
