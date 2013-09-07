package org.cthul.matchers.diagnose.result;

import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribing;
import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribingBase;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 *
 */
public class MatchResultMismatch<T, M extends Matcher<?>> 
                extends MatchResultBase<T, M>
                implements MatchResult.Mismatch<T> {

    private final PrecedencedSelfDescribing providedMismatchDescription;
    private PrecedencedSelfDescribing expectedDescription = null;
    private PrecedencedSelfDescribing mismatchDescription = null;

    public MatchResultMismatch(T value, M matcher) {
        super(value, matcher);
        this.providedMismatchDescription = null;
    }
    
    public MatchResultMismatch(T value, M matcher, final String mismatchDescription) {
        super(value, matcher);
        if (mismatchDescription == null) {
            this.providedMismatchDescription = null;
        } else {
            this.providedMismatchDescription = new PrecedencedSelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    description.appendText(mismatchDescription);
                }
                @Override
                public int getDescriptionPrecedence() {
                    return getMismatchPrecedence();
                }
            };
        }
    }
    
    public MatchResultMismatch(T value, M matcher, final SelfDescribing mismatchDescription) {
        super(value, matcher);
        if (mismatchDescription == null) {
            this.providedMismatchDescription = null;
        } else {
            this.providedMismatchDescription = new PrecedencedSelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    description.appendDescriptionOf(mismatchDescription);
                }
                @Override
                public int getDescriptionPrecedence() {
                    return getMismatchPrecedence();
                }
            };
        }
    }

    public MatchResultMismatch(T value, M matcher, PrecedencedSelfDescribing mismatchDescription) {
        super(value, matcher);
        this.providedMismatchDescription = mismatchDescription;
    }
    
    @Override
    public boolean matched() {
        return false;
    }

    @Override
    public Match<T> getMatch() {
        return null;
    }

    @Override
    public Mismatch<T> getMismatch() {
        return this;
    }

    @Override
    public void describeTo(Description description) {
        describeMismatch(description);
    }

    @Override
    public int getDescriptionPrecedence() {
        return getMismatchPrecedence();
    }

    @Override
    public void describeExpected(Description d) {
        describeMatcher(d);
    }

    @Override
    public int getExpectedPrecedence() {
        return getMatcherPrecedence();
    }

    @Override
    public PrecedencedSelfDescribing getExpectedDescription() {
        if (expectedDescription == null) {
            expectedDescription = new PrecedencedSelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    describeExpected(description);
                }
                @Override
                public int getDescriptionPrecedence() {
                    return getExpectedPrecedence();
                }
            };
        }
        return expectedDescription;
    }

    @Override
    public void describeMismatch(Description d) {
        if (providedMismatchDescription != null) {
            d.appendDescriptionOf(providedMismatchDescription);
        } else {
            getMatcher().describeMismatch(getValue(), d);
        }
    }

    @Override
    public int getMismatchPrecedence() {
        return Nested.mismatchPrecedenceOf(getMatcher());
    }

    @Override
    public PrecedencedSelfDescribing getMismatchDescription() {
        if (mismatchDescription == null) {
            mismatchDescription = new PrecedencedSelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    describeMismatch(description);
                }
                @Override
                public int getDescriptionPrecedence() {
                    return getMismatchPrecedence();
                }
            };
        }
        return mismatchDescription;
    }
}
