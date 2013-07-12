package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.chain.AndChainMatcher;
import org.cthul.matchers.chain.OrChainMatcher;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.property.FluentProperty;
import org.cthul.matchers.fluent.values.MatchValueType;
import org.cthul.matchers.fluent.values.ValueMatcher;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class AbstractFluentPropertyBuilder
                <Value, Property, ThisFluent extends Fluent<Value>,
                 This extends AbstractFluentPropertyBuilder<Value, Property, ThisFluent, This>>
                implements FluentProperty<Value, Property> {
    
    private boolean negate = false;
    private String prefix = null;
    private String reason = null;

    public AbstractFluentPropertyBuilder() {
    }

    protected String getReason() {
        return reason;
    }
    
    protected void _as(String reason) {
        this.reason = reason;
    }
    
    protected void _is() {
        prefix = "is";
    }
    
    protected void _has() {
        prefix = "has";
    }
    
    protected void _not() {
        negate = !negate;
    }

    protected void _match(Matcher<? super Property> matcher) {
        if (negate) {
            matcher = new FNot<>(prefix, matcher);
            negate = false;
        } else if (prefix != null) {
            matcher = new FIs<>(prefix, matcher);
            prefix = null;
        }
        applyMatcher(matcher);
    }
    
    protected abstract void applyMatcher(Matcher<? super Property> matcher);
    
    @SuppressWarnings("unchecked")
    protected This _this() {
        return (This) this;
    }
    
    protected abstract ThisFluent _thisFluent();
    
    protected abstract <P> FluentProperty<Value, P> _newProperty(MatchValueType<? super Property, P> adapter);
    
    @Override
    public This as(String reason) {
        _as(reason);
        return _this();
    }

    @Override
    public This as(String reason, Object... args) {
        _as(String.format(reason, args));
        return _this();
    }

    @Override
    public This is() {
        _is();
        return _this();
    }

    @Override
    public This has() {
        _has();
        return _this();
    }

    @Override
    public This not() {
        _not();
        return _this();
    }

    @Override
    public ThisFluent _(Matcher<? super Property> matcher) {
        _match(matcher);
        return _thisFluent();
    }

    @Override
    public ThisFluent is(Matcher<? super Property> matcher) {
        _is();
        _match(matcher);
        return _thisFluent();
    }

    @Override
    public ThisFluent has(Matcher<? super Property> matcher) {
        _has();
        _match(matcher);
        return _thisFluent();
    }

    @Override
    public ThisFluent not(Matcher<? super Property> matcher) {
        _not();
        _match(matcher);
        return _thisFluent();
    }

    @Override
    public ThisFluent isNot(Matcher<? super Property> matcher) {
        _is();
        _not();
        _match(matcher);
        return _thisFluent();
    }

    @Override
    public ThisFluent hasNot(Matcher<? super Property> matcher) {
        _has();
        _not();
        _match(matcher);
        return _thisFluent();
    }

    @Override
    public ThisFluent all(Matcher<? super Property>... matcher) {
        _match(AndChainMatcher.all(matcher));
        return _thisFluent();
    }

    @Override
    public ThisFluent any(Matcher<? super Property>... matcher) {
        _match(OrChainMatcher.or(matcher));
        return _thisFluent();
    }

    @Override
    public ThisFluent none(Matcher<? super Property>... matcher) {
        _not();
        _match(OrChainMatcher.or(matcher));
        return _thisFluent();
    }

    @Override
    public <P> FluentProperty<Value, P> _(MatchValueType<? super Property, P> adapter) {
        return _newProperty(adapter);
    }
    
    @Override
    public <P> FluentProperty<Value, P> not(MatchValueType<? super Property, P> adapter) {
        _not();
        return _newProperty(adapter);
    }
    
    @Override
    public <P> FluentProperty<Value, P> has(MatchValueType<? super Property, P> adapter) {
        _has();
        return _newProperty(adapter);
    }
    
    @Override
    public <P> FluentProperty<Value, P> hasNot(MatchValueType<? super Property, P> adapter) {
        _has();
        _not();
        return _newProperty(adapter);
    }
    
    protected static abstract class InternPropertyBuilder
                    <Value, BaseProperty, ThisFluent extends Fluent<Value>, 
                     Property, This2 extends InternPropertyBuilder<Value, BaseProperty, ThisFluent, Property, This2>> 
                    extends AbstractFluentPropertyBuilder<Value, Property, ThisFluent, This2> {
        
        private final AbstractFluentPropertyBuilder<Value, BaseProperty, ThisFluent, ?> baseFluent;
        private final MatchValueType<? super BaseProperty, Property> adapter;
        private boolean used = false;

        public InternPropertyBuilder(AbstractFluentPropertyBuilder<Value, BaseProperty, ThisFluent, ?> baseFluent, MatchValueType<? super BaseProperty, Property> adapter) {
            this.baseFluent = baseFluent;
            this.adapter = adapter;
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
