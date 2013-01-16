package org.cthul.matchers.chain;

import java.util.Collection;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * 
 * @author Arian Treffer
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
            nestedDescribe(description, m);
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

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item, Description mismatch) {
        for (Matcher<? super T> m: matchers) {
            // the first matcher that succeeds describes the mismatch
            if (m.matches(item)) {
                m.describeTo(mismatch);
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
        return P_NOR;
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
    
    public static class Builder<T> extends ChainBuilder<T> {
        public Builder() {
            super(FACTORY);
        }
        public Builder(ChainFactory factory) {
            super(factory);
        }
        public Builder<T> nor(Matcher<? super T> m) {
            return (Builder<T>) add(m);
        }
        public Builder<T> nor(Matcher<? super T>... m) {
            return (Builder<T>) add(m);
        }
        public Builder<T> nor(Collection<? extends Matcher<? super T>> m) {
            return (Builder<T>) add(m);
        }
    }
}
