package org.cthul.matchers.fluent.property;

import org.cthul.matchers.fluent.*;
import org.cthul.matchers.fluent.values.MatchValueType;
import org.hamcrest.Matcher;

/**
 * Fluent assert that matches a property of a value.
 * After the match, the chain goes back to the value itself.
 * 
 * @param <Value, Property> 
 */
public interface FluentAssertProperty<Value, Property> extends FluentProperty<Value, Property> {

    @Override
    FluentAssertProperty<Value, Property> as(String reason);

    @Override
    FluentAssertProperty<Value, Property> as(String reason, Object... args);

    @Override
    FluentAssertProperty<Value, Property> is();

    @Override
    FluentAssertProperty<Value, Property> has();

    @Override
    FluentAssertProperty<Value, Property> not();
    
    @Override
    FluentAssert<Value> _(Matcher<? super Property> matcher);

    @Override
    FluentAssert<Value> is(Matcher<? super Property> matcher);
    
    @Override
    FluentAssert<Value> has(Matcher<? super Property> matcher);

    @Override
    FluentAssert<Value> not(Matcher<? super Property> matcher);
    
    @Override
    FluentAssert<Value> isNot(Matcher<? super Property> matcher);
    
    @Override
    FluentAssert<Value> hasNot(Matcher<? super Property> matcher);

    @Override
    FluentAssert<Value> all(Matcher<? super Property>... matcher);
    
    @Override
    FluentAssert<Value> any(Matcher<? super Property>... matcher);
    
    @Override
    FluentAssert<Value> none(Matcher<? super Property>... matcher);
    
    @Override
    <P> FluentAssertProperty<Value, P> _(MatchValueType<? super Property, P> matcher);
    
}