package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.Fluent;
import org.hamcrest.Matcher;

/**
 *
 */
@Deprecated
public abstract class AbstractFluentBuilder
                <Value, This extends AbstractFluentBuilder<Value, This>> 
                extends AbstractFluentPropertyBuilder<Value, Value, This, This>
                implements Fluent<Value> {
    
    public AbstractFluentBuilder() {
    }

    protected void _and() {}
      
    @Override
    @SuppressWarnings("unchecked")
    protected This _thisFluent() {
        return (This) this;
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

}
