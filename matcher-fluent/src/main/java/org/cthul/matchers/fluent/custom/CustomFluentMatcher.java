package org.cthul.matchers.fluent.custom;

import org.hamcrest.Matcher;
import org.cthul.matchers.fluent.FluentMatcher;

public interface CustomFluentMatcher<Item, 
                                This extends CustomFluentMatcher<Item, This>> 
        extends FluentMatcher<Item>,
                CustomFluent<Item, This> {

    This as(String reason);

    This as(String reason, Object... args);

    This is(Matcher<? super Item> matcher);

    This is();

    This and(Matcher<? super Item> matcher);

    This and();

    This isNot(Matcher<? super Item> matcher);

    This not(Matcher<? super Item> matcher);

    This not();

    This andNot(Matcher<? super Item> matcher);

    This andNot();

    This either(Matcher<? super Item> matcher);

    This either();

    This xor(Matcher<? super Item> matcher);

    This xor();

    This or(Matcher<? super Item> matcher);

    This or();

    This orNot(Matcher<? super Item> matcher);

    This orNot();

}
