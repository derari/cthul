package org.cthul.matchers;

import org.cthul.matchers.chain.AndChainMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;

/**
 *
 * @author Arian Treffer
 */
public class InstanceThat {
    
    @Factory
    @SuppressWarnings("unchecked")
    public static <T, T2> Matcher<T> instanceThat(Class<T2> clazz, Matcher<? super T2> m) {
        return (Matcher) AndChainMatcher.and(IsInstanceOf.instanceOf(clazz), m);
    }
    
    @Factory
    @SuppressWarnings("unchecked")
    public static <T, T2> Matcher<T> instanceThat(Class<T2> clazz, Matcher<? super T2>... m) {
        Matcher[] matchers = new Matcher[m.length+1];
        matchers[0] = IsInstanceOf.instanceOf(clazz);
        System.arraycopy(m, 0, matchers, 1, m.length);
        return AndChainMatcher.and(matchers);
    }
    
    @Factory
    public static <T, T2> Matcher<T> isInstanceThat(Class<T2> clazz, Matcher<? super T2> m) {
        return Is.is(InstanceThat.<T, T2>instanceThat(clazz, m));
    }
    
    @Factory
    @SuppressWarnings("unchecked")
    public static <T, T2> Matcher<T> isInstanceThat(Class<T2> clazz, Matcher<? super T2>... m) {
        return Is.is(InstanceThat.<T, T2>instanceThat(clazz, m));
    }
    
}