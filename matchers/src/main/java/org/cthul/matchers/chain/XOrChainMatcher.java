package org.cthul.matchers.chain;

import java.util.Collection;
import static org.cthul.matchers.diagnose.PrecedencedMatcher.P_AND;
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
            nestedDescribe(description, m);
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
        int i = 0;
        boolean[] matches = new boolean[matchers.length];
        boolean match = false;
        for (Matcher<?> m: matchers) {
            if (m.matches(item)) {
                matches[i] = true;
                match = !match;
            }
            i++;
        }
        if (match) return true;
        listMatchDescriptions(matches, item, mismatch);
        return false;
    }
    
    private void listMatchDescriptions(boolean[] matches, Object item, Description mismatch) {
        final int len = matchers.length;
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                if (i < len-1) {
                    mismatch.appendText(", ");
                } else {
                    mismatch.appendText(i == 1 ? " " : ", ");
                    mismatch.appendText("and ");
                }
            }
            // append either fail- or match-description
            if (matches[i]) {
                nestedDescribe(mismatch, matchers[i]);
            } else {
                nestedDescribeMismatch(mismatch, matchers[i], item);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void describeMismatch(Object item, Description mismatch) {
        int i = 0;
        for (Matcher<?> m: matchers) {
            if (i > 0) {
                if (i > 1 && i+1 < matchers.length) {
                    mismatch.appendText(", ");
                } else {
                    
                    mismatch.appendText(i == 1 ? " " : ", ");
                    mismatch.appendText("and ");
                }
            }
            i++;
            // append either fail- or match-description
            if (nestedQuickMatch(m, item, mismatch)) {
                nestedDescribe(mismatch, m);
            }
        }
    }

    @Override
    public int getPrecedence() {
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

        public Builder(ChainFactory factory) {
            super(factory, null);
            makeXOR();
        }
    }
}
