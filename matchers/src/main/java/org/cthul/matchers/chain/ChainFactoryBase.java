package org.cthul.matchers.chain;

import java.util.Arrays;
import java.util.Collection;
import org.hamcrest.Matcher;

/**
 */
public abstract class ChainFactoryBase implements ChainFactory {

    @Override
    public <T> Matcher<T> create(Matcher<? super T>... chain) {
        return create(Arrays.asList(chain));
    }

    @Override
    public <T> Matcher<T> of(Collection<? extends Matcher<? super T>> chain) {
        return create(chain);
    }

    @Override
    public <T> Matcher<T> of(Matcher<? super T>... chain) {
        return create(chain);
    }
    
}
