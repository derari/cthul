package org.cthul.matchers.chain;

import org.cthul.matchers.diagnose.NestedMatcher;
import java.util.Collection;
import org.cthul.matchers.diagnose.QuickDiagnosingMatcherBase;
import org.hamcrest.*;

/**
 * Combines multiple matchers.
 * 
 * @author Arian Treffer
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
