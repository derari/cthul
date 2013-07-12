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
import org.cthul.matchers.fluent.property.FluentMatcherProperty;
import org.cthul.matchers.fluent.values.MatchValueType;
import org.cthul.matchers.fluent.values.ValueMatcher;
import org.cthul.matchers.fluent.values.types.IdentityValueType;
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
        return new FluentMatcherBuilder<>(IdentityValueType.<V>value());
    }
    
    @Factory
    public static <V> FluentMatcher<V, V> match(Class<V> c) {
        return new FluentMatcherBuilder<>(IdentityValueType.<V>value());
    }
    
    @Factory
    public static <V, M> FluentMatcher<V, M> match(MatchValueType<M, V> adapter) {
        return new FluentMatcherBuilder<>(adapter);
    }
    
    private final List<Matcher<? super Value>> matchers = new ArrayList<>();
    private final MatchValueType<Match, Value> matchValueType;
    private ChainFactory chainFactory = null;
    private boolean xorPossible = true;
    
    private QuickDiagnosingMatcher<Match> result = null;

    public FluentMatcherBuilder(MatchValueType<Match, Value> matchValueType) {
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

    @Override
    protected This _thisFluent() {
        return (This) this;
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
    protected <P> FluentMatcherProperty<Value, P, Match> _newProperty(MatchValueType<? super Value, P> adapter) {
        return new MatcherPropertyBuilder<>(this, adapter);
    }

    @Override
    protected void applyMatcher(Matcher<? super Value> matcher) {
        matchers.add(matcher);
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
        _match(matcher);
        return _this();
    }

    @Override
    public This or(Matcher<? super Value> matcher) {
        _or();
        _match(matcher);
        return _this();
    }

    @Override
    public This xor(Matcher<? super Value> matcher) {
        _xor();
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

    @Override
    public This orNot(Matcher<? super Value> matcher) {
        _or();
        _not();
        _match(matcher);
        return _this();
    }

    @Override
    public This xorNot(Matcher<? super Value> matcher) {
        _xor();
        _not();
        _match(matcher);
        return _this();
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
            if (matchValueType instanceof IdentityValueType 
                    && m instanceof QuickDiagnosingMatcher) {
                result = (QuickDiagnosingMatcher) m;
            } else {
                result = new ValueMatcher<>(m, matchValueType);
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
    
    protected static class MatcherPropertyBuilder
                    <Value, BaseProperty, Match, ThisFluent extends FluentMatcher<Value, Match>, 
                     Property, This extends MatcherPropertyBuilder<Value, BaseProperty, Match, ThisFluent, Property, This>>
                    extends AbstractMatcherPropertyBuilder<Value, Property, Match, ThisFluent, This> 
                    implements FluentMatcherProperty<Value, Property, Match> {
        
        private final AbstractMatcherPropertyBuilder<Value, BaseProperty, Match, ThisFluent, ?> baseFluent;
        private final MatchValueType<? super BaseProperty, Property> adapter;
        private boolean used = false;

        public MatcherPropertyBuilder(AbstractMatcherPropertyBuilder<Value, BaseProperty, Match, ThisFluent, ?> baseFluent, MatchValueType<? super BaseProperty, Property> adapter) {
            this.baseFluent = baseFluent;
            this.adapter = adapter;
        }

        @Override
        protected <P> FluentMatcherProperty<Value, P, Match> _newProperty(MatchValueType<? super Property, P> adapter) {
            return new MatcherPropertyBuilder<>(this, adapter);
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
