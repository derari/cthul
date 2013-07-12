package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.property.FluentAssertProperty;
import org.cthul.matchers.fluent.values.MatchValueType;

/**
 *
 */
public abstract class AbstractAssertPropertyBuilder
                <Value, Property, ThisFluent extends FluentAssert<Value>,
                 This extends AbstractAssertPropertyBuilder<Value, Property, ThisFluent, This>>
                extends AbstractFluentPropertyBuilder<Value, Property, ThisFluent, This>
                implements FluentAssertProperty<Value, Property> {

    @Override
    protected abstract <P> FluentAssertProperty<Value, P> _newProperty(MatchValueType<? super Property, P> adapter);

    @Override
    public <P> FluentAssertProperty<Value, P> _(MatchValueType<? super Property, P> adapter) {
        return (FluentAssertProperty) super._(adapter);
    }
    
    @Override
    public <P> FluentAssertProperty<Value, P> has(MatchValueType<? super Property, P> adapter) {
        return (FluentAssertProperty) super.has(adapter);
    }
    
    @Override
    public <P> FluentAssertProperty<Value, P> not(MatchValueType<? super Property, P> adapter) {
        return (FluentAssertProperty) super.not(adapter);
    }
    
    @Override
    public <P> FluentAssertProperty<Value, P> hasNot(MatchValueType<? super Property, P> adapter) {
        return (FluentAssertProperty) super.hasNot(adapter);
    }
    
}
