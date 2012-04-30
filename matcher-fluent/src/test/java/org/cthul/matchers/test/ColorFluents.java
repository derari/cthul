package org.cthul.matchers.test;

import org.cthul.matchers.fluent.custom.CustomFluent;
import org.cthul.matchers.fluent.custom.CustomFluentAssert;
import org.cthul.matchers.fluent.custom.CustomFluentMatcher;
import org.cthul.matchers.fluent.custom.CustomFluents;

/**
 *
 * @author derari
 */
public class ColorFluents extends CustomFluents {
    
    public static <T> Assert<T> assertThat(T object) {
        return fluentAssert(ASSERT, object, Assert.class);
    }
    
    public static <T> Matcher<T> is() {
        return fluentMatcher(Matcher.class);
    }
    
    public static interface Assert<Item>
                extends CustomFluentAssert<Item, Assert<Item>>, 
                        ColorFluent<Item, Assert<Item>> {
    }
    
    public static interface Matcher<Item> 
                extends ColorFluent.Matcher<Item> {
    }
    
    // new way to define fluents:
    
    public static interface InstanceThatMatcher
                    extends CoreInstanceThatFluent<StringMatcher, IntegerMatcher, Void> {
    }
    
    public static interface InstanceThatAssert
                    extends CoreInstanceThatFluent<StringAssert, Void, Void> {
    }
    
    public static interface ObjectFluent<This extends ObjectFluent<This>>
                    extends CustomFluent<Object, This> {
    }
    
    public static interface StringFluent<This extends StringFluent<This>> 
                    extends ColorFluent.String<String, This> {
    }
    
    public static interface IntegerFluent<This extends IntegerFluent<This>> 
                    extends ColorFluent.Integer<Integer, This> {
    }
    
    public static interface ObjectAssert
                    extends InstanceThatAssert,
                            ObjectFluent<ObjectAssert> {
    }
    
    public static interface ObjectMatcher
                    extends InstanceThatMatcher,
                            ObjectFluent<ObjectMatcher> {
    }
    
    public static interface StringAssert 
                    extends StringFluent<StringAssert>,
                            CustomFluentAssert<String, StringAssert> {
    }
    
    public static interface StringMatcher 
                    extends StringFluent<StringMatcher>,
                            CustomFluentMatcher<String, StringMatcher> {
    }
    
    public static interface IntegerMatcher 
                    extends IntegerFluent<IntegerMatcher>,
                            CustomFluentMatcher<Integer, IntegerMatcher> {
    }
       
    {
        ObjectMatcher m = null;
        m.isStringThat().is().red();
    }

}
