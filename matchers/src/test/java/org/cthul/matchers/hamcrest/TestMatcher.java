package org.cthul.matchers.hamcrest;

import org.cthul.matchers.diagnose.result.AbstractMatchResult;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.safe.TypesafeQuickResultMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public class TestMatcher extends TypesafeQuickResultMatcher<Boolean> {
    
    public static final String Description =    "a good value";
    public static final String Match =          "was a good value";
    public static final String Expected =       "better values";
    public static final String Mismatch =       "was a bad value";
    public static final TestMatcher Instance = new TestMatcher();

    @Override
    public void describeTo(Description description) {
        description.appendText(Description);
    }

    @Override
    protected <I extends Boolean> MatchResult<I> matchResultSafely(I item) {
        return new AbstractMatchResult<I, Matcher<?>>(item, this, item) {
            @Override
            public void describeMatch(Description d) {
                d.appendText(Match);
            }
            @Override
            public void describeExpected(Description d) {
                d.appendText(Expected);
            }
            @Override
            public void describeMismatch(Description d) {
                d.appendText(Mismatch);
            }
        };
    }
}
