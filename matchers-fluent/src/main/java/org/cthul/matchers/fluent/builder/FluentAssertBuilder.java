package org.cthul.matchers.fluent.builder;

import static org.cthul.matchers.fluent.AssertUtils.ASSERT;
import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.property.FluentAssertProperty;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.cthul.matchers.fluent.value.ElementMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Implements {@link FluentAssert}.
 */
public class FluentAssertBuilder<Value, This extends FluentAssertBuilder<Value, This>> 
                extends AbstractAssertPropertyBuilder<Value, Value, This, This>
                implements FluentAssert<Value> {
    
    @Factory
    public static <T> FluentAssert<T> assertThat(T object) {
        return new FluentAssertBuilder<>(ASSERT, object);
    }

    @Factory
    public static <T> FluentAssert<T> assertThat(MatchValue<T> object) {
        return new FluentAssertBuilder<>(ASSERT, object);
    }

    @Factory
    public static <T> FluentAssert<T> assertThat(String reason, T object) {
        return assertThat(object).as(reason);
    }

    @Factory
    public static <T> FluentAssert<T> assertThat(String reason, MatchValue<T> object) {
        return assertThat(object).as(reason);
    }
    
    private final MatchValue<Value> matchValue;
    private final FailureHandler failureHandler;

    public FluentAssertBuilder(FailureHandler failureHandler, MatchValue<Value> matchValues) {
        this.matchValue = matchValues;
        this.failureHandler = failureHandler;
    }

    public FluentAssertBuilder(FailureHandler failureHandler, Value item) {
        this.matchValue = IdentityValue.value(item);
        this.failureHandler = failureHandler;
    }
    
    protected void _and() {}

    @Override
    protected This _applyMatcher(Matcher<? super Value> matcher) {
        MatchValue.ElementMatcher<Value> m = new ElementMatcher<>(matcher);
        if (!matchValue.matches(m)) {
            failureHandler.mismatch(getReason(), matchValue, m);
        }
        return _this();
    }
    
    @Override
    public This and() {
        _and();
        return _this();
    }

    @Override
    public This andNot() {
        _and();
        _not();
        return _this();
    }
    
    @Override
    public This and(Matcher<? super Value> matcher) {
        _and();
        return _match(matcher);
    }

    @Override
    public This andNot(Matcher<? super Value> matcher) {
        _and();
        _not();
        return _match(matcher);
    }

    @Override
    public <P> FluentAssertProperty<Value, P> and(MatchValueAdapter<? super Value, P> adapter) {
        _and();
        return _newProperty(adapter);
    }

    @Override
    public <P> FluentAssertProperty<Value, P> andNot(MatchValueAdapter<? super Value, P> adapter) {
        _and();
        _not();
        return _newProperty(adapter);
    }
    
}
