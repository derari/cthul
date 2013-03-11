package org.cthul.matchers.fluent.assertion;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.base.AbstractFluentBuilder;
import org.cthul.matchers.fluent.strategy.MatchingObject;
import org.cthul.matchers.fluent.strategy.SingleItemStrategy;
import org.hamcrest.Matcher;

/**
 *
 * @author Arian Treffer
 */
public class FluentAssertBuilder<Item, This extends FluentAssertBuilder<Item, This>> 
                extends AbstractFluentBuilder<Item, This> 
                implements FluentAssert<Item> {
    
    private final MatchingObject<Item> matchingObject;
    private final FailureHandler failureHandler;

    public FluentAssertBuilder(FailureHandler failureHandler, MatchingObject<Item> matchingObject) {
        this.matchingObject = matchingObject;
        this.failureHandler = failureHandler;
    }

    public FluentAssertBuilder(FailureHandler failureHandler, Item item) {
        this.matchingObject = SingleItemStrategy.applyTo(item);
        this.failureHandler = failureHandler;
    }

    @Override
    protected void applyMatcher(Matcher<? super Item> matcher) {
        Object m = matchingObject.validate(matcher);
        if (m != MatchingObject.VALID){
            failureHandler.mismatch(getReason(), m, matcher);
        }
    }
    
}
