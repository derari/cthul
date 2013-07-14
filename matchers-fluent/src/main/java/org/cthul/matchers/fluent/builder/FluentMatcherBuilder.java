package org.cthul.matchers.fluent.builder;

import java.util.ArrayList;
import java.util.List;
import org.cthul.matchers.chain.AndChainMatcher;
import org.cthul.matchers.chain.ChainFactory;
import org.cthul.matchers.chain.OrChainMatcher;
import org.cthul.matchers.chain.XOrChainMatcher;
import org.cthul.matchers.diagnose.MatcherDescription;
import org.cthul.matchers.diagnose.QuickDiagnosingMatcher;
import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.values.MatchValueAdapter;
import org.cthul.matchers.fluent.values.AdaptingMatcher;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 *
 */
public class FluentMatcherBuilder
                <Value, Match, This extends FluentMatcherBuilder<Value, Match, This>>
                extends AbstractMatcherPropertyBuilder<Value, Value, Match, This, This>
                implements FluentMatcher<Value, Match> {
    
    @Factory
    public static <V> FluentMatcher<V, V> match() {
        return new FluentMatcherBuilder<>(IdentityValue.<V>value());
    }
    
    @Factory
    public static <V> FluentMatcher<V, V> match(Class<V> c) {
        return new FluentMatcherBuilder<>(IdentityValue.<V>value());
    }
    
    @Factory
    public static <V, M> FluentMatcher<V, M> match(MatchValueAdapter<M, V> adapter) {
        return new FluentMatcherBuilder<>(adapter);
    }
    
    private final List<Matcher<? super Value>> matchers = new ArrayList<>();
    private final MatchValueAdapter<Match, Value> matchValueType;
    private ChainFactory chainFactory = null;
    private boolean xorPossible = true;
    
    private QuickDiagnosingMatcher<Match> result = null;

    public FluentMatcherBuilder(MatchValueAdapter<Match, Value> matchValueType) {
        this.matchValueType = matchValueType;
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
    protected This _applyMatcher(Matcher<? super Value> matcher) {
        matchers.add(matcher);
        return _this();
    }

    @Override
    public This and() {
        _and();
        return _this();
    }

    @Override
    public This or() {
        _or();
        return _this();
    }

    @Override
    public This xor() {
        _xor();
        return _this();
    }

    @Override
    public This andNot() {
        _and();
        _not();
        return _this();
    }
    
    @Override
    public This orNot() {
        _or();
        _not();
        return _this();
    }

    @Override
    public This xorNot() {
        _xor();
        _not();
        return _this();
    }

    @Override
    public This and(Matcher<? super Value> matcher) {
        _and();
        return _match(matcher);
    }

    @Override
    public This or(Matcher<? super Value> matcher) {
        _or();
        return _match(matcher);
    }

    @Override
    public This xor(Matcher<? super Value> matcher) {
        _xor();
        return _match(matcher);
    }

    @Override
    public This andNot(Matcher<? super Value> matcher) {
        _and();
        _not();
        return _match(matcher);
    }

    @Override
    public This orNot(Matcher<? super Value> matcher) {
        _or();
        _not();
        return _match(matcher);
    }

    @Override
    public This xorNot(Matcher<? super Value> matcher) {
        _xor();
        _not();
        return _match(matcher);
    }

    @Override
    public QuickDiagnosingMatcher<Match> getMatcher() {
        if (result == null) {
            if (chainFactory == null) {
                and();
            }
            Matcher<Value> m = chainFactory.create(matchers);
            String description = getReason();
            if (description != null) {
                m = new MatcherDescription<>(m, description);
            }
            if (matchValueType instanceof IdentityValue 
                    && m instanceof QuickDiagnosingMatcher) {
                result = (QuickDiagnosingMatcher) m;
            } else {
                result = new AdaptingMatcher<>(matchValueType, m);
            }
        }
        return result;
    }

    @Override
    public boolean matches(Object item, Description mismatch) {
        return getMatcher().matches(item, mismatch);
    }

    @Override
    public boolean matches(Object item) {
        return getMatcher().matches(item);
    }

    @Override
    public void describeMismatch(Object item, Description mismatchDescription) {
        getMatcher().describeMismatch(item, mismatchDescription);
    }

    @Override
    public void describeTo(Description description) {
        getMatcher().describeTo(description);
    }
    
    @Deprecated
    @Override
    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
        // Java needs traits
    }

    @Override
    public String toString() {
        if (result == null) {
            String s = getMatcher().toString();
            result = null;
            return s;
        }
        return getMatcher().toString();
    }
    
}
