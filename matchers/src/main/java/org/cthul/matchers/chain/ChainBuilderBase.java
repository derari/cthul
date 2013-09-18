package org.cthul.matchers.chain;

import java.util.*;
import org.cthul.matchers.diagnose.result.MatcherProxy;
import org.hamcrest.Matcher;

/**
 * Build a matcher chain using a {@link ChainFactory}.
 * For convenience, can also be used as a matcher directly.
 * 
 * @param <T> 
 */
public abstract class ChainBuilderBase<T> extends MatcherProxy<T> {

    private final List<Matcher<? super T>> matchers = new ArrayList<>();
    private Matcher<T> matcher = null;

    public ChainBuilderBase() {
    }
    
    protected abstract ChainFactory factory();
    
    @Override
    protected Matcher<T> matcher() {
        if (matcher == null) {
            matcher = factory().create(matchers);
        }
        return matcher;
    }
    
    protected <T2 extends T> ChainBuilderBase<T2> _add(Matcher<? super T2> m) {
        matcher = null;
        matchers.add((Matcher) m);
        return (ChainBuilderBase) this;
    }

    protected <T2 extends T> ChainBuilderBase<T2> _add(Matcher<? super T2>... m) {
        return _add(Arrays.asList(m));
    }

    protected <T2 extends T> ChainBuilderBase<T2> _add(Collection<? extends Matcher<? super T2>> m) {
        matcher = null;
        matchers.addAll((Collection) m);
        return (ChainBuilderBase) this;
    }

    public Matcher<T> build() {
        return matcher();
    }
}
