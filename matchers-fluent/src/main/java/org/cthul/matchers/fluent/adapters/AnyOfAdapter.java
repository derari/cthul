package org.cthul.matchers.fluent.adapters;

import java.util.Arrays;
import java.util.Iterator;
import org.cthul.matchers.fluent.values.AbstractMatchValueAdapter;
import org.cthul.matchers.fluent.values.AbstractMatchValue;
import org.cthul.matchers.fluent.values.MatchValue;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 *
 */
public class AnyOfAdapter<Item> extends 
                AbstractMatchValueAdapter<Iterable<? extends Item>, Item> {

    private static final AnyOfAdapter INSTANCE = new AnyOfAdapter<>();

    @Factory
    public static <I> AnyOfAdapter<I> anyOf() {
        return INSTANCE;
    }
    
    @Factory
    public static <I> MatchValue<I> anyOf(Iterable<? extends I> iterable) {
        return (MatchValue) INSTANCE.adapt(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> anyOf(I... array) {
        return anyOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> anyOf(MatchValue<? extends Iterable<? extends I>> mo) {
        return INSTANCE.wrap(mo);
    }
    
    @Factory
    public static <I> AnyOfAdapter<I> any(Class<I> c) {
        return INSTANCE;
    }
    
    @Factory
    public static <I> MatchValue<I> any(Iterable<? extends I> iterable) {
        return INSTANCE.adapt(iterable);
    }
    
    @Factory
    public static <I> MatchValue<I> any(I... array) {
        return anyOf(Arrays.asList(array));
    }
    
    @Factory
    public static <I> MatchValue<I> any(MatchValue<? extends Iterable<? extends I>> mo) {
        return INSTANCE.wrap(mo);
    }
    
    @Factory
    public static AnyOfAdapter<Integer> anyInt() {
        return INSTANCE;
    }

    @Override
    public MatchValue<Item> adapt(Iterable<? extends Item> v) {
        return new AnyOfValues<>(v);
    }

    @Override
    public void describeTo(Matcher<?> matcher, Description description) {
        description.appendText("any ");
        description.appendDescriptionOf(matcher);
    }

    protected static class AnyOfValues<I> extends AbstractMatchValue<I> {
        
        private final Iterable<? extends I> iterable;
        private boolean active = true;
        private Iterator<? extends I> iterator = null;
        private I current = null;
        private Mismatch<I> mismatches = null;
        private Mismatch<I> lastMismatch = null;
        
        public AnyOfValues(Iterable<? extends I> iterable) {
            this.iterable = iterable;
        }
        
        private boolean hasCurrent() {
            if (iterator == null) {
                iterator = iterable.iterator();
                return next();
            }
            return active;
        }
        
        private boolean next() {
            if (!iterator.hasNext()) {
                active = false;
                return false;
            }
            current = iterator.next();
            return true;
        }

        @Override
        public boolean matches(Matcher<? super I> matcher) {
            while (hasCurrent()) {
                if (matcher.matches(current)) {
                    return true;
                }
                Mismatch<I> m = new Mismatch<>(current, matcher);
                if (lastMismatch == null) {
                    mismatches = lastMismatch = m;
                } else {
                    lastMismatch.next = m;
                    lastMismatch = m;
                }
                next();
            }
            return false;
        }

        @Override
        public boolean matches(Matcher<? super I> matcher, Description mismatch) {
            if (matches(matcher)) {
                return true;
            }
            describeMismatch(matcher, mismatch);
            return false;
        }

        @Override
        public void describeExpected(Matcher<? super I> matcher, Description description) {
            description.appendText("any ");
            Mismatch<I> m = mismatches;
            Matcher<?> last = null;
            while (m != null) {
                Matcher<?> expected = m.matcher;
                if (expected != last) {
                    if (last != null) {
                        description.appendText(" and ");
                    }
                    description.appendDescriptionOf(expected);
                    last = expected;
                }
                m = m.next;
            }
        }

        @Override
        public void describeMismatch(Matcher<? super I> matcher, Description description) {
            Mismatch<I> m = mismatches;
            while (m != null) {
                if (m != mismatches) {
                    description.appendText(", ");
                }
                m.matcher.describeMismatch(m.item, description);
                m = m.next;
            }
        }

    }
    
    private static class Mismatch<I> {
        
        public final I item;
        public final Matcher<? super I> matcher;
        public Mismatch<I> next = null;

        public Mismatch(I item, Matcher<? super I> matcher) {
            this.item = item;
            this.matcher = matcher;
        }
        
    }
}
