package org.cthul.matchers.diagnose.result;

import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribing;
import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribingBase;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 * Implements a successful match.
 */
public class MatchResultSuccess<T, M extends Matcher<?>> 
                extends MatchResultBase<T, M>
                implements MatchResult.Match<T> {

    private final PrecedencedSelfDescribing providedMatchDescription;
    private PrecedencedSelfDescribing matchDescription = null;

    public MatchResultSuccess(T value, M matcher) {
        super(value, matcher);
        this.providedMatchDescription = null;
    }
    
    public MatchResultSuccess(T value, M matcher, final String matchDescription) {
        super(value, matcher);
        if (matchDescription == null) {
            this.providedMatchDescription = null;   
        } else {
            this.providedMatchDescription = new PrecedencedSelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    description.appendText(matchDescription);
                }
                @Override
                public int getDescriptionPrecedence() {
                    return getMatchPrecedence();
                }
            };
        }
    }
    
    public MatchResultSuccess(T value, M matcher, final SelfDescribing matchDescription) {
        super(value, matcher);
        if (matchDescription == null) {
            this.providedMatchDescription = null;
        } else if (matchDescription instanceof PrecedencedSelfDescribing) {
            this.providedMatchDescription = (PrecedencedSelfDescribing) matchDescription;
        } else {
            this.providedMatchDescription = new PrecedencedSelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    description.appendDescriptionOf(matchDescription);
                }
                @Override
                public int getDescriptionPrecedence() {
                    return getMatchPrecedence();
                }
            };
        }
    }
    
    public MatchResultSuccess(T value, M matcher, PrecedencedSelfDescribing matchDescription) {
        super(value, matcher);
        this.providedMatchDescription = matchDescription;
    }
    
    @Override
    public boolean matched() {
        return true;
    }

    @Override
    public Match<T> getMatch() {
        return this;
    }

    @Override
    public Mismatch<T> getMismatch() {
        return null;
    }

    @Override
    public void describeTo(Description description) {
        describeMatch(description);
    }

    @Override
    public int getDescriptionPrecedence() {
        return getMatchPrecedence();
    }

    @Override
    public void describeMatch(Description d) {
        if (providedMatchDescription != null) {
            d.appendDescriptionOf(providedMatchDescription);
        } else {
            d.appendText("was ");
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
