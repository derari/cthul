package org.cthul.matchers.chain;

import java.util.Collection;
import static org.cthul.matchers.diagnose.nested.PrecedencedMatcher.P_UNARY;
import org.cthul.matchers.diagnose.result.MatchResult;
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

    /** {@inheritDoc} */
    @Override
    public void describeTo(Description description) {
        boolean first = true;
        for (Matcher<?> m: matchers) {
            if (first) {
                first = false;
                if (matchers.length == 1) {
                    description.appendText("not ");
                } else {
                    description.appendText("neither ");
                }
            } else {
                description.appendText(" nor ");
            }
            nestedDescribeTo(m, description);
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
    
    private <I> MatchResult.Match<I> positiveMatch(I item) {
        for (Matcher<?> m: matchers) {
            MatchResult<I> mr = quickMatchResult(m, item);
            if (mr.isSuccess()) {
                return mr.getMatch();
            }
        }
        return null;
    }

    @Override
    public <I> MatchResult<I> matchResult(I item) {
        final MatchResult.Match<I> nested = positiveMatch(item);
        if (nested == null) {
            return new MatchResultSuccess<>(item, this);
        }
        return new NestedMismatch<I, NOrChainMatcher<T>>(item, this) {
            @Override
            public int getExpectedPrecedence() {
                return P_UNARY;
            }
            @Override
            public void describeExpected(Description d) {
                d.appendText("not ");
                nestedDescribeMatch(getExpectedPrecedence(), nested, d);
            }
            @Override
            public void describeMismatch(Description d) {
                nestedDescribeMatch(getMismatchPrecedence(), nested, d);
            }
        };
    }
    

    @Override
    public int getDescriptionPrecedence() {
        return P_OR;
    }

    @Override
    public int getMismatchPrecedence() {
        // prints only first match
        return P_UNARY;
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
    
    public static final ChainFactory FACTORY = new ChainFactory() {
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
