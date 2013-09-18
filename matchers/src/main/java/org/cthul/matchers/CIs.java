package org.cthul.matchers;

import org.cthul.matchers.diagnose.nested.NestedResultMatcher;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class CIs<T> extends NestedResultMatcher<T> {
    
    @Factory
    public static <T> CIs<T> is(Matcher<? super T> matcher) {
        return new CIs<>(matcher, "is", false);
    }
    
    @Factory
    public static <T> CIs<T> has(Matcher<? super T> matcher) {
        return new CIs<>(matcher, "has", false);
    }
    
    @Factory
    public static <T> CIs<T> not(Matcher<? super T> matcher) {
        return new CIs<>(matcher, null, true);
    }
    
    @Factory
    public static <T> CIs<T> isNot(Matcher<? super T> matcher) {
        return new CIs<>(matcher, "is", true);
    }
    
    @Factory
    public static <T> CIs<T> _is(Matcher<? super T> matcher) {
        return new CIs<>(matcher, "is", false);
    }
    
    @Factory
    public static <T> CIs<T> _has(Matcher<? super T> matcher) {
        return new CIs<>(matcher, "has", false);
    }
    
    @Factory
    public static <T> CIs<T> _not(Matcher<? super T> matcher) {
        return new CIs<>(matcher, null, true);
    }
    
    @Factory
    public static <T> CIs<T> _isNot(Matcher<? super T> matcher) {
        return new CIs<>(matcher, "is", true);
    }

    private final Matcher<? super T> nested;
    private final String prefix;
    private final String pastPrefix;
    private final boolean not;

    public CIs(Matcher<? super T> nested, String prefix, boolean not) {
        this.nested = nested;
        this.prefix = prefix;
        this.pastPrefix = pastPrefix(prefix);
        this.not = not;
    }

    public CIs(Matcher<? super T> nested, String prefix, String pastPrefix, boolean not) {
        this.nested = nested;
        this.prefix = prefix;
        this.pastPrefix = pastPrefix;
        this.not = not;
    }

    public CIs(Matcher<? super T> nested) {
        this(nested, "is", false);
    }

    @Override
    public boolean matches(Object o) {
        return not ^ nested.matches(o);
    }
    
    @Override
    public boolean matches(Object o, Description d) {
        if (matches(o)) return true;
        describeMismatch(o, d);
        return false;
    }

    @Override
    public void describeTo(Description description) {
        if (prefix != null) {
            description.appendText(prefix).appendText(" ");
        }
        if (not) {
            description.appendText("not ");
        }
        nested.describeTo(description);
    }

    @Override
    public int getDescriptionPrecedence() {
        return P_UNARY;
    }

    @Override
    public <I> MatchResult<I> matchResult(I item) {
        final MatchResult<I> result = quickMatchResult(nested, item);
        return new NestedResult<I, CIs<T>>(item, this, not ^ result.matched()) {
            @Override
            public void describeMatch(Description d) {
                appendPastPrefix(d);
                nestedDescribeTo(getMatchPrecedence(), result, d);
            }
            @Override
            public void describeExpected(Description d) {
                if (prefix != null) {
                    d.appendText(prefix).appendText(" ");
                }
                if (not) {
                    d.appendText("not ");
                    nestedDescribeTo(getExpectedPrecedence(), result, d);
                } else {
                    result.getMismatch().describeExpected(d);
                }
            }
            @Override
            public void describeMismatch(Description d) {
                if (not) {
                    d.appendValue(getValue()).appendText(" ");
                    appendPastPrefix(d);
                } 
                nestedDescribeTo(getMismatchPrecedence(), result, d);
            }
        };
    }
    
    protected void appendPastPrefix(Description d) {
        if (pastPrefix != null) {
            d.appendText(pastPrefix).appendText(" ");
        }
    }

    public static String pastPrefix(String prefix) {
        if (prefix != null) {
            switch (prefix) {
                case "is":
                    return "was";
                case "has":
                    return "had";
                default:
                    return prefix;
            }
        }
        return null;
    }
    
    public static <T> Matcher<T> wrap(String prefix, boolean not, Matcher<? super T> matcher) {
        if (prefix == null && !not) {
            return (Matcher) matcher;
        } else {
            return new CIs<>(matcher, prefix, not);
        }
    }
    
}
