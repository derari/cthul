package org.cthul.matchers.diagnose.result;

import org.cthul.matchers.diagnose.nested.PrecedencedSelfDescribing;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 * A mismatch that is not nested.
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
        return PrecedencedSelfDescribing.P_ATOMIC;
    }
}
