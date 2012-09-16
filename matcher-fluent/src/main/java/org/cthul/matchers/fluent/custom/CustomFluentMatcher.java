package org.cthul.matchers.fluent.custom;

import org.hamcrest.Matcher;
import org.cthul.matchers.fluent.FluentMatcher;

public interface CustomFluentMatcher<Item, 
                                     This extends CustomFluentMatcher<Item, This>> 
        extends FluentMatcher<Item>,
                CustomFluent<Item, This> {

    @Override
    This as(String reason);

    @Override
    This as(String reason, Object... args);

    @Override
    This is(Matcher<? super Item> matcher);

    @Override
    This is();

    @Override
    This and(Matcher<? super Item> matcher);

    @Override
    This and();

    @Override
    This isNot(Matcher<? super Item> matcher);

    @Override
    This not(Matcher<? super Item> matcher);

    @Override
    This not();

    @Override
    This andNot(Matcher<? super Item> matcher);

    @Override
    This andNot();

    @Override
    This either(Matcher<? super Item> matcher);

    @Override
    This either();

    @Override
    This xor(Matcher<? super Item> matcher);

    @Override
    This xor();

    @Override
    This or(Matcher<? super Item> matcher);

    @Override
    This or();

    @Override
    This orNot(Matcher<? super Item> matcher);

    @Override
    This orNot();

}
