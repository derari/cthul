package org.cthul.matchers.fluent.builder;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;
import org.cthul.matchers.diagnose.MatcherDescription;
import org.cthul.matchers.chain.AndChainMatcher;
import org.cthul.matchers.chain.ChainFactory;
import org.cthul.matchers.chain.OrChainMatcher;
import org.cthul.matchers.chain.XOrChainMatcher;
import org.cthul.matchers.diagnose.QuickDiagnosingMatcherBase;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.FluentMatcher;
import org.hamcrest.core.IsNot;

/**
 * Default implementation of FluentMatcher.
 * 
 * @author derari
 * @param <T> 
 */
public class FluentBuilder<T> 
        extends QuickDiagnosingMatcherBase<T> 
        implements FluentMatcher<T> {

    private List<Matcher<? super T>> matchers = new ArrayList<>();
    private ChainFactory chainFactory = null;
    private boolean xorPossible = true;
    private boolean negated = false;
    
    private String description = null;
    private Matcher<T> result = null;

    public FluentBuilder() {
    }
    
    protected void modify() {
        if (result != null) {
            throw new IllegalStateException("Matcher already created");
        }
    }
    
    protected void ensureChain(ChainFactory f) {
        if (chainFactory == null) {
            chainFactory = f;
        } else if (chainFactory != f) {
            throw new IllegalStateException(
                    "Chain type is " + chainFactory + 
                    ", but required was " + f);
        }
    }

    protected void _as(String reason) {
        this.description = reason;
    }
    
    protected void _is(Matcher<? super T> matcher) {
        modify();
        if (matcher instanceof FluentMatcher) {
            matcher = ((FluentMatcher<? super T>) matcher).getMatcher();
        }
        if (negated) {
            matcher = IsNot.not(matcher);
            negated = false;
        }
        matchers.add(matcher);
    }

    protected void _not() {
        negated = !negated;
    }
    
    protected void _and() {
        ensureChain(AndChainMatcher.FACTORY);
    }
    
    protected void _either() {
        ensureChain(OrChainMatcher.FACTORY);
    }
    
    protected void _or() {
        ensureChain(OrChainMatcher.FACTORY);
        xorPossible = false;
    }
    
    protected void _xor() {
        if (xorPossible && chainFactory == OrChainMatcher.FACTORY) {
            chainFactory = XOrChainMatcher.FACTORY;
        } else {
            ensureChain(XOrChainMatcher.FACTORY);
        }
    }
    
    @Override
    public FluentMatcher<T> as(String reason) {
        _as(reason);
        return this;
    }

    @Override
    public FluentMatcher<T> as(String reason, Object... args) {
        _as(String.format(reason, args));
        return this;
    }
    
    @Override
    public FluentMatcher<T> is(Matcher<? super T> matcher) {
        _is(matcher);
        return this;
    }

    @Override
    public FluentMatcher<T> _(Matcher<? super T> matcher) {
        _is(matcher);
        return this;
    }

    @Override
    public FluentMatcher is() {
        return this;
    }

    @Override
    public FluentMatcher and(Matcher<? super T> matcher) {
        _and();
        _is(matcher);
        return this;
    }

    @Override
    public FluentMatcher and() {
        _and();
        return this;
    }

    @Override
    public FluentMatcher isNot(Matcher<? super T> matcher) {
        _not();
        _is(matcher);
        return this;
    }

    @Override
    public FluentMatcher not(Matcher<? super T> matcher) {
        _not();
        _is(matcher);
        return this;
    }

    @Override
    public FluentMatcher not() {
        _not();
        return this;
    }

    @Override
    public FluentMatcher andNot(Matcher<? super T> matcher) {
        _and();
        _not();
        _is(matcher);
        return this;
    }

    @Override
    public FluentMatcher andNot() {
        _and();
        _not();
        return this;
    }

    @Override
    public FluentMatcher either(Matcher<? super T> matcher) {
        _either();
        _is(matcher);
        return this;
    }

    @Override
    public FluentMatcher either() {
        _either();
        return this;
    }

    @Override
    public FluentMatcher xor(Matcher<? super T> matcher) {
        _xor();
        _is(matcher);
        return this;
    }

    @Override
    public FluentMatcher xor() {
        _xor();
        return this;
    }

    @Override
    public FluentMatcher or(Matcher<? super T> matcher) {
        _or();
        _is(matcher);
        return this;
    }

    @Override
    public FluentMatcher or() {
        _or();
        return this;
    }

    @Override
    public FluentMatcher orNot(Matcher<? super T> matcher) {
        _or();
        _not();
        _is(matcher);
        return this;
    }

    @Override
    public FluentMatcher orNot() {
        _or();
        _not();
        return this;
    }

    @Override
    public Fluent all(Matcher<? super T>... matcher) {
        _and();
        for (Matcher<? super T> m: matcher) {
            _is(m);
        }
        return this;
    }

    @Override
    public Matcher<T> getMatcher() {
        if (result == null) {
            if (chainFactory == null) {
                and();
            }
            Matcher<T> m = chainFactory.create(matchers);
            if (description != null) {
                m = new MatcherDescription<>(m, description);
            }
            result = m;
        }
        return result;
    }

    @Override
    public boolean matches(Object o) {
        return getMatcher().matches(o);
    }

    @Override
    public void describeTo(Description d) {
        getMatcher().describeTo(d);
    }

    @Override
    public boolean matches(Object item, Description mismatch) {
        return quickMatch(getMatcher(), item, mismatch);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        getMatcher().describeMismatch(item, description);
    }

}