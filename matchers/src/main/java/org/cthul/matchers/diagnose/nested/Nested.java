package org.cthul.matchers.diagnose.nested;

import java.util.List;
import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.diagnose.SelfDescribingBase;
import org.cthul.matchers.diagnose.result.AbstractMatchResult;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResultMismatch;
import org.cthul.matchers.diagnose.result.MatchResultSuccess;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

/**
 * 
 */
public class Nested<T> {

    /**
     * If {@code o} is a {@link PrecedencedMatcher},
     * calls {@link PrecedencedMatcher#getPrecedence()},
     * otherwise returns {@link PrecedencedMatcher#P_ATOMIC}.
     * @param o
     * @return precedence value
     */
    public static int precedenceOf(Object o) {
        if (o instanceof PrecedencedSelfDescribing) {
            return ((PrecedencedSelfDescribing) o).getDescriptionPrecedence();
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
    public static int mismatchPrecedenceOf(Object o) {
        if (o instanceof PrecedencedMatcher) {
            return ((PrecedencedMatcher) o).getMismatchPrecedence();
        } else {
            return PrecedencedMatcher.P_ATOMIC;
        }
    }
    
    /**
     * Appends description of {@code s} to {@code d},
     * enclosed in parantheses if necessary.
     * @param self
     * @param d
     * @param nested 
     */
    public static  void describeTo(PrecedencedMatcher self, SelfDescribing nested, Description d) {
        boolean paren = useParen(self, nested);
        describeTo(paren, nested, d);
    }
    
    /**
     * Appends description of {@code s} to {@code d},
     * enclosed in parantheses if necessary.
     * @param self
     * @param nested 
     * @param d
     * @param message
     */
    public static void describeTo(PrecedencedMatcher self, SelfDescribing nested, Description d, String message) {
        boolean paren = useParen(self, nested);
        describeTo(paren, nested, d, message);
    }
    
    private static SelfDescribing mismatch(final Matcher<?> m, final Object item) {
        return new SelfDescribingBase() {
            @Override
            public void describeTo(Description description) {
                m.describeMismatch(item, description);
            }
        };
    }
    
    /**
     * Appends mismatch description of {@code m} to {@code d},
     * enclosed in parantheses if necessary.
     * @param self
     * @param m
     * @param item 
     * @param d
     */
    public static void describeMismatch(PrecedencedMatcher self, Matcher<?> m, Object item, Description d) {
        boolean paren = useParenMismatch(self, m);
        describeTo(paren, mismatch(m, item), d);
    }
    
    /**
     * Appends mismatch description of {@code m} to {@code d},
     * enclosed in parantheses if necessary.
     * @param self
     * @param m
     * @param item 
     * @param d
     * @param message 
     */
    public static void describeMismatch(PrecedencedMatcher self, Matcher<?> m, Object item, Description d, String message) {
        boolean paren = useParenMismatch(self, m);
        describeTo(paren, mismatch(m, item), d, message);
    }
    
    /**
     * Invokes {@link #quickMatch(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description)}
     * for {@code m}, 
     * enclosed in parantheses if necessary.
     * @param self
     * @param matcher
     * @param item
     * @param mismatch
     * @return 
     */
    public static boolean matches(PrecedencedMatcher self, Matcher<?> matcher, Object item, Description mismatch) {
        boolean paren = useParenMismatch(self, matcher);
        if (paren) {
            return QuickDiagnose.matches(matcher, item, mismatch, "($1)");
        } else {
            return QuickDiagnose.matches(matcher, item, mismatch);
        }
    }
    
    /**
     * Invokes {@link #quickMatch(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description, java.lang.String)}
     * for {@code m}, 
     * enclosed in parantheses if necessary.
     * @param matcher
     * @param item
     * @param mismatch
     * @param message
     * @return 
     */
    public static boolean matches(PrecedencedMatcher self, Matcher<?> matcher, Object item, Description mismatch, String message) {
        if (message == null) {
            return matches(self, matcher, item, mismatch);
        }
        boolean paren = useParenMismatch(self, matcher);
        if (paren) message = message.replace("$1", "($1)");
        return QuickDiagnose.matches(matcher, item, mismatch, message);
    }

    private static boolean useParen(PrecedencedMatcher pm, Object nested) {
        int pNested = precedenceOf(nested);
        int pSelf = pm.getDescriptionPrecedence();
        return useParentheses(pSelf, pNested);
    }

    private static boolean useParenMismatch(PrecedencedMatcher pm, Object nested) {
        int pNested = mismatchPrecedenceOf(nested);
        int pSelf = pm.getMismatchPrecedence();
        return useParentheses(pSelf, pNested);
    }
    
    public static boolean useParentheses(int pSelf, int pNested) {
        return pNested < PrecedencedMatcher.P_UNARY ? pNested <= pSelf : pNested < pSelf;
    }
    
    public static void describeTo(boolean paren, SelfDescribing sd, Description d) {
        if (paren) d.appendText("(");
        d.appendDescriptionOf(sd);
        if (paren) d.appendText(")");
    }
    
    public static void describeTo(boolean paren, SelfDescribing sd, Description d, String message) {
        if (message == null) {
            describeTo(paren, sd, d);
            return;
        }
        if (message.contains("$1")) {
            StringDescription tmp = new StringDescription();
            if (paren) tmp.appendText("(");
            tmp.appendDescriptionOf(sd);
            if (paren) tmp.appendText(")");
            d.appendText(message.replace("$1", tmp.toString()));
        } else {
            if (paren) d.appendText("(");
            d.appendText(message);
            if (paren) d.appendText(")");
        }
    }
    
    public static void listDescriptions(int myPrecedence, List<? extends PrecedencedSelfDescribing> nested, Description d) {
        int i = 0;
        final int len = nested.size();
        for (PrecedencedSelfDescribing sd: nested) {
            if (i > 0) {
                if (i < len-1) {
                    d.appendText(", ");
                } else {
                    d.appendText(i == 1 ? " " : ", ");
                    d.appendText("and ");
                }
            }
            i++;
            boolean paren = useParentheses(myPrecedence, sd.getDescriptionPrecedence());
            describeTo(paren, sd, d);
        }
    }
    
    public static class Match<T, M extends Matcher<?>> 
                     extends MatchResultSuccess<T, M> {

        public Match(T value, M matcher) {
            super(value, matcher);
        }
        
        protected void nestedDescribeTo(boolean paren, SelfDescribing sd, Description d) {
            Nested.describeTo(paren, sd, d);
        }
        
        protected void nestedDescribeTo(boolean paren, SelfDescribing sd, Description d, String message) {
            Nested.describeTo(paren, sd, d, message);
        }
        
        protected void nestedDescribeTo(int myPrecedence, PrecedencedSelfDescribing sd, Description d) {
            boolean paren = useParentheses(myPrecedence, sd.getDescriptionPrecedence());
            nestedDescribeTo(paren, sd, d);
        }
        
        protected void nestedDescribeTo(int myPrecedence, PrecedencedSelfDescribing sd, Description d, String message) {
            boolean paren = useParentheses(myPrecedence, sd.getDescriptionPrecedence());
            nestedDescribeTo(paren, sd, d, message);
        }
        
        protected void nestedDescribeMatcher(MatchResult<?> nested, Description d) {
            nestedDescribeMatcher(nested, d, null);
        }
        
        protected void nestedDescribeMatch(Match<?> nested, Description d) {
            nestedDescribeMatch(nested, d, null);
        }
        
        protected void nestedDescribeExpected(int myPrecedence, Mismatch<?> nested, Description d) {
            nestedDescribeExpected(myPrecedence, nested, d, null);
        }
        
        protected void nestedDescribeMismatch(int myPrecedence, Mismatch<?> nested, Description d) {
            nestedDescribeMismatch(myPrecedence, nested, d, null);
        }
        
        protected void nestedDescribeMatcher(MatchResult<?> nested, Description d, String message) {
            nestedDescribeTo(getMatcherPrecedence(), nested.getMatcherDescription(), d, message);
        }
        
        protected void nestedDescribeMatch(Match<?> nested, Description d, String message) {
            nestedDescribeTo(getMatchPrecedence(), nested.getMatchDescription(), d, message);
        }
        
        protected void nestedDescribeExpected(int myPrecedence, Mismatch<?> nested, Description d, String message) {
            nestedDescribeTo(myPrecedence, nested.getExpectedDescription(), d, message);
        }
        
        protected void nestedDescribeMismatch(int myPrecedence, Mismatch<?> nested, Description d, String message) {
            nestedDescribeTo(myPrecedence, nested.getMismatchDescription(), d, message);
        }
    }
    
    public static class Mismatch<T, M extends Matcher<?>> 
                     extends MatchResultMismatch<T, M> {

        public Mismatch(T value, M matcher) {
            super(value, matcher);
        }
        
        protected void nestedDescribeTo(boolean paren, SelfDescribing sd, Description d) {
            Nested.describeTo(paren, sd, d);
        }
        
        protected void nestedDescribeTo(boolean paren, SelfDescribing sd, Description d, String message) {
            Nested.describeTo(paren, sd, d, message);
        }
        
        protected void nestedDescribeTo(int myPrecedence, PrecedencedSelfDescribing sd, Description d) {
            boolean paren = useParentheses(myPrecedence, sd.getDescriptionPrecedence());
            nestedDescribeTo(paren, sd, d);
        }
        
        protected void nestedDescribeTo(int myPrecedence, PrecedencedSelfDescribing sd, Description d, String message) {
            boolean paren = useParentheses(myPrecedence, sd.getDescriptionPrecedence());
            nestedDescribeTo(paren, sd, d, message);
        }
        
        protected void nestedDescribeMatcher(MatchResult<?> nested, Description d) {
            nestedDescribeMatcher(nested, d, null);
        }
        
        protected void nestedDescribeExpected(Mismatch<?> nested, Description d) {
            nestedDescribeExpected(nested, d, null);
        }
        
        protected void nestedDescribeMismatch(Mismatch<?> nested, Description d) {
            nestedDescribeMismatch(nested, d, null);
        }
        
        protected void nestedDescribeMatch(int myPrecedence, Match<?> nested, Description d) {
            nestedDescribeMatch(myPrecedence, nested, d, null);
        }
        
        protected void nestedDescribeMatcher(MatchResult<?> nested, Description d, String message) {
            nestedDescribeTo(getMatcherPrecedence(), nested.getMatcherDescription(), d, message);
        }
        
        protected void nestedDescribeExpected(Mismatch<?> nested, Description d, String message) {
            nestedDescribeTo(getExpectedPrecedence(), nested.getExpectedDescription(), d, message);
        }
        
        protected void nestedDescribeMismatch(Mismatch<?> nested, Description d, String message) {
            nestedDescribeTo(getMismatchPrecedence(), nested.getMismatchDescription(), d, message);
        }
        
        protected void nestedDescribeMatch(int myPrecedence, Match<?> nested, Description d, String message) {
            nestedDescribeTo(myPrecedence, nested.getMatchDescription(), d, message);
        }
    }
    
    public static class Result<T, M extends Matcher<?>> 
                     extends AbstractMatchResult<T, M> {

        public Result(T value, M matcher) {
            super(value, matcher);
        }

        public Result(T value, M matcher, boolean success) {
            super(value, matcher, success);
        }
        
        protected void nestedDescribeTo(boolean paren, SelfDescribing sd, Description d) {
            Nested.describeTo(paren, sd, d);
        }
        
        protected void nestedDescribeTo(boolean paren, SelfDescribing sd, Description d, String message) {
            Nested.describeTo(paren, sd, d, message);
        }
        
        protected void nestedDescribeTo(int myPrecedence, PrecedencedSelfDescribing sd, Description d) {
            boolean paren = useParentheses(myPrecedence, sd.getDescriptionPrecedence());
            nestedDescribeTo(paren, sd, d);
        }
        
        protected void nestedDescribeTo(int myPrecedence, PrecedencedSelfDescribing sd, Description d, String message) {
            boolean paren = useParentheses(myPrecedence, sd.getDescriptionPrecedence());
            nestedDescribeTo(paren, sd, d, message);
        }
    }
}
