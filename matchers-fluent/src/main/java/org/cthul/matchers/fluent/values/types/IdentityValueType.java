package org.cthul.matchers.fluent.values.types;

import org.cthul.matchers.fluent.values.MatchingObject;

/**
 *
 */
public class IdentityValueType<T> extends ConvertingValueType<T, T> {

    private static final IdentityValueType INSTANCE = new IdentityValueType<>();
    
    public static <T> IdentityValueType<T> value() {
        return INSTANCE;
    }
    
    public static <T> MatchingObject<T> value(T v) {
        return INSTANCE.matchingObject(v);
    }
    
    @Override
    protected T convert(T v) {
        return v;
    }
    
}
