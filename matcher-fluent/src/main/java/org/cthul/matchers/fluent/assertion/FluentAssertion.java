package org.cthul.matchers.fluent.assertion;

import org.hamcrest.Matcher;
import org.hamcrest.core.IsNot;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.FluentAssert;

public class FluentAssertion<T> implements FluentAssert<T> {

    private final AssertionStrategy assertionStrategy;
    private final FailureHandler failureHandler;
    private boolean negate = false;

    private String reason = null;

    public FluentAssertion(FailureHandler failureHandler, AssertionStrategy assertionStrategy) {
        this.failureHandler = failureHandler;
        this.assertionStrategy = assertionStrategy;
    }

    public FluentAssertion(FailureHandler failureHandler, Object object) {
        this(failureHandler, new SingleItemStrategy(object));
    }
    
    protected String _reason(Object mismatch) {
        if (reason == null) {
            return null;
        } else {
            return String.format(reason, mismatch);
        }
    }
    
    protected void _as(String reason) {
        this.reason = reason;
    }
    
    protected void _is(Matcher<?> matcher) {
        if (negate) {
            matcher = IsNot.not(matcher);
            negate = false;
        }
        Object mismatch = assertionStrategy.validate(matcher);
        if (mismatch != AssertionStrategy.VALID) {
            failureHandler.mismatch(_reason(mismatch), mismatch, matcher);
        }
    }
    
    protected void _not() {
        negate = !negate;
    }

    @Override
    public Fluent<T> as(String reason) {
        _as(reason);
        return this;
    }

    @Override
    public Fluent<T> as(String reason, Object... args) {
        _as(String.format(reason, args));
        return this;
    }

    @Override
    public Fluent<T> is(Matcher<? super T> matcher) {
        _is(matcher);
        return this;
    }

    @Override
    public Fluent<T> _(Matcher<? super T> matcher) {
        _is(matcher);
        return this;
    }

    @Override
    public Fluent<T> is() {
        return this;
    }

    @Override
    public Fluent<T> and(Matcher<? super T> matcher) {
        _is(matcher);
        return this;
    }

    @Override
    public Fluent<T> and() {
        return this;
    }

    @Override
    public Fluent<T> isNot(Matcher<? super T> matcher) {
        _not();
        _is(matcher);
        return this;
    }

    @Override
    public Fluent<T> not(Matcher<? super T> matcher) {
        _not();
        _is(matcher);
        return this;
    }

    @Override
    public Fluent<T> not() {
        _not();
        return this;
    }

    @Override
    public Fluent<T> andNot(Matcher<? super T> matcher) {
        _not();
        _is(matcher);
        return this;
    }

    @Override
    public Fluent<T> andNot() {
        _not();
        return this;
    }

    @Override
    public Fluent<T> all(Matcher<? super T>... matchers) {
        boolean n = negate;
        for (Matcher<? super T> m: matchers) {
            negate = n;
            _is(m);
        }
        return this;
    }
    
}
