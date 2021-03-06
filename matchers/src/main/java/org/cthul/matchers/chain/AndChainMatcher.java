package org.cthul.matchers.chain;

import java.util.*;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResult.Match;
import org.hamcrest.*;

/**
 * Conjunction of multiple matchers.
 * 
 * @param <T> 
 */
public class AndChainMatcher<T> extends MatcherChainBase<T> {

    public AndChainMatcher(Collection<? extends Matcher<? super T>> matchers) {
        super(matchers);
    }

    @SuppressWarnings("unchecked")
    public AndChainMatcher(Matcher<? super T>... matchers) {
        super(matchers);
    }

    @Override
    public int getDescriptionPrecedence() {
        return Nested.pAtomicUnaryOr(P_AND, matchers.length);
    }
    
    /** {@inheritDoc} */
    @Override
    public void describeTo(Description description) {
        if (matchers.length == 0) {
            description.appendText("<anything>");
        } else {
            Nested.joinDescriptions(getDescriptionPrecedence(), matchersList(), description, " and ");
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item) {
        for (Matcher<?> m: matchers) {
            if (!m.matches(item)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public <I> MatchResult<I> matchResult(I item) {
        List<MatchResult.Match<I>> results = new ArrayList<>(matchers.length);
        for (Matcher<?> m: matchers) {
            MatchResult<I> mr = quickMatchResult(m, item);
            if (!mr.matched()) {
                return failResult(item, mr.getMismatch());
            }
            results.add(mr.getMatch());
        }
        return successResult(item, results);
    }
    
    private <I> MatchResult<I> successResult(I item, final List<Match<I>> results) {
        return new NestedMatch<I, AndChainMatcher<T>>(item, this) {
            @Override
            public void describeMatch(Description description) {
                if (results.isEmpty()) {
                    describeMatcher(description);
                } else {
                    Nested.joinMatchDescriptions(getMatchPrecedence(), results, description, " and ");
                }
            }
        };
    }
    
    private <I> MatchResult<I> failResult(I item, final MatchResult.Mismatch<I> nested) {
        return new NestedMismatch<I, AndChainMatcher<T>>(item, this) {
            @Override
            public int getExpectedPrecedence() {
                return P_UNARY;
            }
            @Override
            public void describeExpected(Description d) {
                nestedDescribeExpected(nested, d);
            }
            @Override
            public int getMismatchPrecedence() {
                return P_UNARY;
            }
            @Override
            public void describeMismatch(Description d) {
                nestedDescribeMismatch(nested, d);
            }
        };
    }

    @Factory
    @SuppressWarnings("unchecked")
    public static <T> Matcher<T> and(Matcher<? super T>... matchers) {
        return new AndChainMatcher<>(matchers);
    }
    
    @Factory
    public static <T> Matcher<T> and(Collection<? extends Matcher<? super T>> matchers) {
        return new AndChainMatcher<>(matchers);
    }
    
    @Factory
    public static <T> Builder<T> both(Matcher<? super T> m) {
        return new Builder<T>().and(m);
    }
    
    @Factory
    public static <T> Builder<T> both(Matcher<? super T>... m) {
        return new Builder<T>().and(m);
    }
    
    @Factory
    public static <T> Builder<T> all(Matcher<? super T> m) {
        return new Builder<T>().and(m);
    }
    
    @Factory
    public static <T> Builder<T> all(Matcher<? super T>... m) {
        return new Builder<T>().and(m);
    }
    
    @Factory
    public static ChainFactory all() {
        return FACTORY;
    }
    
    public static final ChainFactory FACTORY = new ChainFactoryBase() {
        @Override
        public <T> Matcher<T> create(Collection<? extends Matcher<? super T>> chain) {
            return new AndChainMatcher<>(chain);
        }
    };

    public static class Builder<T> extends ChainBuilderBase<T> {
        public Builder() {
        }
        @Override
        protected ChainFactory factory() {
            return FACTORY;
        }
        public <T2 extends T> Builder<T2> and(Matcher<? super T2> m) {
            return (Builder<T2>) _add( m);
        }
        public <T2 extends T> Builder<T2>  and(Matcher<? super T2>... m) {
            return (Builder<T2>) _add(m);
        }
        public <T2 extends T> Builder<T2>  and(Collection<? extends Matcher<? super T2>> m) {
            return (Builder<T2>) _add(m);
        }
    }
}
