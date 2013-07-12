package org.cthul.matchers;

import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.builder.FluentMatcherBuilder;

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
    
}
