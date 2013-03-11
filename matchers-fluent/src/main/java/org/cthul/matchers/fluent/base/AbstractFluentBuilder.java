package org.cthul.matchers.fluent.base;

import org.cthul.matchers.chain.ChainBuilder;
import org.cthul.matchers.chain.ChainFactory;
import org.cthul.matchers.fluent.Fluent;
import org.hamcrest.Matcher;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractFluentBuilder<Item, This extends AbstractFluentBuilder<Item, This>> extends ChainBuilder<Item> implements Fluent<Item> {
    
    private boolean negate = false;
    private boolean is = false;
    private String reason = null;

    public AbstractFluentBuilder() {
        super(null);
    }

    @Override
    protected ChainFactory factory() {
        throw new UnsupportedOperationException();
    }

    protected String getReason() {
        return reason;
    }
    
    protected void _as(String reason) {
        this.reason = reason;
    }
    
    protected void _is() {
        is = true;
    }
    
    protected void _not() {
        negate = !negate;
    }

    protected void _and() {}
    
    protected void _match(Matcher<? super Item> matcher) {
        if (negate) {
            matcher = new FNot<>(is, matcher);
            negate = is = false;
        } else if (is) {
            matcher = new FIs<>(matcher);
            is = false;
        }
        applyMatcher(matcher);
    }
    
    protected abstract void applyMatcher(Matcher<? super Item> matcher);
    
    @SuppressWarnings("unchecked")
    protected This _this() {
        return (This) this;
    }
    
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
    public This is(Matcher<? super Item> matcher) {
        _is();
        _match(matcher);
        return _this();
    }

    @Override
    public This _(Matcher<? super Item> matcher) {
        _match(matcher);
        return _this();
    }

    @Override
    public This is() {
        _is();
        return _this();
    }

    @Override
    public This and(Matcher<? super Item> matcher) {
        _and();
        _match(matcher);
        return _this();
    }

    @Override
    public This and() {
        _and();
        return _this();
    }

    @Override
    public This isNot(Matcher<? super Item> matcher) {
        _is();
        _not();
        _match(matcher);
        return _this();
    }

    @Override
    public This not(Matcher<? super Item> matcher) {
        _not();
        _match(matcher);
        return _this();
    }

    @Override
    public This not() {
        _not();
        return _this();
    }

    @Override
    public This andNot(Matcher<? super Item> matcher) {
        _and();
        _not();
        _match(matcher);
        return _this();
    }

    @Override
    public This andNot() {
        _and();
        _not();
        return _this();
    }

    @Override
    public This all(Matcher<? super Item>... matchers) {
        for (Matcher<? super Item> m: matchers) {
            _and();
            _match(m);
        }
        return _this();
    }
    
}
