package org.cthul.matchers.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.result.MatchResult;
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

    @Override
    public int getDescriptionPrecedence() {
        return P_COMPLEX;
    }
    
    /** {@inheritDoc} */
    @Override
    public void describeTo(Description description) {
        nestedDescribeTo(countMatcher, description);
        description.appendText(" of: ");
        describeMatchers(description);
    }
    
    private void describeMatchers(Description description) {
        Nested.joinDescriptions(getDescriptionPrecedence(), matchersList(), description, ", ", ", and ", " and ");
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

    @Override
    public <I> MatchResult<I> matchResult(I item) {
        final List<MatchResult<I>> results = new ArrayList<>(matchers.length);
        int count = 0;
        for (Matcher<?> m: matchers) {
            if (m.matches(item)) {
                count++;
            }
        }
        final MatchResult<Integer> countResult = quickMatchResult(countMatcher, count);
        return result(item, countResult, results);
    }

    private <I> MatchResult<I> result(I item, final MatchResult<Integer> countResult, final List<MatchResult<I>> results) {
        return new NestedResult<I, SomeOfChainMatcher<T>>(item, this, countResult.matched()) {
            @Override
            public void describeTo(Description description) {
                description.appendText("matched ");
                nestedDescribeTo(getDescriptionPrecedence(), countResult, description);
                description.appendText(": ");
                Nested.listDescriptions(getDescriptionPrecedence(), results, description);
            }
            @Override
            public void describeMatch(Description description) {
                describeTo(description);
            }
            @Override
            public void describeExpected(Description description) {
                nestedDescribeTo(getExpectedPrecedence(), countResult.getMismatch().getExpectedDescription(), description);
                description.appendText(" of: ");
                describeMatchers(description);
            }
            @Override
            public void describeMismatch(Description description) {
                describeTo(description);
            }
        };
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

    public static class SomeOfChainFactory extends ChainFactoryBase {

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
        
        @Override
        public <T> Builder<T> of(Matcher<? super T>... matchers) {
            return new Builder<>(this).and(matchers);
        }
        
        @Override
        public <T> Builder<T> of(Collection<? extends Matcher<? super T>> matcher) {
            return new Builder<>(this).and(matcher);
        }
    };
    
    public static class Builder<T> extends ChainBuilderBase<T> {
        private final ChainFactory factory;
        public Builder(ChainFactory factory) {
            this.factory = factory;
        }
        @Override
        protected ChainFactory factory() {
            return factory;
        }
        public <T2 extends T> Builder<T2> and(Matcher<? super T2> m) {
            return (Builder<T2>) _add( m);
        }
        public <T2 extends T> Builder<T2> and(Matcher<? super T2>... m) {
            return (Builder<T2>) _add(m);
        }
        public <T2 extends T> Builder<T2> and(Collection<? extends Matcher<? super T2>> m) {
            return (Builder<T2>) _add(m);
        }
    }
}
