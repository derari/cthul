package org.cthul.matchers;

import org.cthul.matchers.chain.AndChainMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;

/**
 *
 */
public class InstanceThat {
    
    @Factory
    @SuppressWarnings("unchecked")
    public static <T, T2> AndChainMatcher.Builder<T> instanceThat(Class<T2> clazz, Matcher<? super T2> m) {
        return (AndChainMatcher.Builder) AndChainMatcher.both(IsInstanceOf.instanceOf(clazz), m);
    }
    
    @Factory
    @SuppressWarnings("unchecked")
    public static <T, T2> AndChainMatcher.Builder<T> instanceThat(Class<T2> clazz, Matcher<? super T2>... m) {
        Matcher[] matchers = new Matcher[m.length+1];
        matchers[0] = IsInstanceOf.instanceOf(clazz);
        System.arraycopy(m, 0, matchers, 1, m.length);
        return AndChainMatcher.both(matchers);
    }
    
    @Factory
    @SuppressWarnings("unchecked")
    public static <T, T2> AndChainMatcher.Builder<T> isInstanceThat(Class<T2> clazz, Matcher<? super T2> m) {
        return (AndChainMatcher.Builder) AndChainMatcher.both(Is.is(IsInstanceOf.instanceOf(clazz)), m);
    }
    
    @Factory
    @SuppressWarnings("unchecked")
    public static <T, T2> Matcher<T> isInstanceThat(Class<T2> clazz, Matcher<? super T2>... m) {
        return Is.is(InstanceThat.<T, T2>instanceThat(clazz, m));
    }
    
}
