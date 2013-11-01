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
public class MatcherAccepts<T> extends TypesafeNestedResultMatcher<Matcher<? super T>> {
    
    @Factory
    public static <T> Matcher<Matcher<? super T>> accepts(T value) {
        return new MatcherAccepts<>(value);
    }
    
    @Factory
    public static <T> Matcher<Matcher<? super T>> rejects(T value) {
        return new MatcherAccepts<>(false, value);
    }

    @Factory
    public static <T> Matcher<Matcher<? super T>> accepts(T value, Matcher<? super MatchResult<T>> resultMatcher) {
        return new MatcherAccepts<>(value, resultMatcher);
    }
    
    @Factory
    public static <T> Matcher<Matcher<? super T>> rejects(T value, Matcher<? super MatchResult<T>> resultMatcher) {
        return new MatcherAccepts<>(false, value, resultMatcher);
    }

    @Factory
    public static <T> Matcher<Matcher<? super T>> acceptsWithMessage(T value, Matcher<? super String> messageMatcher) {
        return new MatcherAccepts<>(value, HasDescription.message(messageMatcher));
    }
    
    @Factory
    public static <T> Matcher<Matcher<? super T>> rejectsWithMessage(T value, Matcher<? super String> messageMatcher) {
        return new MatcherAccepts<>(false, value, HasDescription.message(messageMatcher));
    }

    @Factory
    public static <T> Matcher<Matcher<? super T>> rejectsWithExpectedMessage(T value, Matcher<? super String> messageMatcher) {
        return new MatcherAccepts<>(false, value, MatchResultExpected.expectedMessage(messageMatcher));
    }

    @Factory
    public static <T> Matcher<Matcher<? super T>> acceptsWithMessage(T value, String messageMatcher) {
        return new MatcherAccepts<>(value, HasDescription.message(messageMatcher));
    }
    
    @Factory
    public static <T> Matcher<Matcher<? super T>> rejectsWithMessage(T value, String messageMatcher) {
        return new MatcherAccepts<>(false, value, HasDescription.message(messageMatcher));
    }

    @Factory
    public static <T> Matcher<Matcher<? super T>> rejectsWithExpectedMessage(T value, String messageMatcher) {
        return new MatcherAccepts<>(false, value, MatchResultExpected.expectedMessage(messageMatcher));
    }

    @Factory
    public static <T> Matcher<Matcher<? super T>> accepts(T value, String messageMatcher) {
        return acceptsWithMessage(value, messageMatcher);
    }
    
    @Factory
    public static <T> Matcher<Matcher<? super T>> rejects(T value, String messageMatcher) {
        return rejectsWithMessage(value, messageMatcher);
    }

    @Factory
    public static <T> Matcher<Matcher<? super T>> expects(T value, String messageMatcher) {
        return rejectsWithExpectedMessage(value, messageMatcher);
    }

    private final boolean match;
    private final T value;
    private final Matcher<? super MatchResult<T>> resultMatcher;

    public MatcherAccepts(T value) {
        super(Matcher.class);
        this.match = true;
        this.value = value;
        this.resultMatcher = null;
    }

    public MatcherAccepts(T value, Matcher<? super MatchResult<T>> resultMatcher) {
        super(Matcher.class);
        this.match = true;
        this.value = value;
        this.resultMatcher = resultMatcher;
    }

    public MatcherAccepts(boolean match, T value) {
        super(Matcher.class);
        this.match = match;
        this.value = value;
        this.resultMatcher = null;
    }

    public MatcherAccepts(boolean match, T value, Matcher<? super MatchResult<T>> resultMatcher) {
        super(Matcher.class);
        this.match = match;
        this.value = value;
        this.resultMatcher = resultMatcher;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a matcher ")
                .appendText(match ? "accepting " : "rejecting ")
                .appendValue(value);
        if (resultMatcher != null) {
            description
                    .appendText(" and ")
                    .appendText(match ? "match " : "mismatch ");
            nestedDescribeTo(resultMatcher, description);
        }
    }

    @Override
    public int getDescriptionPrecedence() {
        return resultMatcher == null ? P_ATOMIC : P_UNARY;
    }
    
    @Override
    protected boolean matchesSafely(Matcher<? super T> item) {
        if (item.matches(value) != match) {
            return false;
        }
        if (resultMatcher == null) {
            return true;
        }
        return resultMatcher.matches(quickMatchResult(item, value));
    }

    @Override
    protected <I extends Matcher<? super T>> MatchResult<I> matchResultSafely(I item) {
        MatchResult<T> valueResult = quickMatchResult(item, value);
        if (resultMatcher == null || valueResult.matched() != match) {
            return valueResult(item, valueResult);
        }
        MatchResult<MatchResult<T>> msgResult = quickMatchResult(resultMatcher, valueResult);
        return messageResult(item, msgResult);
    }

    private <I extends Matcher<? super T>> MatchResult<I> valueResult(I item, final MatchResult<T> mr) {
        return new AbstractMatchResult<I,Matcher<?>>(item, this, mr.matched() == match) {
            @Override
            public void describeMatch(Description d) {
                d.appendValue(getValue())
                        .appendText(match ? " accepted " : " rejected ")
                        .appendValue(value)
                        .appendText(": '")
                        .appendValue(mr)
                        .appendText("'");
            }
            @Override
            public void describeMismatch(Description d) {
                d.appendValue(getValue())
                        .appendText(match ? " rejected " : " accepted ")
                        .appendValue(value)
                        .appendText(": '")
                        .appendValue(mr)
                        .appendText("'");
            }
        };
    }

    private <I extends Object & Matcher<? super T>> MatchResult<I> messageResult(I item, final MatchResult<MatchResult<T>> mr) {
        return new NestedResult<I, Matcher<?>>(item, this, mr.matched()) {
            @Override
            public void describeMatch(Description d) {
                d.appendValue(getValue());
                d.appendText(match ? " match " : " mismatch ");
                nestedDescribeTo(getMatchPrecedence(), mr, d);
            }
            @Override
            public void describeMismatch(Description d) {
                d.appendValue(getValue());
                d.appendText(match ? " match " : " mismatch ");
                nestedDescribeTo(getMismatchPrecedence(), mr, d);
            }
        };
    }
}
