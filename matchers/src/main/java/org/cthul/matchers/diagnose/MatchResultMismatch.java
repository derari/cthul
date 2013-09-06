package org.cthul.matchers.diagnose;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 *
 */
public class MatchResultMismatch<T, M extends Matcher<?>> 
                implements MatchResult<T>,
                           MatchResult.Mismatch<T> {

    protected final T value;
    protected final M matcher;
    private final SelfDescribing providedMismatchDescription;
    private SelfDescribing expectedDescription;
    private SelfDescribing mismatchDescription;

    public MatchResultMismatch(T value, M matcher) {
        this.value = value;
        this.matcher = matcher;
        this.providedMismatchDescription = null;
    }
    
    public MatchResultMismatch(T value, M matcher, final String mismatchDescription) {
        this.value = value;
        this.matcher = matcher;
        this.providedMismatchDescription = new SelfDescribingBase() {
            @Override
            public void describeTo(Description description) {
                description.appendText(mismatchDescription);
            }
        };
    }
    
    public MatchResultMismatch(T value, M matcher, SelfDescribing mismatchDescription) {
        this.value = value;
        this.matcher = matcher;
        this.providedMismatchDescription = mismatchDescription;
    }
    
    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public Mismatch<T> getMismatch() {
        return this;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public M getMatcher() {
        return matcher;
    }

    /**
     * If {@code o} is a {@link PrecedencedMatcher},
     * calls {@link PrecedencedMatcher#getPrecedence()},
     * otherwise returns {@link PrecedencedMatcher#P_ATOMIC}.
     * @param o
     * @return precedence value
     */
    protected int precedenceOf(Object o) {
        if (o instanceof PrecedencedMatcher) {
            return ((PrecedencedMatcher) o).getPrecedence();
        } else {
            return PrecedencedMatcher.P_ATOMIC;
        }
    }

    /**
     * If {@code o} is a {@link PrecedencedMatcher},
     * calls {@link PrecedencedMatcher#getMismatchPrecedence()},
     * otherwise returns {@link PrecedencedMatcher#P_ATOMIC}.
     * @param o
     * @return precedence value
     */
    protected int mismatchPrecedenceOf(Object o) {
        if (o instanceof PrecedencedMatcher) {
            return ((PrecedencedMatcher) o).getMismatchPrecedence();
        } else {
            return PrecedencedMatcher.P_ATOMIC;
        }
    }

    @Override
    public void describeMatcher(Description d) {
        d.appendDescriptionOf(getMatcher());
    }
    
    @Override
    public int getMatcherPrecedence() {
        return precedenceOf(getMatcher());
    }

    @Override
    public SelfDescribing getMatcherDescription() {
        return getMatcher();
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
    public SelfDescribing getExpectedDescription() {
        if (expectedDescription == null) {
            expectedDescription = new SelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    describeExpected(description);
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
        return mismatchPrecedenceOf(getMatcher());
    }

    @Override
    public SelfDescribing getMismatchDescription() {
        if (mismatchDescription == null) {
            mismatchDescription = new SelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    describeMismatch(description);
                }
            };
        }
        return mismatchDescription;
    }
}
