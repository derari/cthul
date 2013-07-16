package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.chain.AndChainMatcher;
import org.cthul.matchers.chain.OrChainMatcher;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.property.FluentProperty;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
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

    protected ThisFluent _match(Matcher<? super Property> matcher) {
        if (negate) {
            matcher = new FNot<>(prefix, matcher);
            prefix = null;
            negate = false;
        } else if (prefix != null) {
            matcher = new FIs<>(prefix, matcher);
            prefix = null;
        }
        return _applyMatcher(matcher);
    }
    
    protected abstract ThisFluent _applyMatcher(Matcher<? super Property> matcher);
    
    @SuppressWarnings("unchecked")
    protected This _this() {
        return (This) this;
    }
    
    protected abstract <P> FluentProperty<Value, P> _newProperty(MatchValueAdapter<? super Property, P> adapter);
    
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
        return _match(matcher);
    }

    @Override
    public ThisFluent is(Matcher<? super Property> matcher) {
        _is();
        return _match(matcher);
    }

    @Override
    public ThisFluent has(Matcher<? super Property> matcher) {
        _has();
        return _match(matcher);
    }

    @Override
    public ThisFluent not(Matcher<? super Property> matcher) {
        _not();
        return _match(matcher);
    }

    @Override
    public ThisFluent isNot(Matcher<? super Property> matcher) {
        _is();
        _not();
        return _match(matcher);
    }

    @Override
    public ThisFluent hasNot(Matcher<? super Property> matcher) {
        _has();
        _not();
        return _match(matcher);
    }

    @Override
    public ThisFluent all(Matcher<? super Property>... matcher) {
        return _match(AndChainMatcher.all(matcher));
    }

    @Override
    public ThisFluent any(Matcher<? super Property>... matcher) {
        return _match(OrChainMatcher.or(matcher));
    }

    @Override
    public ThisFluent none(Matcher<? super Property>... matcher) {
        _not();
        return _match(OrChainMatcher.or(matcher));
    }

    @Override
    public <P> FluentProperty<Value, P> _(MatchValueAdapter<? super Property, P> adapter) {
        return _newProperty(adapter);
    }
    
    @Override
    public <P> FluentProperty<Value, P> not(MatchValueAdapter<? super Property, P> adapter) {
        _not();
        return _newProperty(adapter);
    }
    
    @Override
    public <P> FluentProperty<Value, P> has(MatchValueAdapter<? super Property, P> adapter) {
        _has();
        return _newProperty(adapter);
    }
    
    @Override
    public <P> FluentProperty<Value, P> hasNot(MatchValueAdapter<? super Property, P> adapter) {
        _has();
        _not();
        return _newProperty(adapter);
    }
}
