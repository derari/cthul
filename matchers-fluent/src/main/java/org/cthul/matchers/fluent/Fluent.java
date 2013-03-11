package org.cthul.matchers.fluent;

import org.hamcrest.Matcher;

/**
 * Fluent matcher chain.
 * 
 * @author Arian Treffer
 * @param <Item> 
 */
public interface Fluent<Item> {

    /*
     assertThat(x).is(foo()
                  .or().bar());
     assertThat(x).is().foo()
                  .and.bar();

     assertThat(x).is(either(a().and().b()).
                      or(c().and().d());
                  .is(red().blue().or().green())
                  .and(top().or().bottom());
     */

    Fluent<Item> as(String reason);

    Fluent<Item> as(String reason, Object... args);

    Fluent<Item> is(Matcher<? super Item> matcher);
    
    Fluent<Item> _(Matcher<? super Item> matcher);

    Fluent<Item> is();

    Fluent<Item> and(Matcher<? super Item> matcher);

    Fluent<Item> and();

    Fluent<Item> isNot(Matcher<? super Item> matcher);

    Fluent<Item> not(Matcher<? super Item> matcher);

    Fluent<Item> not();

    Fluent<Item> andNot(Matcher<? super Item> matcher);

    Fluent<Item> andNot();
    
    Fluent<Item> all(Matcher<? super Item>... matcher);

}
