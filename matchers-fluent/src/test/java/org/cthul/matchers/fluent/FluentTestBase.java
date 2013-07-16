package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.builder.*;
import org.cthul.matchers.fluent.values.MatchValue;
import org.cthul.matchers.fluent.values.MatchValueAdapter;
import org.hamcrest.Matcher;
import org.junit.Before;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.hamcrest.StringDescription;

/**
 *
 */
public class FluentTestBase {

    private boolean success = true;
    private String mismatch = null;
    
    @Before
    public void reset() {
        success = true;
        mismatch = null;
    }
    
    protected void assertSuccess() {
        assertThat("expected success", success);
    }
    
    protected void assertFail() {
        assertThat("expected fail", !success);
    }
    
    protected void assertMismatch(String... msg) {
        assertFail();
        for (String m: msg) {
            assertThat(mismatch, containsString(m));
        }
    }
    
    protected <T> void test_assertThat(T value, Matcher<? super T> matcher) {
        if (success && !matcher.matches(value)) {
            success = false;
            StringDescription desc = new StringDescription();
            matcher.describeMismatch(value, desc);
            mismatch = desc.toString();
        }
    }
    
    protected <T> FluentAssert<T> test_assertThat(MatchValue<T> object) {
        return new FluentAssertBuilder<>(TEST_HANDLER, object);
    }
    
    protected <T> FluentAssert<T> test_assertThat(T object) {
        return new FluentAssertBuilder<>(TEST_HANDLER, object);
    }
    
    protected FluentMatcher<Integer, Iterable<? extends Integer>> test_matcher(MatchValueAdapter<Iterable<? extends Integer>, Integer> adapter) {
        return FluentMatcherBuilder.match(adapter);
    }
    
    protected FailureHandler TEST_HANDLER = new FailureHandlerBase() {
        @Override
        public <T> void mismatch(String reason, MatchValue<T> actual, MatchValue.ElementMatcher<T> matcher) {
            if (!success) {
                return;// don't override first mismatch
            }
            success = false;
            mismatch = getMismatchString(reason, actual);
        }
    };
}