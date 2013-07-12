package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.property.FluentAssertProperty;
import org.hamcrest.Matcher;

/**
 * A fluent that is not a matcher,
 * but evaluates the conditions as they are added.
 * 
 * @author Arian Treffer
 */
public interface FluentAssert<Value> 
                extends Fluent<Value>, 
                        FluentAssertProperty<Value, Value> {
    
    @Override
    FluentAssert<Value> as(String reason);

    @Override
    FluentAssert<Value> as(String reason, Object... args);

    @Override
    FluentAssert<Value> is();
    
    @Override
    FluentAssert<Value> has();

    @Override
    FluentAssert<Value> not();
    
    @Override
    FluentAssert<Value> and();

    @Override
    FluentAssert<Value> andNot();
    
    @Override
    FluentAssert<Value> _(Matcher<? super Value> matcher);

    @Override
    FluentAssert<Value> is(Matcher<? super Value> matcher);

    @Override
    FluentAssert<Value> has(Matcher<? super Value> matcher);

    @Override
    FluentAssert<Value> not(Matcher<? super Value> matcher);

    @Override
    FluentAssert<Value> isNot(Matcher<? super Value> matcher);

    @Override
    FluentAssert<Value> hasNot(Matcher<? super Value> matcher);

    @Override
    FluentAssert<Value> and(Matcher<? super Value> matcher);

    @Override
    FluentAssert<Value> andNot(Matcher<? super Value> matcher);
    
    @Override
    FluentAssert<Value> all(Matcher<? super Value>... matcher);
    
    @Override
    FluentAssert<Value> any(Matcher<? super Value>... matcher);
    
    @Override
    FluentAssert<Value> none(Matcher<? super Value>... matcher);
    
}
