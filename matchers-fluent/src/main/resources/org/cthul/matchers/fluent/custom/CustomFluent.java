package org.cthul.matchers.fluent.custom;

import org.hamcrest.Matcher;
import org.cthul.matchers.fluent.Fluent;

/**
 * A fluent that explicitly is from a subtype of {@link Fluent}.
 * @author Arian Treffer
 * @param <Item>
 * @param <This> 
 */
public interface CustomFluent<Item, This extends CustomFluent<Item, This>> 
                extends Fluent<Item> {

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

}
