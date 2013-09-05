package org.cthul.matchers.exceptions;

import java.util.regex.Pattern;
import org.cthul.matchers.ContainsPattern;
import org.cthul.matchers.diagnose.TypesafeNestedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.Is;

/**
 * Matches the message of an exception.
 */
public class ExceptionMessage extends TypesafeNestedMatcher<Throwable> {
    
    private Matcher<? super String> messageMatcher;

    public ExceptionMessage(Matcher<? super String> messageMatcher) {
        super(Throwable.class);
        this.messageMatcher = messageMatcher;
    }

    @Override
    public int getPrecedence() {
        return P_UNARY;
    }

    @Override
    protected boolean matchesSafely(Throwable ex) {
        return messageMatcher.matches(ex.getMessage());
    }

    @Override
    protected boolean matchesSafely(Throwable ex, Description mismatch) {
        return nestedQuickMatch(messageMatcher, ex.getMessage(), mismatch, "message $1");
    }

    @Override
    protected void describeMismatchSafely(Throwable item, Description mismatch) {
        mismatch.appendText("message ");
        nestedDescribeMismatch(mismatch, messageMatcher, item.getMessage());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("message ");
        nestedDescribe(description, messageMatcher);
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
