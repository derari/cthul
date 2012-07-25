/*
 * 
 */

package org.cthul.matchers.proc;

import org.cthul.matchers.diagnose.TypesafeQuickDiagnoseMatcherBase;
import org.cthul.proc.Proc;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsAnything;
import org.hamcrest.core.IsEqual;

/**
 *
 * @author Arian Treffer
 */
public class Returns extends TypesafeQuickDiagnoseMatcherBase<Proc> {

    /**
     * Does the proc return a value equal to {@code value}?
     * @param value
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> result(Object value) {
        return returns(value);
    }

    /**
     * Does the proc return a value that satisfies the condition?
     * @param resultMatcher
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> result(Matcher<?> resultMatcher) {
        return returns(resultMatcher);
    }
    
    /**
     * Does the proc complete without throwing an exception?
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> hasResult() {
        return returns();
    }

    /**
     * Does the proc return a value equal to {@code value}?
     * @param value
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> returns(Object value) {
        return new Returns(IsEqual.equalTo(value));
    }

    /**
     * Does the proc return a value that satisfies the condition?
     * @param resultMatcher
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> returns(Matcher<?> resultMatcher) {
        return new Returns(resultMatcher);
    }
    
    /**
     * Does the proc complete without throwing an exception?
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> returns() {
        return new Returns(IsAnything.anything());
    }

    private final Matcher<?> resultMatcher;
    
    public Returns(Matcher<?> resultMatcher) {
        this.resultMatcher = resultMatcher;
    }

    /** {@inheritDoc} */
    @Override
    public void describeTo(Description description) {
        description.appendText("returns ")
                   .appendDescriptionOf(resultMatcher);
    }

    /** {@inheritDoc} */
    @Override
    protected boolean matchesSafely(Proc proc) {
        if (!proc.hasResult()) {
            return false;
        }
        return resultMatcher.matches(proc.getResult());
    }

    /** {@inheritDoc} */
    @Override
    protected void describeMismatchSafely(Proc proc, Description mismatch) {
        if (!proc.hasResult()) {
            mismatch.appendText("threw ")
                    .appendValue(proc.getException());
        } else {
            mismatch.appendText("returned ");
            resultMatcher.describeMismatch(proc.getResult(), mismatch);
        }
    }

    @Override
    protected boolean matchesSafely(Proc proc, Description mismatch) {
        if (!proc.hasResult()) {
            mismatch.appendText("threw ")
                    .appendValue(proc.getException());
            return false;
        } else {
            return matches(resultMatcher, proc.getResult(), mismatch, "returned $1");
        }
    }

}