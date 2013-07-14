package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.property.FluentProperty;
import org.cthul.matchers.fluent.values.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * Fluent matcher chain.
 * <p>
 * As a {@linkplain FluentProperty property}, it matches against the
 * value itself.
 * 
 * @param <Value> 
 */
public interface Fluent<Value> extends FluentProperty<Value, Value> {

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

    @Override
    Fluent<Value> as(String reason);

    @Override
    Fluent<Value> as(String reason, Object... args);

    @Override
    Fluent<Value> is();
    
    @Override
    Fluent<Value> has();

    @Override
    Fluent<Value> not();
    
    @Override
    Fluent<Value> _(Matcher<? super Value> matcher);

    @Override
    Fluent<Value> is(Matcher<? super Value> matcher);

    @Override
    Fluent<Value> has(Matcher<? super Value> matcher);

    @Override
    Fluent<Value> not(Matcher<? super Value> matcher);

    @Override
    Fluent<Value> isNot(Matcher<? super Value> matcher);

    @Override
    Fluent<Value> hasNot(Matcher<? super Value> matcher);

    Fluent<Value> and();

    Fluent<Value> andNot();
    
    Fluent<Value> and(Matcher<? super Value> matcher);

    Fluent<Value> andNot(Matcher<? super Value> matcher);
    
    @Override
    Fluent<Value> all(Matcher<? super Value>... matcher);
    
    @Override
    Fluent<Value> any(Matcher<? super Value>... matcher);
    
    @Override
    Fluent<Value> none(Matcher<? super Value>... matcher);
    
    @Override
    <P> FluentProperty<Value, P> _(MatchValueAdapter<? super Value, P> adapter);
    
    @Override
    <P> FluentProperty<Value, P> has(MatchValueAdapter<? super Value, P> adapter);
    
    @Override
    <P> FluentProperty<Value, P> not(MatchValueAdapter<? super Value, P> adapter);
    
    @Override
    <P> FluentProperty<Value, P> hasNot(MatchValueAdapter<? super Value, P> adapter);

}
