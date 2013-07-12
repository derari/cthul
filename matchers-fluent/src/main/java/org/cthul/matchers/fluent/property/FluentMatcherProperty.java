package org.cthul.matchers.fluent.property;

import org.cthul.matchers.diagnose.QuickDiagnosingMatcher;
import org.cthul.matchers.fluent.*;
import org.hamcrest.Matcher;

/**
 * Fluent Matcher that matches a property of a value.
 * After the match, the chain goes back to the value itself.
 * 
 * @param <Value, Property> 
 */
public interface FluentMatcherProperty<Value, Property, Match> 
                extends FluentProperty<Value, Property> {

    @Override
    FluentMatcherProperty<Value, Property, Match> as(String reason);

    @Override
    FluentMatcherProperty<Value, Property, Match> as(String reason, Object... args);

    @Override
    FluentMatcherProperty<Value, Property, Match> is();

    @Override
    FluentMatcherProperty<Value, Property, Match> has();

    @Override
    FluentMatcherProperty<Value, Property, Match> not();
    
    @Override
    FluentMatcher<Value, Match> _(Matcher<? super Property> matcher);

    @Override
    FluentMatcher<Value, Match> is(Matcher<? super Property> matcher);
    
    @Override
    FluentMatcher<Value, Match> has(Matcher<? super Property> matcher);

    @Override
    FluentMatcher<Value, Match> not(Matcher<? super Property> matcher);
    
    @Override
    FluentMatcher<Value, Match> isNot(Matcher<? super Property> matcher);
    
    @Override
    FluentMatcher<Value, Match> hasNot(Matcher<? super Property> matcher);

    @Override
    FluentMatcher<Value, Match> all(Matcher<? super Property>... matcher);
    
    @Override
    FluentMatcher<Value, Match> any(Matcher<? super Property>... matcher);
    
    @Override
    FluentMatcher<Value, Match> none(Matcher<? super Property>... matcher);
    
}