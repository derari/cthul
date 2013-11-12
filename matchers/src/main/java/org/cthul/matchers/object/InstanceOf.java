package org.cthul.matchers.object;

import org.cthul.matchers.chain.AndChainMatcher;
import org.cthul.matchers.diagnose.QuickDiagnosingMatcherBase;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.*;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;

/**
 * Example:
 * <pre>{@code 
 *   Object o = "foobar;
 *   assertThat(o, isA(String.class).thatIs(foo()).and(bar());
 * }</pre>
 */
public class InstanceOf<T> extends QuickDiagnosingMatcherBase<Object> {

    private boolean prependIs;
    private final Class<T> clazz;
    private IsInstanceOf matcher = null;

    public InstanceOf(boolean prependIs, Class<T> expectedClass) {
        this.prependIs = prependIs;
        this.clazz = expectedClass;
    }
    
    public InstanceOf(Class<T> expectedClass) {
        this(false, expectedClass);
    }
    
    protected IsInstanceOf matcher() {
        if (matcher == null) {
            matcher = new IsInstanceOf(clazz);
        }
        return matcher;
    }
    
    @Override
    public boolean matches(Object o) {
        return matcher().matches(o);
    }
    
    @Override
    public boolean matches(Object item, Description mismatch) {
        return quickMatch(matcher(), item, mismatch);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        matcher().describeMismatch(item, description);
    }

    @Override
    public void describeTo(Description description) {
        matcher().describeTo(description);
    }

    @Override
    public <I> MatchResult<I> matchResult(I item) {
        return quickMatchResult(matcher(), item);
    }
    
    public <X> AndChainMatcher.Builder<X> that(Matcher<? super T> m) {
        if (prependIs) {
            return InstanceThat.isInstanceThat(clazz, m);
        } else {
            return InstanceThat.instanceThat(clazz, m);
        }
    }
    
    public <X> AndChainMatcher.Builder<X> thatIs(Matcher<? super T> m) {
        return that(Is.is(m));
    }
    
    @SuppressWarnings("unchecked")
    public <X> Matcher<X> that(Matcher... m) {
        if (prependIs) {
            return InstanceThat.isInstanceThat(clazz, m);
        } else {
            return InstanceThat.instanceThat(clazz, m);
        }
    }
    
    @Factory
    public static <T> InstanceOf<T> isInstanceOf(Class<T> clazz) {
        return new InstanceOf<>(true, clazz);
    }
    
    @Factory
    public static <T> InstanceOf<T> instanceOf(Class<T> clazz) {
        return new InstanceOf<>(clazz);
    }

    @Factory
    public static <T> InstanceOf<T> isA(Class<T> clazz) {
        return new InstanceOf<>(true, clazz);
    }
    
    @Factory
    public static <T> InstanceOf<T> _instanceOf(Class<T> clazz) {
        return new InstanceOf<>(clazz);
    }

    @Factory
    public static <T> InstanceOf<T> _isA(Class<T> clazz) {
        return new InstanceOf<>(true, clazz);
    }
    
    @Factory
    public static <T> InstanceOf<T> a(Class<T> clazz) {
        return new InstanceOf<>(clazz);
    }
    
}
