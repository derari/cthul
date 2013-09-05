package org.cthul.matchers.chain;

import java.util.Collection;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;

/**
 * Conjunction of multiple matchers.
 * 
 * @param <T> 
 */
public class SomeOfChainMatcher<T> extends MatcherChainBase<T> {
    
    private final Matcher<? super Integer> countMatcher;

    public SomeOfChainMatcher(Matcher<? super Integer> countMatcher, Matcher<? super T>... matchers) {
        super(matchers);
        this.countMatcher = countMatcher;
    }

    public SomeOfChainMatcher(Matcher<? super Integer> countMatcher, Collection<? extends Matcher<? super T>> matchers) {
        super(matchers);
        this.countMatcher = countMatcher;
    }

    /** {@inheritDoc} */
    @Override
    public void describeTo(Description description) {
        countMatcher.describeTo(description);
        description.appendText(" of: ");
        int i = 0;
        for (Matcher<?> m: matchers) {
            if (i > 0) {
                if (i+1 < matchers.length) {
                    description.appendText(", ");
                } else {
                    description.appendText(i == 1 ? " " : ", ");
                    description.appendText("and ");
                }
            }
            i++;
            nestedDescribe(description, m);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item) {
        int count = 0;
        for (Matcher<?> m: matchers) {
            if (m.matches(item)) {
                count++;
            }
        }
        return countMatcher.matches(count);
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(Object item, Description mismatch) {
        boolean[] matches = new boolean[matchers.length];
        int count = 0;
        int i = 0;
        for (Matcher<?> m: matchers) {
            if (m.matches(item)) {
                matches[i] = true;
                count++;
            }
            i++;
        }
        if (nestedQuickMatch(countMatcher, count, mismatch, "matched $1: ")) {
            return true;
        } else {
            listMatchDescriptions(matches, item, mismatch);
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void describeMismatch(Object item, Description mismatch) {
        matches(item, mismatch);
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

    @Override
    public int getPrecedence() {
        return P_COMPLEX;
    }
    
    @Factory
    public static SomeOfChainFactory matches(int count) {
        return factory(count);
    }
    
    @Factory
    public static SomeOfChainFactory matches(Matcher<? super Integer> countMatcher) {
        return factory(countMatcher);
    }
    
    @Factory
    public static <T> Builder<T> matches(int count, Matcher<? super T>... matchers) {
        return factory(count).of(matchers);
    }
    
    @Factory
    public static <T> Builder<T> matches(Matcher<? super Integer> countMatcher, Matcher<? super T>... matchers) {
        return factory(countMatcher).of(matchers);
    }
    
    @Factory
    public <T> Builder<T> oneOf(Matcher<? super T>... matchers) {
        return matches(1).of(matchers);
    }

    @Factory
    public <T> Builder<T> oneOf(Collection<? extends Matcher<? super T>> matcher) {
        return matches(1).of(matcher);
    }

    public static SomeOfChainFactory factory(int count) {
        return factory(Is.is(count));
    }
    
    public static SomeOfChainFactory factory(Matcher<? super Integer> countMatcher) {
        return new SomeOfChainFactory(countMatcher);
    }
    
    public static class SomeOfChainFactory implements ChainFactory {

        private final Matcher<? super Integer> countMatcher;

        public SomeOfChainFactory(Matcher<? super Integer> countMatcher) {
            this.countMatcher = countMatcher;
        }
        
        @Override
        public <T> Matcher<T> create(Collection<? extends Matcher<? super T>> chain) {
            return new SomeOfChainMatcher<>(countMatcher, chain);
        }
        
        public <T> Builder<T> of(Matcher<? super T> matcher) {
            return new Builder<>(this).and(matcher);
        }
        
        public <T> Builder<T> of(Matcher<? super T>... matchers) {
            return new Builder<>(this).and(matchers);
        }
        
        public <T> Builder<T> of(Collection<? extends Matcher<? super T>> matcher) {
            return new Builder<>(this).and(matcher);
        }
    };
    
    public static class Builder<T> extends ChainBuilder<T> {
        public Builder(ChainFactory factory) {
            super(factory);
        }
        public <T2 extends T> Builder<T2> and(Matcher<? super T2> m) {
            return (Builder<T2>) add( m);
        }
        public <T2 extends T> Builder<T2> and(Matcher<? super T2>... m) {
            return (Builder<T2>) add(m);
        }
        public <T2 extends T> Builder<T2> and(Collection<? extends Matcher<? super T2>> m) {
            return (Builder<T2>) add(m);
        }
    }
}
