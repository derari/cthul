package org.cthul.matchers.fluent.adapters;

import org.cthul.matchers.fluent.values.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.values.MatchValue;
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
    public MatchValue<T> wrap(MatchValue<T> v) {
        // well, that was easy
        return v;
    }

    @Override
    public void describeTo(Matcher<?> matcher, Description description) {
        description.appendDescriptionOf(matcher);
    }
}
