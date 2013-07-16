package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.fluent.value.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public class IdentityValue<T> extends AbstractMatchValueAdapter<T, T> {

    private static final IdentityValue INSTANCE = new IdentityValue<>();
    
    public static <T> IdentityValue<T> value() {
        return INSTANCE;
    }
    
    public static <T> MatchValue<T> value(T v) {
        return INSTANCE.adapt(v);
    }

    @Override
    public MatchValue<T> adapt(MatchValue<T> v) {
        return v; // well, that was easy
    }

    @Override
    public void describeMatcher(Matcher<?> matcher, Description description) {
        description.appendDescriptionOf(matcher);
    }
}
