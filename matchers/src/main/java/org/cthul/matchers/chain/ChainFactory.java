package org.cthul.matchers.chain;

import java.util.Collection;
import org.hamcrest.Matcher;

/**
 * Creates a matcher chain.
 */
public interface ChainFactory {
    
    /**
     * Creates a matcher chain.
     * 
     * @param <T>
     * @param chain elements of the chain
     * @return matcher chain
     */
    public <T> Matcher<T> create(Collection<? extends Matcher<? super T>> chain);
    
}
