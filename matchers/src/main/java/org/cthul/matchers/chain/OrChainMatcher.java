package org.cthul.matchers.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * 
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
            nestedDescribeTo(m, description);
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

    @Override
    public <I> MatchResult<I> matchResult(I item) {
        List<MatchResult.Mismatch<I>> mismatches = new ArrayList<>(matchers.length);
        for (Matcher<?> m: matchers) {
            MatchResult<I> mr = quickMatchResult(m, item);
            if (mr.isSuccess()) {
                return successResult(item, mr.getMatch());
            } else {
                mismatches.add(mr.getMismatch());
            }
        }
        return failResult(item, mismatches);
    }

    private <I> MatchResult<I> successResult(I item, final MatchResult.Match<I> nested) {
        return new NestedMatch<I, OrChainMatcher<T>>(item, this) {
            @Override
            public int getMatchPrecedence() {
                return P_UNARY;
            }
            @Override
            public void describeMatch(Description d) {
                nestedDescribeMatch(nested, d);
            }
        };
    }

    private <I> MatchResult<I> failResult(I item, final List<MatchResult.Mismatch<I>> nested) {
        return new NestedMismatch<I, OrChainMatcher<T>>(item, this) {
            @Override
            public void describeExpected(Description d) {
                boolean first = true;
                for (MatchResult.Mismatch<I> m: nested) {
                    if (first) {
                        first = false;
                    } else {
                        d.appendText(" or ");
                    }
                    nestedDescribeExpected(m, d);
                }
            }
            @Override
            public void describeMismatch(Description d) {
                boolean first = true;
                for (MatchResult.Mismatch<I> m: nested) {
                    if (first) {
                        first = false;
                    } else {
                        d.appendText(" and ");
                    }
                    nestedDescribeMismatch(m, d);
                }
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
    public static <T> Matcher<T> or(Matcher<? super T>... matchers) {
        return new OrChainMatcher<>(matchers);
    }

    @Factory
    public static <T> Matcher<T> or(Collection<? extends Matcher<? super T>> matchers) {
        return new OrChainMatcher<>(matchers);
    }
    
    @Factory
    public static <T> Builder<T> either(Matcher<? super T> m) {
        return new Builder<T>()._or(m);
    }
    
    @Factory
    public static <T> Builder<T> either(Matcher<? super T>... m) {
        return new Builder<T>()._or(m);
    }
    
    public static final ChainFactory FACTORY = new ChainFactory() {
        @Override
        public <T> Matcher<T> create(Collection<? extends Matcher<? super T>> chain) {
            return new OrChainMatcher<>(chain);
        }
    };

    public static class Builder<T> extends ChainBuilderBase<T> {
        
        private Boolean xorEnabled = null;
        
        public Builder() {
        }
        
        @Override
        protected ChainFactory factory() {
            if (xorEnabled != null && xorEnabled) {
                return xorFactory();
            }
            return orFactory();
        }
        
        protected ChainFactory orFactory() {
            return FACTORY;
        }
        
        protected ChainFactory xorFactory() {
            return XOrChainMatcher.FACTORY;
        }
        
        protected void makeOR() {
            if (xorEnabled == Boolean.TRUE) {
                throw new IllegalStateException(
                        "Cannot switch between XOR and OR");
            }
            xorEnabled = Boolean.FALSE;
        }
        
        protected void makeXOR() {
            if (xorEnabled == Boolean.FALSE) {
                throw new IllegalStateException(
                        "Cannot switch between OR and XOR");
            }
            xorEnabled = Boolean.TRUE;
        }
        
        protected <T2 extends T> Builder<T> _or(Matcher<? super T2> m) {
            return (Builder<T>) _add(m);
        }
        
        protected <T2 extends T> Builder<T> _or(Matcher<? super T2>... m) {
            return (Builder<T>) _add(m);
        }
        
        protected <T2 extends T> Builder<T> _or(Collection<? extends Matcher<? super T2>> m) {
            return (Builder<T>) _add(m);
        }
        
        public <T2 extends T> Builder<T> or(Matcher<? super T2> m) {
            makeOR();
            return _or(m);
        }
        
        public <T2 extends T> Builder<T> or(Matcher<? super T>... m) {
            makeOR();
            return _or(m);
        }
        
        public <T2 extends T> Builder<T> or(Collection<? extends Matcher<? super T2>> m) {
            makeOR();
            return _or(m);
        }
        
        public <T2 extends T> Builder<T> xor(Matcher<? super T2> m) {
            makeXOR();
            return _or(m);
        }
        
        public <T2 extends T> Builder<T> xor(Matcher<? super T2>... m) {
            makeXOR();
            return _or(m);
        }
        
        public <T2 extends T> Builder<T> xor(Collection<? extends Matcher<? super T2>> m) {
            makeXOR();
            return _or(m);
        }
        
    }
}
