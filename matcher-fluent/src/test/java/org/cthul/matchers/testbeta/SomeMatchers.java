package org.cthul.matchers.testbeta;

import org.cthul.matchers.InstanceOf;
import org.cthul.matchers.chain.OrChainMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.hamcrest.text.IsEmptyString;

/**
 *
 * @author Arian Treffer
 */
public class SomeMatchers {
    
    public static Matcher<Object> equalTo(Object other) {
        return IsEqual.equalTo(other);
    }
    
    public static Matcher<Object> stringThat(Matcher<String> m) {
        return InstanceOf.instanceOf(String.class).that(m);
    }
    
    public static Matcher<Object> intThat(Matcher<Integer> m) {
        return InstanceOf.instanceOf(Integer.class).that(m);
    }
    
    public static Matcher<Integer> isPrime() {
        return OrChainMatcher.or(Is.is(2), Is.is(3), Is.is(5));
    }
    
    public static Matcher<String> isEmpty() {
        return IsEmptyString.isEmptyString();
    }
}
