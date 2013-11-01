package org.cthul.matchers.exceptions;

import java.util.*;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.result.AtomicMismatch;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.safe.TypesafeNestedResultMatcher;
import org.hamcrest.*;

/**
 * Matches exception chains.
 */
public class CausedBy extends TypesafeNestedResultMatcher<Throwable> {

    private final boolean direct;
    private final Matcher<? super Throwable> throwableMatcher;

    public CausedBy(boolean direct, Matcher<? super Throwable> m) {
        super(Throwable.class);
        this.direct = direct;
        this.throwableMatcher = m;
    }

    @Override
    public int getDescriptionPrecedence() {
        return P_UNARY;
    }

    @Override
    protected boolean matchesSafely(Throwable ex) {
        Throwable cause = ex.getCause();
        if (direct) {
            return throwableMatcher.matches(cause);
        }
        while (cause != null) {
            if (throwableMatcher.matches(cause)) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a throwable ");
        if (direct) {
            description.appendText("directly ");
        }
        description.appendText("caused by ");
        nestedDescribeTo(throwableMatcher, description);
    }

    @Override
    protected <I extends Throwable> MatchResult<I> matchResultSafely(I ex) {
        Throwable cause = ex.getCause();

        // try direct match
        if (cause == null) {
            return new AtomicMismatch<>(ex, this, "no cause");
        }
        if (direct || cause.getCause() == null) {
            MatchResult<Throwable> mr = quickMatchResult(throwableMatcher, cause);
            if (mr.matched()) {
                return successResult(ex, -1, mr.getMatch());
            } else {
                return failResult(ex, Arrays.asList(mr.getMismatch()));
            }
        }

        // check if chain matches
        List<MatchResult.Mismatch<Throwable>> mismatches = new ArrayList<>();
        int i = 1;
        while (cause != null) {
            MatchResult<Throwable> mr = quickMatchResult(throwableMatcher, cause);
            if (mr.matched()) {
                return successResult(ex, i, mr.getMatch());
            } else {
                mismatches.add(mr.getMismatch());
            }
            i++;
            cause = cause.getCause();
        }

        return failResult(ex, mismatches);
    }
    
    private <I extends Throwable> MatchResult<I> successResult(I ex, final int index, final MatchResult.Match<Throwable> nested) {
        return new NestedMatch<I, CausedBy>(ex, this) {
            @Override
            public void describeMatch(Description description) {
                description.appendText("cause ");
                if (index > 0) {
                    description.appendText(String.valueOf(index));
                    description.appendText(" ");
                }
                nestedDescribeMatch(nested, description);
            }
        };
    }
    
    private <I extends Throwable> MatchResult<I> failResult(I ex, final List<MatchResult.Mismatch<Throwable>> nested) {
        return new NestedMismatch<I, CausedBy>(ex, this) {
            @Override
            public int getMismatchPrecedence() {
                return Nested.pAtomicUnaryOr(P_COMPLEX, nested.size());
            }
            @Override
            public void describeExpected(Description description) {
                if (nested.size() == 1) {
                    description.appendText("cause ");
                    nestedDescribeExpected(nested.get(0), description);
                } else {
                    describeMatcher(description);
                }
            }
            @Override
            public void describeMismatch(Description description) {
                if (nested.size() == 1) {
                    description.appendText("cause ");
                    nestedDescribeMismatch(nested.get(0), description);
                } else {
                    int i = 1;
                    for (MatchResult.Mismatch<Throwable> m: nested) {
                        if (i > 1) {
                            description.appendText(", ");
                        }
                        description.appendText("cause ");
                        description.appendText(String.valueOf(i++));
                        description.appendText(" ");
                        nestedDescribeMismatch(m, description);
                    }
                }
            }
        };
    }
    
    @Factory
    public static CausedBy causedBy(Matcher<? super Throwable> matcher) {
        return new CausedBy(false, matcher);
    }
    
//    @Factory
//    public static CausedBy cause(Matcher<? super Throwable> matcher) {
//        return causedBy(matcher);
//    }
    
    @Factory
    public static CausedBy causedBy(Class<? extends Throwable> clazz) {
        return causedBy(IsThrowable.throwable(clazz));
    }
    
    @Factory
    public static CausedBy causedBy(String message) {
        return causedBy(IsThrowable.throwable(message));
    }
    
    @Factory
    public static CausedBy causedBy(Class<? extends Throwable> clazz, String message) {
        return causedBy(IsThrowable.throwable(clazz, message));
    }
    
    @Factory
    public static CausedBy causedBy(Class<? extends Throwable> clazz, Matcher<? super Throwable> matcher) {
        return causedBy(IsThrowable.throwable(clazz, matcher));
    }
    
    @Factory
    public static CausedBy causedBy(Class<? extends Throwable> clazz, String message, Matcher<? super Throwable> matcher) {
        return causedBy(IsThrowable.throwable(clazz, message, matcher));
    }

    @Factory
    public static CausedBy directlyCausedBy(Matcher<? super Throwable> matcher) {
        return new CausedBy(true, matcher);
    }
    
//    @Factory
//    public static CausedBy directCause(Matcher<? super Throwable> matcher) {
//        return directlyCausedBy(matcher);
//    }
    
    @Factory
    public static CausedBy directlyCausedBy(Class<? extends Throwable> clazz) {
        return directlyCausedBy(IsThrowable.throwable(clazz));
    }
    
    @Factory
    public static CausedBy directlyCausedBy(Class<? extends Throwable> clazz, String message) {
        return directlyCausedBy(IsThrowable.throwable(clazz, message));
    }
    
    @Factory
    public static CausedBy directlyCausedBy(Class<? extends Throwable> clazz, Matcher<? super Throwable> matcher) {
        return directlyCausedBy(IsThrowable.throwable(clazz, matcher));
    }
    
    @Factory
    public static CausedBy directlyCausedBy(Class<? extends Throwable> clazz, String message, Matcher<? super Throwable> matcher) {
        return directlyCausedBy(IsThrowable.throwable(clazz, message, matcher));
    }

}
