package org.cthul.matchers.testbeta;

import org.cthul.matchers.fluent.custom.Implementation;

/**
 *
 * @author Arian Treffer
 */
@Implementation(SomeMatchers.class)
public interface ObjectFluent<This extends ObjectFluent<This>>
        extends BasicFluent<Object, This> {
    
    public This stringThat(org.hamcrest.Matcher<String> m);
    
    public This intThat(org.hamcrest.Matcher<Integer> m);
    
    public static interface Assert<This extends Assert<This>>
            extends ObjectFluent<This>,
                    BasicFluent.Assert<Object, This> {
    }
    
    public static interface Matcher<This extends Matcher<This>>
            extends ObjectFluent<This>,
                    BasicFluent.Matcher<Object, This> {
    }
    
}
