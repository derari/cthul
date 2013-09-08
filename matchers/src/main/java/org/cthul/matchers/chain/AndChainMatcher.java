package org.cthul.matchers.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResult.Match;
import org.cthul.matchers.diagnose.result.MatchResultSuccess;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

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

    /** {@inheritDoc} */
    @Override
    public void describeTo(Description description) {
        boolean first = true;
        for (Matcher<?> m: matchers) {
            if (first) {
                first = false;
            } else {
                description.appendText(" and ");
            }
            nestedDescribeTo(m, description);
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
    
    private <I> MatchResult.Mismatch<I> failedMatch(I item) {
        for (Matcher<?> m: matchers) {
            MatchResult<I> mr = quickMatchResult(m, item);
            if (!mr.matched()) {
                return mr.getMismatch();
            }
        }
        return null;
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
            public void describeMatch(Description d) {
                boolean first = true;
                for (Match<?> m: results) {
                    if (first) {
                        first = false;
                    } else {
                        d.appendText(" and ");
                    }
                    nestedDescribeMatch(m, d);
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
            public void describeMismatch(Description d) {
                nestedDescribeMismatch(nested, d);
            }
        };
    }

    @Override
    public int getDescriptionPrecedence() {
        return P_AND;
    }

    @Override
    public int getMismatchPrecedence() {
        // prints only failed matcher
        return P_UNARY;
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
    
    public static final ChainFactory FACTORY = new ChainFactory() {
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
