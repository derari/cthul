package org.cthul.matchers.fluent;

import org.hamcrest.Matcher;

/**
 * A fluent that is not a matcher,
 * but evaluates the conditions as they are added.
 * 
 * @author Arian Treffer
 */
public interface FluentAssert<Item> extends Fluent<Item> {
    
    @Override
    FluentAssert<Item> as(String reason);

    @Override
    FluentAssert<Item> as(String reason, Object... args);

    @Override
    FluentAssert<Item> is(Matcher<? super Item> matcher);
    
    @Override
    FluentAssert<Item> _(Matcher<? super Item> matcher);

    @Override
    FluentAssert<Item> is();

    @Override
    FluentAssert<Item> and(Matcher<? super Item> matcher);

    @Override
    FluentAssert<Item> and();

    @Override
    FluentAssert<Item> isNot(Matcher<? super Item> matcher);

    @Override
    FluentAssert<Item> not(Matcher<? super Item> matcher);

    @Override
    FluentAssert<Item> not();

    @Override
    FluentAssert<Item> andNot(Matcher<? super Item> matcher);

    @Override
    FluentAssert<Item> andNot();
    
    @Override
    FluentAssert<Item> all(Matcher<? super Item>... matcher);
    
}
