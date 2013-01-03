package org.cthul.matchers.chain;

import java.util.Collection;
import org.hamcrest.*;

/**
 * 
 * @author Arian Treffer
 * @param <T> 
 */
public class OrChainMatcher<T> extends MatcherChainBase<T> {
    
    public OrChainMatcher(Collection<? extends Matcher<? super T>> matchers) {
        super(matchers);
    }

    @SuppressWarnings("unchecked")
    public OrChainMatcher(Matcher<? super T>... matchers) {
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
                description.appendText(" or ");
            }
            nestedDescribe(description, m);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item) {
        for (Matcher<?> m: matchers) {
            if (m.matches(item)) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item, Description mismatch) {
        if (matches(item)) {
            return true;
        }
        // we can only add a mismatch description
        // once we are sure none of the matchers matched
        describeMismatch(item, mismatch);
        return false;
    }
    
    /** {@inheritDoc} */
    @Override
    public void describeMismatch(Object item, Description description) {
        // all matchers failed
        boolean first = true;
        for (Matcher<?> m: matchers) {
            if (first) {
                first = false;
            } else {
                description.appendText(" and ");
            }
            nestedDescribeMismatch(description, m, item);
        }
    }

    @Override
    public int getPrecedence() {
        return P_OR;
    }
    
    @Factory
    @SuppressWarnings("unchecked")
    public static <T> Matcher<T> or(Matcher<? super T>... matchers) {
        return new OrChainMatcher<>(matchers);
    }

    @Factory
    public static <T> Matcher<T> or(Collection<? extends Matcher<? super T>> matchers) {
        return new OrChainMatcher<>(matchers);
    }
    
    public static final ChainFactory FACTORY = new ChainFactory() {
        @Override
        public <T> Matcher<T> create(Collection<? extends Matcher<? super T>> chain) {
            return new OrChainMatcher<>(chain);
        }
    };
    
}
