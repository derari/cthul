package org.cthul.matchers.fluent.property;

import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.values.MatchValueType;
import org.hamcrest.Matcher;


/**
 * Fluent that matches a property of a value.
 * After the match, the chain goes back to the value itself.
 * 
 * @param <Value, Property> 
 */
public interface FluentProperty<Value, Property> {

    FluentProperty<Value, Property> as(String reason);

    FluentProperty<Value, Property> as(String reason, Object... args);

    FluentProperty<Value, Property> is();

    FluentProperty<Value, Property> has();

    FluentProperty<Value, Property> not();
    
    Fluent<Value> _(Matcher<? super Property> matcher);

    Fluent<Value> is(Matcher<? super Property> matcher);
    
    Fluent<Value> has(Matcher<? super Property> matcher);

    Fluent<Value> not(Matcher<? super Property> matcher);
    
    Fluent<Value> isNot(Matcher<? super Property> matcher);
    
    Fluent<Value> hasNot(Matcher<? super Property> matcher);

    Fluent<Value> all(Matcher<? super Property>... matcher);
    
    Fluent<Value> any(Matcher<? super Property>... matcher);
    
    Fluent<Value> none(Matcher<? super Property>... matcher);
    
    <P> FluentProperty<Value, P> _(MatchValueType<? super Property, P> adapter);
}
