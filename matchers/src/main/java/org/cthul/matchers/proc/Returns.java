package org.cthul.matchers.proc;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.cthul.matchers.diagnose.safe.TypesafeNestedMatcher;
import org.cthul.proc.Proc;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsAnything;
import org.hamcrest.core.IsEqual;

/**
 *
 */
public class Returns extends TypesafeNestedMatcher<Proc> {

    private final Matcher<?> resultMatcher;
    
    public Returns(Matcher<?> resultMatcher) {
        super(Proc.class);
        this.resultMatcher = resultMatcher;
    }

    @Override
    public int getDescriptionPrecedence() {
        return P_UNARY;
    }

    /** {@inheritDoc} */
    @Override
    public void describeTo(Description description) {
        description.appendText("returns ");
        nestedDescribeTo(resultMatcher, description);
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
            StringWriter sw = new StringWriter();
            proc.getException().printStackTrace(new PrintWriter(sw));
            mismatch.appendText(" ").appendText(sw.toString());
        } else {
            mismatch.appendText("returned ");
            nestedDescribeMismatch(resultMatcher, proc.getResult(), mismatch);
        }
    }

    @Override
    protected boolean matchesSafely(Proc proc, Description mismatch) {
        if (!proc.hasResult()) {
            describeMismatchSafely(proc, mismatch);
            return false;
        } else {
            return nestedMatch(resultMatcher, proc.getResult(), mismatch, "returned $1");
        }
    }
    
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

}
