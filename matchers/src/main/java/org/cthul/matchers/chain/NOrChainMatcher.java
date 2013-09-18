package org.cthul.matchers.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResultMismatch;
import org.cthul.matchers.diagnose.result.MatchResultSuccess;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * 
 * @param <T> 
 */
public class NOrChainMatcher<T> extends MatcherChainBase<T> {

    public NOrChainMatcher(Collection<? extends Matcher<? super T>> matchers) {
        super(matchers);
    }

    @SuppressWarnings("unchecked")
    public NOrChainMatcher(Matcher<? super T>... matchers) {
        super(matchers);
    }

    @Override
    public int getDescriptionPrecedence() {
        return P_OR;
    }
    
    /** {@inheritDoc} */
    @Override
    public void describeTo(Description description) {
        switch (matchers.length) {
            case 0:
                description.appendText("<anything>");
                return;
            case 1:
                description.appendText("not ");
                nestedDescribeTo(matchers[0], description);
                return;
            default:
                description.appendText("neither ");
                Nested.joinDescriptions(getDescriptionPrecedence(), matchersList(), description, " nor ");
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item) {
        for (Matcher<?> m: matchers) {
            if (m.matches(item)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public <I> MatchResult<I> matchResult(I item) {
        List<MatchResult.Mismatch<I>> results = new ArrayList<>(matchers.length);
        for (Matcher<?> m: matchers) {
            MatchResult<I> mr = quickMatchResult(m, item);
            if (mr.matched()) {
                return failResult(item, mr.getMatch());
            }
            results.add(mr.getMismatch());
        }
        return successResult(item, results);
    }
    
    private <I> MatchResult<I> failResult(I item, final MatchResult.Match<I> match) {
        return new MatchResultMismatch<I, Matcher<?>>(item, this) {
            @Override
            public int getMismatchPrecedence() {
                return P_UNARY;
            }
            @Override
            public void describeMismatch(Description description) {
                match.describeMatch(description);
            }
        };
    }

    private <I> MatchResult<I> successResult(I item, final List<MatchResult.Mismatch<I>> results) {
        return new MatchResultSuccess<I, Matcher<?>>(item, this) {
            @Override
            public int getMatchPrecedence() {
                return Nested.pAtomicUnaryOr(P_AND, results.size());
            }
            @Override
            public void describeMatch(Description description) {
                if (results.isEmpty()) {
                    describeMatcher(description);
                } else {
                    Nested.joinMismatchDescriptions(getMatchPrecedence(), results, description, " and ");
                }
            }
        };
    }
    
    @Factory
    @SuppressWarnings("unchecked")
    public static <T> Matcher<T> none(Matcher<? super T>... matchers) {
        return new NOrChainMatcher<>(matchers);
    }
    
    @Factory
    public static <T> Matcher<T> none(Collection<? extends Matcher<? super T>> matchers) {
        return new NOrChainMatcher<>(matchers);
    }
    
    @Factory
    @SuppressWarnings("unchecked")
    public static <T> Matcher<T> nor(Matcher<? super T>... matchers) {
        return new NOrChainMatcher<>(matchers);
    }
    
    @Factory
    public static <T> Matcher<T> nor(Collection<? extends Matcher<? super T>> matchers) {
        return new NOrChainMatcher<>(matchers);
    }
    
    @Factory
    public static <T> Builder<T> neither(Matcher<? super T> m) {
        return new Builder<T>().nor(m);
    }
    
    @Factory
    public static <T> Builder<T> neither(Matcher<? super T>... m) {
        return new Builder<T>().nor(m);
    }
    
    @Factory
    public static ChainFactory none() {
        return FACTORY;
    }
    
    public static final ChainFactory FACTORY = new ChainFactoryBase() {
        @Override
        public <T> Matcher<T> create(Collection<? extends Matcher<? super T>> chain) {
            return new NOrChainMatcher<>(chain);
        }
    };

    public static class Builder<T> extends ChainBuilderBase<T> {
        public Builder() {
        }
        @Override
        protected ChainFactory factory() {
            return FACTORY;
        }
        public <T2 extends T> Builder<T> nor(Matcher<? super T2> m) {
            return (Builder<T>) _add(m);
        }
        public <T2 extends T> Builder<T> nor(Matcher<? super T2>... m) {
            return (Builder<T>) _add(m);
        }
        public <T2 extends T> Builder<T> nor(Collection<? extends Matcher<? super T2>> m) {
            return (Builder<T>) _add(m);
        }
    }
}
