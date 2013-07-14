package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.property.FluentMatcherProperty;
import org.cthul.matchers.fluent.values.AdaptingMatcher;
import org.cthul.matchers.fluent.values.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class AbstractMatcherPropertyBuilder
                <Value, Property, Match, ThisFluent extends FluentMatcher<Value, Match>,
                 This extends AbstractMatcherPropertyBuilder<Value, Property, Match, ThisFluent, This>>
                extends AbstractFluentPropertyBuilder<Value, Property, ThisFluent, This>
                implements FluentMatcherProperty<Value, Property, Match> {

    @Override
    protected <P> FluentMatcherProperty<Value, P, Match> _newProperty(MatchValueAdapter<? super Property, P> adapter) {
        return new MatcherPropertyBuilder<>(adapter);
    }

    @Override
    public <P> FluentMatcherProperty<Value, P, Match> _(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentMatcherProperty) super._(adapter);
    }
    
    @Override
    public <P> FluentMatcherProperty<Value, P, Match> has(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentMatcherProperty) super.has(adapter);
    }
    
    @Override
    public <P> FluentMatcherProperty<Value, P, Match> not(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentMatcherProperty) super.not(adapter);
    }
    
    @Override
    public <P> FluentMatcherProperty<Value, P, Match> hasNot(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentMatcherProperty) super.hasNot(adapter);
    }
    
    protected class MatcherPropertyBuilder
                    <Property2, This2 extends MatcherPropertyBuilder<Property2, This2>>
                    extends AbstractMatcherPropertyBuilder<Value, Property2, Match, ThisFluent, This2> 
                    implements FluentMatcherProperty<Value, Property2, Match> {
        
        private final MatchValueAdapter<? super Property, Property2> adapter;
        private boolean used = false;

        public MatcherPropertyBuilder(MatchValueAdapter<? super Property, Property2> adapter) {
            this.adapter = adapter;
        }

        @Override
        protected ThisFluent _applyMatcher(Matcher<? super Property2> matcher) {
            if (used) {
                throw new IllegalStateException("Property already checked");
            }
            used = true;
            Matcher<? super Property> m = new AdaptingMatcher<>(adapter, matcher);
            return AbstractMatcherPropertyBuilder.this._match(m);
        }
    }
}
