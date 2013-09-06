package org.cthul.matchers.diagnose.result;

import org.cthul.matchers.diagnose.nested.PrecedencedMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 *
 */
public class AtomicMismatch<I, M extends Matcher<?>> extends MatchResultMismatch<I, M> {

    public AtomicMismatch(I value, M matcher, SelfDescribing mismatchDescription) {
        super(value, matcher, mismatchDescription);
    }

    public AtomicMismatch(I value, M matcher, String mismatchDescription) {
        super(value, matcher, mismatchDescription);
    }

    @Override
    public int getMismatchPrecedence() {
        return PrecedencedMatcher.P_ATOMIC;
    }
    
}
