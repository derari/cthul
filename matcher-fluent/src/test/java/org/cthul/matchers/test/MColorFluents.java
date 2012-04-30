package org.cthul.matchers.test;

import org.cthul.matchers.fluent.assertion.AssertionStrategy;
import org.cthul.matchers.fluent.AssertUtils;
import org.cthul.matchers.fluent.custom.CustomFluentAssert;

/**
 * Manual implementation of Color fluent assert.
 * 
 * @author Arian Treffer
 */
public class MColorFluents extends AssertUtils {
    
    public static <T> MColorFluents.Assert<T> assertThat(T object) {
        return new MColorFluentAssertion<>(ASSERT, object);
    }

    public static <T> MColorFluents.Assert<T> assertThat(AssertionStrategy<T> object) {
        return new MColorFluentAssertion<>(ASSERT, object);
    }
    
    public static <T> MColorFluents.Matcher<T> is() {
        return new MColorFluentBuilder<>();
    }
    
    public static interface Assert<Item>
                extends CustomFluentAssert<Item, Assert<Item>>, 
                        ColorFluent<Item, Assert<Item>> {
    }
    
    public static interface Matcher<Item> 
                extends ColorFluent.Matcher<Item> {
    }
    
}
