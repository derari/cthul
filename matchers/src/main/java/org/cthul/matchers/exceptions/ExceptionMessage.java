package org.cthul.matchers.exceptions;

import java.util.regex.Pattern;
import org.cthul.matchers.ContainsPattern;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.safe.TypesafeNestedResultMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;

/**
 * Matches the message of an exception.
 */
public class ExceptionMessage extends TypesafeNestedResultMatcher<Throwable> {
    
    private Matcher<? super String> messageMatcher;

    public ExceptionMessage(Matcher<? super String> messageMatcher) {
        super(Throwable.class);
        this.messageMatcher = messageMatcher;
    }

    @Override
    public int getDescriptionPrecedence() {
        return P_UNARY;
    }

    @Override
    protected boolean matchesSafely(Throwable ex) {
        return messageMatcher.matches(ex.getMessage());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("message ");
        nestedDescribeTo(messageMatcher, description);
    }

    @Override
    protected <I extends Throwable> MatchResult<I> matchResultSafely(I item) {
        final MatchResult<String> nested = quickMatchResult(messageMatcher, item.getMessage());
        return new NestedResult<I,ExceptionMessage>(item, this, nested.matched()) {
            @Override
            public void describeTo(Description d) {
                d.appendText("message ");
                nestedDescribeTo(getDescriptionPrecedence(), nested, d);
            }
            @Override
            public void describeMatch(Description d) {
                describeTo(d);
            }
            @Override
            public void describeExpected(Description d) {
                d.appendText("message ");
                nestedDescribeTo(getExpectedPrecedence(), nested.getMismatch().getExpectedDescription(), d);
            }
            @Override
            public void describeMismatch(Description d) {
                describeTo(d);
            }
        };
    }
    
    @Factory
    public static ExceptionMessage messageIs(String message) {
        return new ExceptionMessage(Is.is(message));
    }
    
    @Factory
    public static ExceptionMessage messageContains(String regex) {
        return new ExceptionMessage(new ContainsPattern(regex, false));
    }
    
    @Factory
    public static ExceptionMessage messageMatches(String regex) {
        return new ExceptionMessage(new ContainsPattern(regex, true));
    }
    
    @Factory
    public static ExceptionMessage messageContains(Pattern pattern) {
        return new ExceptionMessage(new ContainsPattern(pattern, false));
    }
    
    @Factory
    public static ExceptionMessage messageMatches(Pattern pattern) {
        return new ExceptionMessage(new ContainsPattern(pattern, true));
    }
    
    @Factory
    public static ExceptionMessage message(Matcher<? super String> messageMatcher) {
        return new ExceptionMessage(messageMatcher);
    }
    
}
