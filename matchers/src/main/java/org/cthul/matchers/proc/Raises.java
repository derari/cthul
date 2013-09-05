package org.cthul.matchers.proc;

import org.cthul.matchers.diagnose.TypesafeNestedMatcher;
import org.cthul.matchers.exceptions.IsThrowable;
import org.cthul.proc.Proc;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 *
 */
public class Raises extends TypesafeNestedMatcher<Proc> {

    private final Matcher<? super Throwable> exceptionMatcher;

    public Raises(Matcher<? super Throwable> matcher) {
        super(Proc.class);
        exceptionMatcher = matcher;
    }

    @Override
    public int getPrecedence() {
        return P_UNARY;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("throws ");
        nestedDescribe(description, exceptionMatcher);
    }

    @Override
    protected boolean matchesSafely(Proc proc) {
        if (proc.hasResult()) {
            return false;
        }
        return exceptionMatcher.matches(proc.getException());
    }

    @Override
    protected void describeMismatchSafely(Proc proc, Description mismatch) {
        if (proc.hasResult()) {
            mismatch.appendText("threw no exception");
        } else {
            nestedDescribeMismatch(mismatch, exceptionMatcher, proc.getException());
        }
    }

    @Override
    protected boolean matchesSafely(Proc proc, Description mismatchDescription) {
        if (proc.hasResult()) {
            describeMismatchSafely(proc, mismatchDescription);
            return false;
        } else {
            return nestedQuickMatch(exceptionMatcher, proc.getException(), mismatchDescription);
        }
    }

    /**
     * Does the proc raise a throwable that satisfies the condition?
     * 
     * @param throwableMatcher
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> raises(Matcher<? super Throwable> throwableMatcher) {
        return new Raises(throwableMatcher);
    }

    /**
     * Does the proc raise a throwable that satisfies the condition?
     * 
     * @param clazz
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> raises(Class<? extends Throwable> clazz) {
        return raises(IsThrowable.throwable(clazz));
    }

    /**
     * Does the proc raise a throwable that satisfies the condition?
     * 
     * @param regex
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> raises(String regex) {
        return raises(IsThrowable.throwable(regex));
    }

    /**
     * Does the proc raise a throwable that satisfies the condition?
     * 
     * @param clazz
     * @param regex
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> raises(Class<? extends Throwable> clazz, String regex) {
        return raises(IsThrowable.throwable(clazz, regex));
    }
    
    /**
     * Does the proc raise a throwable that satisfies the condition?
     * 
     * @param clazz
     * @param matcher
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> raises(Class<? extends Throwable> clazz, Matcher<? super Throwable> matcher) {
        return raises(IsThrowable.throwable(clazz, matcher));
    }

    /**
     * Does the proc throw an exception?
     * 
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> raisesException() {
        return raises(IsThrowable.exception());
    }

    /**
     * Does the proc raise an exception that satisfies the condition?
     * 
     * @param matcher
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> raisesException(Matcher<? super Exception> matcher) {
        return raises(IsThrowable.exception(matcher));
    }

    /**
     * Does the proc raise an exception that satisfies the condition?
     * @param clazz
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> raisesException(Class<? extends Exception> clazz) {
        return new Raises(IsThrowable.exception(clazz));
    }

    /**
     * Does the proc raise an exception that satisfies the condition?
     * @param regex
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> raisesException(String regex) {
        return new Raises(IsThrowable.exception(regex));
    }

    /**
     * Does the proc raise an exception that satisfies the condition?
     * @param clazz
     * @param regex
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> raisesException(Class<? extends Exception> clazz, String regex) {
        return new Raises(IsThrowable.exception(clazz, regex));
    }

}
