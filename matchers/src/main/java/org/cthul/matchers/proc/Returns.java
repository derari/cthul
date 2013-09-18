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
import org.hamcrest.core.Is;
import org.hamcrest.core.IsAnything;
import org.hamcrest.core.IsEqual;

/**
 *
 */
public class Returns extends TypesafeNestedResultMatcher<Proc> {

    private final Matcher<?> resultMatcher;
    private final String returns;
    private final String returned;

    public Returns(Matcher<?> resultMatcher, String returns, String returned) {
        super(Proc.class);
        this.resultMatcher = resultMatcher;
        this.returns = returns;
        this.returned = returned;
    }
    
    public Returns(Matcher<?> resultMatcher) {
        this(resultMatcher, "returns", "returned");
    }

    @Override
    public int getDescriptionPrecedence() {
        return P_UNARY;
    }

    /** {@inheritDoc} */
    @Override
    public void describeTo(Description description) {
        description.appendText(returns).appendText(" ");
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
            public void describeTo(Description description) {
                description.appendText(returned).appendText(" ");
                nestedDescribeTo(getDescriptionPrecedence(), nested, description);
            }
            @Override
            public void describeMatch(Description description) {
                describeTo(description);
            }
            @Override
            public void describeExpected(Description description) {
                description.appendText(returns).appendText(" ");
                nestedDescribeTo(getExpectedPrecedence  (), nested.getMismatch().getExpectedDescription(), description);
            }
            @Override
            public void describeMismatch(Description description) {
                describeTo(description);
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
        return result(Is.is(value));
    }

    /**
     * Does the proc return a value that satisfies the condition?
     * @param resultMatcher
     * @return Proc-Matcher
     */
    @Factory
    public static Matcher<Proc> result(Matcher<?> resultMatcher) {
        return new Returns(resultMatcher, "result", "result");
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
        return new Returns(IsAnything.anything("anything"));
    }

}
