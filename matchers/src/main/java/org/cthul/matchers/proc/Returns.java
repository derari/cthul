package org.cthul.matchers.proc;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.cthul.matchers.diagnose.result.AtomicMismatch;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.safe.TypesafeNestedResultMatcher;
import org.cthul.proc.Proc;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsAnything;
import org.hamcrest.core.IsEqual;

/**
 *
 */
public class Returns extends TypesafeNestedResultMatcher<Proc> {

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
    
    @Override
    protected <I extends Proc> MatchResult<I> matchResultSafely(I proc) {
        if (!proc.hasResult()) {
            StringWriter sw = new StringWriter();
            sw.append("threw <");
            sw.append(proc.getException().toString());
            sw.append("> ");
            proc.getException().printStackTrace(new PrintWriter(sw));
            return new AtomicMismatch<>(proc, this, sw.toString());
        }
        final MatchResult<Object> nested = quickMatchResult(resultMatcher, proc.getResult());
        return new NestedResult<I, Returns>(proc, this, nested.matched()) {
            @Override
            public void describeTo(Description d) {
                d.appendText("result ");
                nestedDescribeTo(getDescriptionPrecedence(), nested, d);
            }
            @Override
            public void describeMatch(Description d) {
                describeTo(d);
            }
            @Override
            public void describeExpected(Description d) {
                d.appendText("returns ");
                nestedDescribeTo(getExpectedPrecedence  (), nested.getMismatch().getExpectedDescription(), d);
            }
            @Override
            public void describeMismatch(Description d) {
                describeTo(d);
            }
        };
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
