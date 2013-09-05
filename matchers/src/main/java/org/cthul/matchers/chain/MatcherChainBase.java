package org.cthul.matchers.chain;

import java.util.Collection;
import org.cthul.matchers.diagnose.NestedMatcher;
import org.hamcrest.Matcher;

/**
 * Combines multiple matchers.
 * 
 * @param <T> 
 * @see Matcher
 */
public abstract class MatcherChainBase<T> extends NestedMatcher<T> {

    protected final Matcher<? super T>[] matchers;

    @SuppressWarnings("unchecked")
    public MatcherChainBase(Matcher<? super T>... matchers) {
        this.matchers = matchers.clone();
    }
    
    @SuppressWarnings("unchecked")
    public MatcherChainBase(Collection<? extends Matcher<? super T>> matchers) {
        this.matchers = matchers.toArray(new Matcher[matchers.size()]);
    }
        
}
