package org.cthul.matchers.diagnose.result;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Can be used as a base class to decorate another match result.
 * The other result is never exposed.
 * @param <I>
 * @param <M>
 */
public class MatchResultProxy<I, M extends Matcher<?>> extends AbstractMatchResult<I, M> {
    
    private final MatchResult<?> result;

    public MatchResultProxy(MatchResult<?> result, I value, M matcher) {
        super(value, matcher);
        this.result = result;
    }

    public MatchResultProxy(MatchResult<?> result) {
        super(null, null);
        this.result = result;
    }
    
    protected MatchResult<?> result() {
        return result;
    }
    
    /**
     * For internal use. Fails if this is a mismatch.
     * @return this
     */
    protected Match<?> match() {
        Match<?> m = result().getMatch();
        if (m == null) {
            throw new IllegalStateException("Match failed");
        }
        return m;
    }
    
    /**
     * For internal use. Fails if this is a match.
     * @return this
     */
    protected Mismatch<?> mismatch() {
        Mismatch<?> m = result().getMismatch();
        if (m == null) {
            throw new IllegalStateException("Match succeded");
        }
        return m;
    }

    @Override
    public boolean matched() {
        return result().matched();
    }

    @Override
    public void describeTo(Description description) {
        result().describeTo(description);
    }

    @Override
    public int getDescriptionPrecedence() {
        return result().getDescriptionPrecedence();
    }

    @Override
    public void describeMatch(Description description) {
        match().describeMatch(description);
    }

    @Override
    public int getMatchPrecedence() {
        return match().getMatchPrecedence();
    }

    @Override
    public void describeExpected(Description description) {
        mismatch().describeExpected(description);
    }

    @Override
    public int getExpectedPrecedence() {
        return mismatch().getExpectedPrecedence();
    }

    @Override
    public void describeMismatch(Description description) {
        mismatch().describeMismatch(description);
    }

    @Override
    public int getMismatchPrecedence() {
        return mismatch().getMismatchPrecedence();
    }
}
