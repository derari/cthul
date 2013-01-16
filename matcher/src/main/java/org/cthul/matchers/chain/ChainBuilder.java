package org.cthul.matchers.chain;

import java.util.*;
import org.cthul.matchers.diagnose.NestedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Build a matcher chain using a {@link ChainFactory}.
 * For convinience, can also be used as a matcher directly.
 * 
 * @param <T> 
 */
public class ChainBuilder<T> extends NestedMatcher<T> {

    protected final ChainFactory factory;
    private final List<Matcher<? super T>> matchers = new ArrayList<>();
    private Matcher<T> matcher = null;

    public ChainBuilder(ChainFactory factory) {
        this.factory = factory;
    }
    
    protected ChainFactory factory() {
        return factory;
    }
    
    protected Matcher<T> matcher() {
        if (matcher == null) {
            matcher = factory().create(matchers);
        }
        return matcher;
    }
    
    protected ChainBuilder<T> add(Matcher<? super T> m) {
        matcher = null;
        matchers.add(m);
        return this;
    }

    protected ChainBuilder<T> add(Matcher<? super T>... m) {
        return add(Arrays.asList(m));
    }

    protected ChainBuilder<T> add(Collection<? extends Matcher<? super T>> m) {
        matcher = null;
        matchers.addAll(m);
        return this;
    }

    @Override
    public boolean matches(Object o) {
        return matcher().matches(o);
    }
    
    @Override
    public boolean matches(Object item, Description mismatch) {
        return quickMatch(matcher, item, mismatch);
    }

    @Override
    public void describeTo(Description description) {
        matcher().describeTo(description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        matcher().describeMismatch(item, description);
    }

    @Override
    public int getPrecedence() {
        return precedenceOf(matcher());
    }
    
    public Matcher<T> build() {
        return matcher();
    }

}
