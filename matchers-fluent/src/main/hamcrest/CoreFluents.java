package org.cthul.matchers;

import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.builder.FluentMatcherBuilder;
import org.cthul.matchers.fluent.values.MatchValueType;

/**
 *
 */
public class CoreFluents extends CoreFluentsBase {
    
    public static <V> FluentMatcher<V, V> match() {
        return FluentMatcherBuilder.match();
    }
    
    public static <V> FluentMatcher<V, V> match(Class<V> c) {
        return FluentMatcherBuilder.match(c);
    }
    
    public static <V, M> FluentMatcher<V, M> match(MatchValueType<M, V> adapter) {
        return FluentMatcherBuilder.match(adapter);
    }
    
}
