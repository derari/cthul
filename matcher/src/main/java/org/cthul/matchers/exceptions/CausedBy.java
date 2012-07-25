package org.cthul.matchers.exceptions;

import org.cthul.matchers.diagnose.TypesafeQuickDiagnoseMatcherBase;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Matches excpetion chains.
 * 
 * @author Arian Treffer
 */
public class CausedBy extends TypesafeQuickDiagnoseMatcherBase<Throwable> {
    
    @Factory
    public static CausedBy causedBy(Matcher<? super Throwable> matcher) {
        return new CausedBy(false, matcher);
    }
    
//    @Factory
//    public static CausedBy cause(Matcher<? super Throwable> matcher) {
//        return causedBy(matcher);
//    }
    
    @Factory
    public static CausedBy causedBy(Class<? extends Throwable> clazz) {
        return causedBy(IsThrowable.throwable(clazz));
    }
    
    @Factory
    public static CausedBy causedBy(String message) {
        return causedBy(IsThrowable.throwable(message));
    }
    
    @Factory
    public static CausedBy causedBy(Class<? extends Throwable> clazz, String message) {
        return causedBy(IsThrowable.throwable(clazz, message));
    }
    
    @Factory
    public static CausedBy causedBy(Class<? extends Throwable> clazz, Matcher<? super Throwable> matcher) {
        return causedBy(IsThrowable.throwable(clazz, matcher));
    }
    
    @Factory
    public static CausedBy causedBy(Class<? extends Throwable> clazz, String message, Matcher<? super Throwable> matcher) {
        return causedBy(IsThrowable.throwable(clazz, message, matcher));
    }

    @Factory
    public static CausedBy directlyCausedBy(Matcher<? super Throwable> matcher) {
        return new CausedBy(true, matcher);
    }
    
//    @Factory
//    public static CausedBy directCause(Matcher<? super Throwable> matcher) {
//        return directlyCausedBy(matcher);
//    }
    
    @Factory
    public static CausedBy directlyCausedBy(Class<? extends Throwable> clazz) {
        return directlyCausedBy(IsThrowable.throwable(clazz));
    }
    
    @Factory
    public static CausedBy directlyCausedBy(Class<? extends Throwable> clazz, String message) {
        return directlyCausedBy(IsThrowable.throwable(clazz, message));
    }
    
    @Factory
    public static CausedBy directlyCausedBy(Class<? extends Throwable> clazz, Matcher<? super Throwable> matcher) {
        return directlyCausedBy(IsThrowable.throwable(clazz, matcher));
    }
    
    @Factory
    public static CausedBy directlyCausedBy(Class<? extends Throwable> clazz, String message, Matcher<? super Throwable> matcher) {
        return directlyCausedBy(IsThrowable.throwable(clazz, message, matcher));
    }

    private final boolean direct;
    private final Matcher<? super Throwable> throwableMatcher;

    public CausedBy(boolean direct, Matcher<? super Throwable> m) {
        this.direct = direct;
        this.throwableMatcher = m;
    }

    @Override
    protected boolean matchesSafely(Throwable ex) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (throwableMatcher.matches(cause)) {
                return true;
            }
            if (direct) {
                return false;
            }
            cause = cause.getCause();
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        if (direct) {
            description.appendText("directly ");
        }
        description.appendText("caused by ");
        throwableMatcher.describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(Throwable ex, Description mismatch) {
        Throwable cause = ex.getCause();
        if (cause == null) {
            mismatch.appendText("no cause");
            return;
        }
        if (direct || cause.getCause() == null) {
            mismatch.appendText("cause ");
            throwableMatcher.describeMismatch(cause, mismatch);
            return;
        }
        
        int i = 1;
        while (cause != null) {
            mismatch.appendText("cause ").
                     appendText(String.valueOf(i)).
                     appendText(" ");
            throwableMatcher.describeMismatch(cause, mismatch);
            cause = cause.getCause();
            if (cause != null) {
                i++;
                mismatch.appendText(", ");
            }
        }
    }

    @Override
    protected boolean matchesSafely(Throwable ex, Description mismatchDescription) {
        if (matchesSafely(ex)) {
            return true;
        } else {
            describeMismatchSafely(ex, mismatchDescription);
            return false;
        }
    }

}