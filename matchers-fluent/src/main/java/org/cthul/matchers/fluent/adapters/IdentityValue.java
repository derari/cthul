package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.fluent.values.MatchValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public class IdentityValue<T> extends ConvertingAdapter<T, T> {

    private static final IdentityValue INSTANCE = new IdentityValue<>();
    
    public static <T> IdentityValue<T> value() {
        return INSTANCE;
    }
    
    public static <T> MatchValue<T> value(T v) {
        return INSTANCE.adapt(v);
    }
    
    @Override
    protected T getValue(T v) {
        return v;
    }

    @Override
    public void describeTo(Matcher<?> matcher, Description description) {
        matcher.describeTo(description);
    }

    @Override
    protected void describeMismatch(Matcher<?> matcher, T value, T item, Description description) {
        matcher.describeMismatch(item, description);
    }   
}
