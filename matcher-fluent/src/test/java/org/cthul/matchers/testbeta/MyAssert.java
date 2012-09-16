package org.cthul.matchers.testbeta;

import org.cthul.matchers.fluent.custom.CustomFluentsBase;

/**
 *
 * @author Arian Treffer
 */
public class MyAssert extends CustomFluentsBase {
    
    public static IntFluent.Assert<?> assertThat(int actual) {
        return fluentAssert(ASSERT, actual, IntFluent.Assert.class);
    }
    
    public static ObjectFluent.Assert<?> assertThat(Object actual) {
        return fluentAssert(ASSERT, actual, ObjectFluent.Assert.class);
    }
    
    public static IntFluent.Matcher intThat() {
        return fluentMatcher(IntFluent.Matcher.class);
    }
    
    {
        assertThat(2).isPrime().andNot().is(3).and().isPrime();
        Object o = Integer.valueOf(1);
        //assertThat(o).is().intThat(null)
    }
    
}
