package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.property.FluentMatcherProperty;
import org.cthul.matchers.fluent.values.MatchValueType;

/**
 *
 */
public abstract class AbstractMatcherPropertyBuilder
                <Value, Property, Match, ThisFluent extends FluentMatcher<Value, Match>,
                 This extends AbstractMatcherPropertyBuilder<Value, Property, Match, ThisFluent, This>>
                extends AbstractFluentPropertyBuilder<Value, Property, ThisFluent, This>
                implements FluentMatcherProperty<Value, Property, Match> {

    @Override
    protected abstract <P> FluentMatcherProperty<Value, P, Match> _newProperty(MatchValueType<? super Property, P> adapter);

    @Override
    public <P> FluentMatcherProperty<Value, P, Match> _(MatchValueType<? super Property, P> adapter) {
        return (FluentMatcherProperty) super._(adapter);
    }
    
}
