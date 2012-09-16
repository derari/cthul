package org.cthul.matchers.testbeta;

import org.cthul.matchers.fluent.custom.Implementation;

/**
 *
 * @author Arian Treffer
 */
@Implementation(SomeMatchers.class)
public interface IntFluent<This extends IntFluent<This>>
        extends BasicFluent<Integer, This> {
    
    public This isPrime();
    
    public static interface Assert<This extends Assert<This>>
            extends IntFluent<This>,
                    BasicFluent.Assert<Integer, This> {
    }
    
    public static interface Matcher<This extends Matcher<This>>
            extends IntFluent<This>,
                    BasicFluent.Matcher<Integer, This> {
    }
    
}
