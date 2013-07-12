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
public interface StringFluent<This extends StringFluent<This>>
        extends BasicFluent<String, This> {
    
    public This isEmpty();
    
    public static interface Assert<This extends Assert<This>>
            extends StringFluent<This>,
                    BasicFluent.Assert<String, This> {
    }
    
    public static interface Matcher<This extends Matcher<This>>
            extends StringFluent<This>,
                    BasicFluent.Matcher<String, This> {
    }
    
}
