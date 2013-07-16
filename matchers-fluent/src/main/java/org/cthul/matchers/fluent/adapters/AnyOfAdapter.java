package org.cthul.matchers.fluent.adapters;

import java.util.Arrays;
import org.cthul.matchers.fluent.value.MatchValue;
import org.hamcrest.Factory;

/**
 *
 */
public class AnyOfAdapter<Item> extends 
                SimpleAnyOfAdapter<Iterable<? extends Item>, Item> {

    private static final AnyOfAdapter INSTANCE = new AnyOfAdapter<>();

    @Factory
    public static <I> AnyOfAdapter<I> anyOf() {
        return INSTANCE;
    }
    
    @Factory
    public static <I> MatchValue<I> anyOf(Iterable<? extends I> iterable) {
        return (MatchValue) INSTANCE.adapt(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> anyOf(I... array) {
        return anyOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> anyOf(MatchValue<? extends Iterable<? extends I>> mo) {
        return INSTANCE.adapt(mo);
    }
    
    @Factory
    public static <I> AnyOfAdapter<I> any() {
        return INSTANCE;
    }
    
    @Factory
    public static <I> AnyOfAdapter<I> any(Class<I> c) {
        return any();
    }
    
    @Factory
    public static <I> MatchValue<I> any(Iterable<? extends I> iterable) {
        return anyOf(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> any(I... array) {
        return anyOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> any(MatchValue<? extends Iterable<? extends I>> mo) {
        return anyOf(mo);
    }
    
    @Factory
    public static AnyOfAdapter<Integer> anyInt() {
        return any();
    }
    
    public AnyOfAdapter() {
        super();
    }

    public AnyOfAdapter(String name) {
        super(name);
    }

    @Override
    protected Iterable<? extends Item> getElements(Iterable<? extends Item> value) {
        return value;
    }
    
}
