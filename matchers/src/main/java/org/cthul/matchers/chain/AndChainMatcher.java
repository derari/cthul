package org.cthul.matchers.chain;

import java.util.Collection;
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
            nestedDescribe(description, m);
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

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item, Description mismatch) {
        for (Matcher<? super T> m: matchers) {
            // the first matcher that fails describes the mismatch
            if (!nestedQuickMatch(m, item, mismatch)) {
                return false;
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void describeMismatch(Object item, Description description) {
        matches(item, description);
    }

    @Override
    public int getPrecedence() {
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
    
    public static class Builder<T> extends ChainBuilder<T> {
        public Builder() {
            super(FACTORY);
        }
        public Builder(ChainFactory factory) {
            super(factory);
        }
        public <T2 extends T> Builder<T2> and(Matcher<? super T2> m) {
            return (Builder<T2>) add( m);
        }
        public <T2 extends T> Builder<T2>  and(Matcher<? super T2>... m) {
            return (Builder<T2>) add(m);
        }
        public <T2 extends T> Builder<T2>  and(Collection<? extends Matcher<? super T2>> m) {
            return (Builder<T2>) add(m);
        }
    }
}
