package org.cthul.matchers.fluent;

import org.cthul.matchers.diagnose.QuickDiagnosingMatcher;
import org.cthul.matchers.fluent.property.FluentMatcherProperty;
import org.hamcrest.Matcher;

/**
 * Fluent builder for matchers.
 * @param <Value> 
 */
public interface FluentMatcher<Value, Match> 
                extends Fluent<Value>, 
                        FluentMatcherProperty<Value, Value, Match>,
                        QuickDiagnosingMatcher<Match> {

     /*
     valueThat().is(a()).or(b());
     
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
    FluentMatcher<Value, Match> as(String reason);

    @Override
    FluentMatcher<Value, Match> as(String reason, Object... args);

    @Override
    FluentMatcher<Value, Match> is();
    
    @Override
    FluentMatcher<Value, Match> has();

    @Override
    FluentMatcher<Value, Match> not();
    
    @Override
    FluentMatcher<Value, Match> _(Matcher<? super Value> matcher);

    @Override
    FluentMatcher<Value, Match> is(Matcher<? super Value> matcher);

    @Override
    FluentMatcher<Value, Match> has(Matcher<? super Value> matcher);

    @Override
    FluentMatcher<Value, Match> not(Matcher<? super Value> matcher);

    @Override
    FluentMatcher<Value, Match> isNot(Matcher<? super Value> matcher);

    @Override
    FluentMatcher<Value, Match> hasNot(Matcher<? super Value> matcher);

    @Override
    FluentMatcher<Value, Match> and();

    @Override
    FluentMatcher<Value, Match> andNot();
    
    FluentMatcher<Value, Match> or();

    FluentMatcher<Value, Match> orNot();
    
    FluentMatcher<Value, Match> xor();

    FluentMatcher<Value, Match> xorNot();
    
    @Override
    FluentMatcher<Value, Match> and(Matcher<? super Value> matcher);

    @Override
    FluentMatcher<Value, Match> andNot(Matcher<? super Value> matcher);
    
    FluentMatcher<Value, Match> or(Matcher<? super Value> matcher);

    FluentMatcher<Value, Match> orNot(Matcher<? super Value> matcher);
    
    FluentMatcher<Value, Match> xor(Matcher<? super Value> matcher);

    FluentMatcher<Value, Match> xorNot(Matcher<? super Value> matcher);
    
    @Override
    FluentMatcher<Value, Match> all(Matcher<? super Value>... matcher);
    
    @Override
    FluentMatcher<Value, Match> any(Matcher<? super Value>... matcher);
    
    @Override
    FluentMatcher<Value, Match> none(Matcher<? super Value>... matcher);
    
    QuickDiagnosingMatcher<Match> getMatcher();
    
}
