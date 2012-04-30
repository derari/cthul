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
public class XOrChainMatcher<T> extends MatcherChainBase<T> {

    @Factory
    @SuppressWarnings("unchecked")
    public static <T> Matcher<T> xor(Matcher<? super T>... matchers) {
        return new XOrChainMatcher<>(matchers);
    }
    
    @Factory
    public static <T> Matcher<T> xor(Collection<? extends Matcher<? super T>> matchers) {
        return new XOrChainMatcher<>(matchers);
    }
    
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
            m.describeTo(description);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item) {
        boolean match = false;
        for (Matcher<?> m: matchers) {
            if (m.matches(item)) {
                match = !match;
            }
        }
        return match;
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item, Description mismatch) {
        if (matches(item)) {
            return true;
        }
        describeMismatch(item, mismatch);
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void describeMismatch(Object item, Description mismatch) {
        boolean first = true;
        for (Matcher<? super T> m: matchers) {
            if (first) {
                first = false;
            } else {
                mismatch.appendText(" and ");
            }
            if (matches(m, item, mismatch)) {
                m.describeTo(mismatch);
            }
        }
    }
    
    public static final ChainFactory FACTORY = new ChainFactory() {
        @Override
        public <T> Matcher<T> create(Collection<? extends Matcher<? super T>> chain) {
            return new XOrChainMatcher<>(chain);
        }
    };
}
