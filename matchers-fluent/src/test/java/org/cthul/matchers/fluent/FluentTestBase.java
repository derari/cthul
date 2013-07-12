package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.builder.FailureHandler;
import org.cthul.matchers.fluent.values.MatchValues;
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
        assertThat("success", success);
    }
    
    protected void assertFail() {
        assertThat("fail", !success);
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
    
    protected FailureHandler TEST_HANDLER = new FailureHandler() {
        @Override
        public <T> void mismatch(String reason, MatchValues<T> item, Matcher<? super T> matcher) {
            if (!success) {
                // don't override first mismatch
                return;
            }
            success = false;
            StringDescription desc = new StringDescription();
            item.describeMismatch(matcher, desc);
            mismatch = desc.toString();
        }
    };
}