package org.cthul.matchers.chain;

import java.util.Collection;
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

    public static class Builder<T> extends ChainBuilder<T> {
        
        protected final ChainFactory xorFactory;
        private Boolean xorEnabled = null;
        
        public Builder() {
            super(FACTORY);
            xorFactory = XOrChainMatcher.FACTORY;
        }
        
        public Builder(ChainFactory factory) {
            super(factory);
            xorFactory = XOrChainMatcher.FACTORY;
        }
        
        public Builder(ChainFactory xorFactory, ChainFactory factory) {
            super(factory);
            this.xorFactory = xorFactory;
        }

        @Override
        protected ChainFactory factory() {
            if (xorEnabled != null && xorEnabled) {
                return xorFactory;
            }
            return super.factory();
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
            return (Builder<T>) add(m);
        }
        
        protected <T2 extends T> Builder<T> _or(Matcher<? super T2>... m) {
            return (Builder<T>) add(m);
        }
        
        protected <T2 extends T> Builder<T> _or(Collection<? extends Matcher<? super T2>> m) {
            return (Builder<T>) add(m);
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
