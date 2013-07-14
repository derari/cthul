package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.property.FluentAssertProperty;
import org.cthul.matchers.fluent.values.AdaptingMatcher;
import org.cthul.matchers.fluent.values.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class AbstractAssertPropertyBuilder
                <Value, Property, ThisFluent extends FluentAssert<Value>,
                 This extends AbstractAssertPropertyBuilder<Value, Property, ThisFluent, This>>
                extends AbstractFluentPropertyBuilder<Value, Property, ThisFluent, This>
                implements FluentAssertProperty<Value, Property> {

    @Override
    protected <P> FluentAssertProperty<Value, P> _newProperty(MatchValueAdapter<? super Property, P> adapter) {
        return new AssertPropertyBuilder<>(adapter);
    }

    @Override
    public <P> FluentAssertProperty<Value, P> _(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentAssertProperty) super._(adapter);
    }
    
    @Override
    public <P> FluentAssertProperty<Value, P> has(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentAssertProperty) super.has(adapter);
    }
    
    @Override
    public <P> FluentAssertProperty<Value, P> not(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentAssertProperty) super.not(adapter);
    }
    
    @Override
    public <P> FluentAssertProperty<Value, P> hasNot(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentAssertProperty) super.hasNot(adapter);
    }
    
    protected class AssertPropertyBuilder
                    <Property2, This2 extends AssertPropertyBuilder<Property2, This2>>
                    extends AbstractAssertPropertyBuilder<Value, Property2, ThisFluent, This2> 
                    implements FluentAssertProperty<Value, Property2> {
        
        private final MatchValueAdapter<? super Property, Property2> adapter;
        private boolean used = false;

        public AssertPropertyBuilder(MatchValueAdapter<? super Property, Property2> adapter) {
            this.adapter = adapter;
        }

        @Override
        protected ThisFluent _applyMatcher(Matcher<? super Property2> matcher) {
            if (used) {
                throw new IllegalStateException("Property already checked");
            }
            used = true;
            Matcher<? super Property> m = new AdaptingMatcher<>(adapter, matcher);
            return AbstractAssertPropertyBuilder.this._match(m);
        }
    }
}
