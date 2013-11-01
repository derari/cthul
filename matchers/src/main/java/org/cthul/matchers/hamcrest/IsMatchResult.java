package org.cthul.matchers.hamcrest;

import org.cthul.matchers.diagnose.result.AbstractMatchResult;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.safe.TypesafeNestedResultMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 *
 */
public class IsMatchResult<T> extends TypesafeNestedResultMatcher<MatchResult<T>> {

    @Factory
    public static <T> IsMatchResult<T> match() {
        return new IsMatchResult<>();
    }

    @Factory
    public static <T> IsMatchResult<T> mismatch() {
        return new IsMatchResult<>(false);
    }
    
    @Factory
    public static <T> IsMatchResult<T> match(Matcher<? super MatchResult<T>> resultMatcher) {
        return new IsMatchResult<>(resultMatcher);
    }

    @Factory
    public static <T> IsMatchResult<T> mismatch(Matcher<? super MatchResult<T>> resultMatcher) {
        return new IsMatchResult<>(false, resultMatcher);
    }
    
    @Factory
    public static <T> IsMatchResult<T> matchWithMessage(Matcher<? super String> resultMatcher) {
        return match(HasDescription.message(resultMatcher));
    }

    @Factory
    public static <T> IsMatchResult<T> mismatchWithMessage(Matcher<? super String> resultMatcher) {
        return mismatch(HasDescription.message(resultMatcher));
    }
    
    @Factory
    public static <T> IsMatchResult<T> matchWithMessage(String message) {
        return match(HasDescription.message(message));
    }

    @Factory
    public static <T> IsMatchResult<T> mismatchWithMessage(String message) {
        return mismatch(HasDescription.message(message));
    }
    
    @Factory
    public static <T> IsMatchResult<T> match(String message) {
        return match(HasDescription.message(message));
    }

    @Factory
    public static <T> IsMatchResult<T> mismatch(String message) {
        return mismatch(HasDescription.message(message));
    }
    
    private final boolean match;
    private final Matcher<? super MatchResult<T>> resultMatcher;

    public IsMatchResult() {
        super(MatchResult.class);
        this.match = true;
        this.resultMatcher = null;
    }

    public IsMatchResult(Matcher<? super MatchResult<T>> resultMatcher) {
        super(MatchResult.class);
        this.match = true;
        this.resultMatcher = resultMatcher;
    }

    public IsMatchResult(boolean match) {
        super(MatchResult.class);
        this.match = match;
        this.resultMatcher = null;
    }

    public IsMatchResult(boolean match, Matcher<? super MatchResult<T>> resultMatcher) {
        super(MatchResult.class);
        this.match = match;
        this.resultMatcher = resultMatcher;
    }

    @Override
    public void describeTo(Description description) {
        if (resultMatcher == null) {
            description
                    .appendText(match ? "a match" : " a mismatch");
        } else {
            description
                    .appendText(match ? "match " : "mismatch ");
            nestedDescribeTo(resultMatcher, description);
        }
    }

    @Override
    public int getDescriptionPrecedence() {
        return resultMatcher == null ? P_ATOMIC : P_UNARY;
    }
    
    @Override
    protected boolean matchesSafely(MatchResult<T> item) {
        if (item.matched() != match) {
            return false;
        }
        if (resultMatcher == null) {
            return true;
        }
        return resultMatcher.matches(item);
    }

    @Override
    protected <I extends MatchResult<T>> MatchResult<I> matchResultSafely(I item) {
        if (resultMatcher == null || item.matched() != match) {
            return valueResult(item);
        }
        MatchResult<I> msgResult = quickMatchResult(resultMatcher, item);
        return messageResult(item, msgResult);
    }

    private <I extends MatchResult<T>> MatchResult<I> valueResult(final I item) {
        return new AbstractMatchResult<I,Matcher<?>>(item, this, item.matched() == match) {
            @Override
            public void describeMatch(Description d) {
                d.appendText("was ")
                        .appendText(match ? "match " : "mismatch ")
                        .appendValue(item);
            }
            @Override
            public void describeMismatch(Description d) {
                d.appendText("was ")
                        .appendText(match ? "mismatch " : "match ")
                        .appendValue(item);
            }
        };
    }

    private <I extends MatchResult<T>> MatchResult<I> messageResult(I item, final MatchResult<I> mr) {
        return new NestedResult<I, Matcher<?>>(item, this, mr.matched()) {
            @Override
            public void describeMatch(Description d) {
                d.appendText(match ? "match " : "mismatch ");
                nestedDescribeTo(getMatchPrecedence(), mr, d);
            }
            @Override
            public void describeMismatch(Description d) {
                d.appendText(match ? "match " : "mismatch ");
                nestedDescribeTo(getMismatchPrecedence(), mr, d);
            }
        };
    }
}
