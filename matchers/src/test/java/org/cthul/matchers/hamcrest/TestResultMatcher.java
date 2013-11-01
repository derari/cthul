package org.cthul.matchers.hamcrest;

import org.cthul.matchers.diagnose.result.AbstractMatchResult;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.safe.TypesafeQuickResultMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public class TestResultMatcher extends TypesafeQuickResultMatcher<MatchResult<Boolean>> {
    
    public static final String Description =    "a good result";
    public static final String Match =          "was a good match";
    public static final String Expected =       "better expectations";
    public static final String Mismatch =       "was a bad mismatch";
    public static final TestResultMatcher Instance = new TestResultMatcher();

    public TestResultMatcher() {
        super();
    }
    
    @Override
    public void describeTo(Description description) {
        description.appendText(Description);
    }

    @Override
    protected <I extends MatchResult<Boolean>> MatchResult<I> matchResultSafely(I item) {
        return new AbstractMatchResult<I, Matcher<?>>(item, this, item.matched()) {
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
