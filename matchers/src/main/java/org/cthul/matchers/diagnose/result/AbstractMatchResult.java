package org.cthul.matchers.diagnose.result;

import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribing;
import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribingBase;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 *
 */
public class AbstractMatchResult<T, M extends Matcher<?>> 
                extends MatchResultMismatch<T, M>
                implements MatchResult.Match<T> {

    private final boolean success;
    private final PrecedencedSelfDescribing providedMatchDescription;
    private PrecedencedSelfDescribing matchDescription = null;

    public AbstractMatchResult(T value, M matcher) {
        super(value, matcher);
        this.success = false;
        this.providedMatchDescription = null;
    }
    
    public AbstractMatchResult(T value, M matcher, boolean success) {
        super(value, matcher);
        this.success = success;
        this.providedMatchDescription = null;
    }
    
    public AbstractMatchResult(T value, M matcher, boolean success, final String description) {
        super(value, matcher, success ? null : description);
        this.success = success;
        if (!success || description == null) {
            this.providedMatchDescription = null;
        } else {
            this.providedMatchDescription = new PrecedencedSelfDescribingBase() {
                @Override
                public void describeTo(Description d) {
                    d.appendText(description);
                }
                @Override
                public int getDescriptionPrecedence() {
                    return getMatchPrecedence();
                }
            };
        }
    }
    
    public AbstractMatchResult(T value, M matcher, boolean success, final SelfDescribing description) {
        super(value, matcher, success ? null : description);
        this.success = success;
        if (!success || description == null) {
            this.providedMatchDescription = null;
        } else {
            this.providedMatchDescription = new PrecedencedSelfDescribingBase() {
                @Override
                public void describeTo(Description d) {
                    d.appendDescriptionOf(description);
                }
                @Override
                public int getDescriptionPrecedence() {
                    return getMatchPrecedence();
                }
            };
        }
    }
    
    public AbstractMatchResult(T value, M matcher, boolean success, PrecedencedSelfDescribing description) {
        super(value, matcher, success ? null : description);
        this.success = success;
        if (!success) {
            this.providedMatchDescription = null;
        } else {
            this.providedMatchDescription = description;
        }
    }
    
    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public Match<T> getMatch() {
        return isSuccess() ? this : null;
    }

    @Override
    public Mismatch<T> getMismatch() {
        return isSuccess() ? null : this;
    }

    @Override
    public void describeTo(Description description) {
        if (isSuccess()) { 
            describeMatch(description);
        } else {
            describeMismatch(description);
        }
    }

    @Override
    public int getDescriptionPrecedence() {
        if (isSuccess()) {
            return getMatchPrecedence();
        } else {
            return getMismatchPrecedence();
        }
    }

    @Override
    public void describeMatch(Description d) {
        if (providedMatchDescription != null) {
            d.appendDescriptionOf(providedMatchDescription);
        } else {
            describeMatcher(d);
        }
    }

    @Override
    public int getMatchPrecedence() {
        return getMatcherPrecedence();
    }

    @Override
    public PrecedencedSelfDescribing getMatchDescription() {
        if (matchDescription == null) {
            matchDescription = new PrecedencedSelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    describeMatch(description);
                }
                @Override
                public int getDescriptionPrecedence() {
                    return getMatchPrecedence();
                }
            };
        }
        return matchDescription;
    }
}
