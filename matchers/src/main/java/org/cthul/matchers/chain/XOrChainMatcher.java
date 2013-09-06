package org.cthul.matchers.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.cthul.matchers.diagnose.nested.PrecedencedMatcher.P_AND;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * 
 * @param <T> 
 */
public class XOrChainMatcher<T> extends MatcherChainBase<T> {
    
    public XOrChainMatcher(Collection<? extends Matcher<? super T>> matchers) {
        super(matchers);
    }

    @SuppressWarnings("unchecked")
    public XOrChainMatcher(Matcher<? super T>... matchers) {
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
                description.appendText(" xor ");
            }
            nestedDescribeTo(m, description);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item) {
        boolean match = false;
        for (Matcher<?> m: matchers) {
            match ^= m.matches(item);
        }
        return match;
    }

    @Override
    public <I> MatchResult<I> matchResult(I item) {
        List<MatchResult<I>> results = new ArrayList<>(matchers.length);
        boolean match = false;
        for (Matcher<?> m: matchers) {
            match ^= m.matches(item);
        }
        return result(match, item, results);
    }

    private <I> MatchResult<I> result(boolean match, I item, final List<MatchResult<I>> results) {
        return new NestedResult<I, XOrChainMatcher<T>>(item, this, match) {
            @Override
            public int getDescriptionPrecedence() {
                return P_AND;
            }
            @Override
            public int getMatchPrecedence() {
                return P_AND;
            }
            @Override
            public int getMismatchPrecedence() {
                return P_AND;
            }
            @Override
            public void describeTo(Description d) {
                final int p = getMatchPrecedence();
                boolean first = true;
                for (MatchResult<I> mr: results) {
                    if (first) {
                        first = false;
                    } else {
                        d.appendText(" and ");
                    }
                    nestedDescribeTo(p, mr, d);
                }
            }
            @Override
            public void describeMatch(Description d) {
                describeTo(d);
            }
            @Override
            public void describeMismatch(Description d) {
                describeTo(d);
            }
        };
    }

    @Override
    public int getDescriptionPrecedence() {
        return P_OR;
    }

    @Override
    public int getMismatchPrecedence() {
        // prints all matchers with 'and'
        return P_AND;
    }

    @Factory
    @SuppressWarnings("unchecked")
    public static <T> Matcher<T> xor(Matcher<? super T>... matchers) {
        return new XOrChainMatcher<>(matchers);
    }
    
    @Factory
    public static <T> Matcher<T> xor(Collection<? extends Matcher<? super T>> matchers) {
        return new XOrChainMatcher<>(matchers);
    }
    
    // no @Factory, use OrChainMatcher.either instead
    public static <T> OrChainMatcher.Builder<T> either(Matcher<? super T> m) {
        return new Builder<T>().xor(m);
    }
    
    // no @Factory, use OrChainMatcher.either instead
    public static <T> OrChainMatcher.Builder<T> either(Matcher<? super T>... m) {
        return new Builder<T>().xor(m);
    }
    
    public static final ChainFactory FACTORY = new ChainFactory() {
        @Override
        public <T> Matcher<T> create(Collection<? extends Matcher<? super T>> chain) {
            return new XOrChainMatcher<>(chain);
        }
    };
    
    public static class Builder<T> extends OrChainMatcher.Builder<T> {
        public Builder() {
            makeXOR();
        }
    }
}
