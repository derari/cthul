package org.cthul.matchers.fluent.builder;

import static org.cthul.matchers.fluent.AssertUtils.ASSERT;
import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.property.FluentAssertProperty;
import org.cthul.matchers.fluent.values.MatchValueType;
import org.cthul.matchers.fluent.values.MatchValues;
import org.cthul.matchers.fluent.values.MatchingObject;
import org.cthul.matchers.fluent.values.ValueMatcher;
import org.cthul.matchers.fluent.values.types.IdentityValueType;
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
    public static <T> FluentAssert<T> assertThat(MatchingObject<T> object) {
        return new FluentAssertBuilder<>(ASSERT, object);
    }

    @Factory
    public static <T> FluentAssert<T> assertThat(String reason, T object) {
        return assertThat(object).as(reason);
    }

    @Factory
    public static <T> FluentAssert<T> assertThat(String reason, MatchingObject<T> object) {
        return assertThat(object).as(reason);
    }
    
    private final MatchingObject<Value> matchingObject;
    private final FailureHandler failureHandler;

    public FluentAssertBuilder(FailureHandler failureHandler, MatchingObject<Value> matchingObject) {
        this.matchingObject = matchingObject;
        this.failureHandler = failureHandler;
    }

    public FluentAssertBuilder(FailureHandler failureHandler, Value item) {
        this.matchingObject = IdentityValueType.value(item);
        this.failureHandler = failureHandler;
    }

    @Override
    protected <P> FluentAssertProperty<Value, P> _newProperty(MatchValueType<? super Value, P> adapter) {
        return new AssertPropertyBuilder<>(this, adapter);
    }
    
    protected void _and() {}

    @Override
    protected This _thisFluent() {
        return (This) this;
    }

    @Override
    protected void applyMatcher(Matcher<? super Value> matcher) {
        MatchValues<Value> values = matchingObject.values();
        while (values.hasNext()) {
            boolean match = matcher.matches(values.next());
            values.result(match);
        }
        if (!values.matched()) {
            failureHandler.mismatch(getReason(), values, matcher);
        }
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
        _match(matcher);
        return _this();
    }

    @Override
    public This andNot(Matcher<? super Value> matcher) {
        _and();
        _not();
        _match(matcher);
        return _this();
    }

    protected static class AssertPropertyBuilder
                    <Value, BaseProperty, ThisFluent extends FluentAssert<Value>, 
                     Property, This extends AssertPropertyBuilder<Value, BaseProperty, ThisFluent, Property, This>>
                    extends AbstractAssertPropertyBuilder<Value, Property, ThisFluent, This> 
                    implements FluentAssertProperty<Value, Property> {
        
        private final AbstractAssertPropertyBuilder<Value, BaseProperty, ThisFluent, ?> baseFluent;
        private final MatchValueType<? super BaseProperty, Property> adapter;
        private boolean used = false;

        public AssertPropertyBuilder(AbstractAssertPropertyBuilder<Value, BaseProperty, ThisFluent, ?> baseFluent, MatchValueType<? super BaseProperty, Property> adapter) {
            this.baseFluent = baseFluent;
            this.adapter = adapter;
        }

        @Override
        protected <P> FluentAssertProperty<Value, P> _newProperty(MatchValueType<? super Property, P> adapter) {
            return new AssertPropertyBuilder<>(this, adapter);
        }

        @Override
        protected void applyMatcher(Matcher<? super Property> matcher) {
            if (used) {
                throw new IllegalStateException("Property already checked");
            }
            used = true;
            Matcher<? super BaseProperty> m = new ValueMatcher<>(matcher, adapter);
            baseFluent._match(m);
        }

        @Override
        protected ThisFluent _thisFluent() {
            return baseFluent._thisFluent();
        }
        
    }
    
}
