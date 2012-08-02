package org.cthul.matchers;

import org.cthul.matchers.diagnose.QuickDiagnosingMatcherBase;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsInstanceOf;

/**
 *
 * @author Arian Treffer
 */
public class InstanceOf<T> extends QuickDiagnosingMatcherBase<Object> {

    private boolean prependIs;
    private final Class<T> clazz;
    private IsInstanceOf matcher;

    public InstanceOf(boolean prependIs, Class<T> expectedClass) {
        this.matcher = new IsInstanceOf(expectedClass);
        this.prependIs = prependIs;
        this.clazz = expectedClass;
    }
    
    public InstanceOf(Class<T> expectedClass) {
        this(false, expectedClass);
    }
    
    @Override
    public boolean matches(Object o) {
        return matcher.matches(o);
    }
    
    @Override
    public boolean matches(Object item, Description mismatch) {
        return quickMatch(matcher, item, mismatch);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        matcher.describeMismatch(item, description);
    }

    @Override
    public void describeTo(Description description) {
        matcher.describeTo(description);
    }
    
    public Matcher<T> that(Matcher<? super T> m) {
        if (prependIs) {
            return InstanceThat.isInstanceThat(clazz, m);
        } else {
            return InstanceThat.instanceThat(clazz, m);
        }
    }
    
    public Matcher<T> that(Matcher<? super T>... m) {
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

}
