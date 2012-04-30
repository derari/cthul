package org.cthul.matchers.fluent;

import org.hamcrest.Matcher;

/**
 * Fluent builder for matchers.
 * @author Arian Treffer
 * @param <Item> 
 */
public interface FluentMatcher<Item> extends Fluent<Item>, Matcher<Item> {

     /*
     foo().or().bar()
     foo().and().bar()
     
     a().and().b()
     .or()
     c().and().d()
     
     is(red().blue().or().green())
     .and(top().or().bottom())
     
     a().and().b().or(c) --> error
     */
    
    @Override
    FluentMatcher<Item> as(String reason);

    @Override
    FluentMatcher<Item> as(String reason, Object... args);

    @Override
    FluentMatcher<Item> is(Matcher<? super Item> matcher);
    
    @Override
    FluentMatcher<Item> _(Matcher<? super Item> matcher);

    @Override
    FluentMatcher<Item> is();

    @Override
    FluentMatcher<Item> and(Matcher<? super Item> matcher);

    @Override
    FluentMatcher<Item> and();

    @Override
    FluentMatcher<Item> isNot(Matcher<? super Item> matcher);

    @Override
    FluentMatcher<Item> not(Matcher<? super Item> matcher);

    @Override
    FluentMatcher<Item> not();

    @Override
    FluentMatcher<Item> andNot(Matcher<? super Item> matcher);

    @Override
    FluentMatcher<Item> andNot();

    FluentMatcher<Item> either(Matcher<? super Item> matcher);

    FluentMatcher<Item> either();

    FluentMatcher<Item> xor(Matcher<? super Item> matcher);

    FluentMatcher<Item> xor();

    FluentMatcher<Item> or(Matcher<? super Item> matcher);

    FluentMatcher<Item> or();

    FluentMatcher<Item> orNot(Matcher<? super Item> matcher);

    FluentMatcher<Item> orNot();
    
    Matcher<Item> getMatcher();

}
