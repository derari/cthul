package org.cthul.matchers.diagnose.result;

import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribing;
import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribingBase;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * @see MatchResultSuccess
 * @see MatchResultMismatch
 * @see AbstractMatchResult
 * @see MatchResultProxy
 */
public abstract class MatchResultBase<T, M extends Matcher<?>> 
                extends PrecedencedSelfDescribingBase
                implements MatchResult<T> {

    private final T value;
    private final M matcher;

    public MatchResultBase(T value, M matcher) {
        this.value = value;
        this.matcher = matcher;
    }
    
    @Override
    public T getValue() {
        return value;
    }

    @Override
    public M getMatcher() {
        return matcher;
    }

    @Override
    public void describeMatcher(Description d) {
        d.appendDescriptionOf(getMatcher());
    }
    
    @Override
    public int getMatcherPrecedence() {
        return Nested.precedenceOf(getMatcher());
    }

    @Override
    public PrecedencedSelfDescribing getMatcherDescription() {
        final Matcher<?> m = getMatcher();
        if (m instanceof PrecedencedSelfDescribing) {
            return (PrecedencedSelfDescribing) m;
        } else {
            return new PrecedencedSelfDescribingBase() {
                @Override
                public void describeTo(Description description) {
                    description.appendDescriptionOf(m);
                }
                @Override
                public int getDescriptionPrecedence() {
                    return getMatcherPrecedence();
                }
            };
        }
    }
}
