package org.cthul.matchers.diagnose.nested;

import java.util.Collection;
import java.util.Iterator;
import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.diagnose.result.AbstractMatchResult;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResultMismatch;
import org.cthul.matchers.diagnose.result.MatchResultSuccess;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

/**
 * Provides utility methods for generated messages with nested matchers.
 */
public class Nested {

    /**
     * If {@code o} is a {@link PrecedencedSelfDescribing},
     * calls {@link PrecedencedSelfDescribing#getDescriptionPrecedence()},
     * otherwise returns {@link PrecedencedSelfDescribing#P_ATOMIC}.
     * @param o
     * @return precedence value
     */
    public static int precedenceOf(Object o) {
        if (o instanceof PrecedencedSelfDescribing) {
            return ((PrecedencedSelfDescribing) o).getDescriptionPrecedence();
        } else {
            return PrecedencedSelfDescribing.P_ATOMIC;
        }
    }
    
    /**
     * Returns {@link PrecedencedSelfDescribing#P_ATOMIC P_ATOMIC} if {@code count == 0},
     * {@link PrecedencedSelfDescribing#P_UNARY P_UNARY} if {@code count == 1},
     * and otherwise {@code p}.
     * @param p
     * @param count
     * @return precedence
     */
    public static int pAtomicUnaryOr(int p, int count) {
        switch (count) {
            case 0: return PrecedencedSelfDescribing.P_ATOMIC;
            case 1: return PrecedencedSelfDescribing.P_UNARY;
            default: return p;
        }
    }

    /**
     * Appends description of {@code s} to {@code d},
     * enclosed in parentheses if necessary.
     * @param self
     * @param d
     * @param nested 
     */
    public static  void describeTo(PrecedencedSelfDescribing self, SelfDescribing nested, Description d) {
        boolean paren = useParen(self, nested);
        describeTo(paren, nested, d);
    }
    
    /**
     * Appends description of {@code s} to {@code d},
     * enclosed in parentheses if necessary.
     * @param self
     * @param nested 
     * @param d
     * @param message
     */
    public static void describeTo(PrecedencedSelfDescribing self, SelfDescribing nested, Description d, String message) {
        boolean paren = useParen(self, nested);
        describeTo(paren, nested, d, message);
    }
    
    /**
     * Invokes {@link QuickDiagnose#matches(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description)}
     * for {@code m}, 
     * enclosed in parentheses if necessary.
     * @param self
     * @param matcher
     * @param item
     * @param mismatch
     * @return 
     */
    public static boolean matches(PrecedencedSelfDescribing self, Matcher<?> matcher, Object item, Description mismatch) {
        boolean paren = useParen(self, matcher);
        if (paren) {
            return QuickDiagnose.matches(matcher, item, mismatch, "($1)");
        } else {
            return QuickDiagnose.matches(matcher, item, mismatch);
        }
    }
    
    /**
     * Invokes {@link QuickDiagnose#matches(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description, java.lang.String)}
     * for {@code m}, 
     * enclosed in parentheses if necessary.
     * @param self
     * @param matcher
     * @param item
     * @param mismatch
     * @param message
     * @return 
     */
    public static boolean matches(PrecedencedSelfDescribing self, Matcher<?> matcher, Object item, Description mismatch, String message) {
        if (message == null) {
            return matches(self, matcher, item, mismatch);
        }
        boolean paren = useParen(self, matcher);
        if (paren) message = message.replace("$1", "($1)");
        return QuickDiagnose.matches(matcher, item, mismatch, message);
    }

    private static boolean useParen(PrecedencedSelfDescribing pm, Object nested) {
        return useParen(pm.getDescriptionPrecedence(), nested);
    }
    
    private static boolean useParen(int pSelf, Object nested) {
        if (pSelf == PrecedencedSelfDescribing.P_NONE 
                || pSelf == PrecedencedSelfDescribing.P_UNARY_NO_PAREN) {
            return false;
        }
        return useParentheses(pSelf, precedenceOf(nested));
    }

    /**
     * Compares own precedence against nested and return 
     * @param pSelf
     * @param pNested
     * @return true iff parentheses should be used
     */
    public static boolean useParentheses(int pSelf, int pNested) {
        if (pSelf == PrecedencedSelfDescribing.P_NONE
                || pSelf == PrecedencedSelfDescribing.P_UNARY_NO_PAREN) {
            return false;
        }
        if (pNested < PrecedencedSelfDescribing.P_UNARY) {
            return pNested <= pSelf;
        } else {
            return pNested < pSelf;
        }
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
    
    public static void listDescriptions(int myPrecedence, Iterable<? extends SelfDescribing> nested, Description d) {
        joinDescriptions(myPrecedence, nested, d, ", ", ", and ", " and ");
    }
    
    public static void joinMatchDescriptions(int myPrecedence, Iterable<? extends MatchResult<?>> nested, Description d, String sep) {
        joinMatchDescriptions(myPrecedence, nested, d, sep, sep, sep);
    }
    
    public static void joinMatchDescriptions(int myPrecedence, Iterable<? extends MatchResult<?>> nested, Description d, String sep, String lastSep, String singleSep) {
        for (MatchResult<?> mr: collectDescriptions(nested, d, sep, lastSep, singleSep)) {
            mr.getMatch().describeMatch(d);
        }
    }
    
    public static void joinExpectedDescriptions(int myPrecedence, Iterable<? extends MatchResult<?>> nested, Description d, String sep) {
        joinExpectedDescriptions(myPrecedence, nested, d, sep, sep, sep);
    }
    
    public static void joinExpectedDescriptions(int myPrecedence, Iterable<? extends MatchResult<?>> nested, Description d, String sep, String lastSep, String singleSep) {
        for (MatchResult<?> mr: collectDescriptions(nested, d, sep, lastSep, singleSep)) {
            mr.getMismatch().describeExpected(d);
        }
    }
    
    public static void joinMismatchDescriptions(int myPrecedence, Iterable<? extends MatchResult<?>> nested, Description d, String sep) {
        joinMismatchDescriptions(myPrecedence, nested, d, sep, sep, sep);
    }
    
    public static void joinMismatchDescriptions(int myPrecedence, Iterable<? extends MatchResult<?>> nested, Description d, String sep, String lastSep, String singleSep) {
        for (MatchResult<?> mr: collectDescriptions(nested, d, sep, lastSep, singleSep)) {
            mr.getMismatch().describeMismatch(d);
        }
    }
    
    public static void joinDescriptions(int myPrecedence, Iterable<? extends SelfDescribing> nested, Description description, String sep) {
        joinDescriptions(myPrecedence, nested, description, sep, sep, sep);
    }
    
    public static void joinDescriptions(int myPrecedence, Iterable<? extends SelfDescribing> nested, Description description, String sep, String lastSep, String singleSep) {
        for (SelfDescribing sd: collectDescriptions(nested, description, sep, lastSep, singleSep)) {
            boolean paren = useParentheses(myPrecedence, precedenceOf(sd));
            describeTo(paren, sd, description);
        }
    }
    
    public static void joinDescriptions(Collection<? extends SelfDescribing> nested, Description description, String sep) {
        joinDescriptions(nested, description, sep, sep, sep);
    }
    
    public static void joinDescriptions(Collection<? extends SelfDescribing> nested, Description description, String sep, String lastSep, String singleSep) {
        joinDescriptions(PrecedencedSelfDescribing.P_NONE, nested, description, sep, lastSep, singleSep);
    }
    
    public static PrecedencedSelfDescribing joinDescriptions(int myPrecedence, Iterable<? extends SelfDescribing> nested, String sep) {
        return joinDescriptions(myPrecedence, nested, sep, sep, sep);
    }
    
    public static PrecedencedSelfDescribing joinDescriptions(final int myPrecedence, final Iterable<? extends SelfDescribing> nested, final String sep, final String lastSep, final String singleSep) {
        return new PrecedencedSelfDescribingBase() {
            @Override
            public int getDescriptionPrecedence() {
                return myPrecedence;
            }
            @Override
            public void describeTo(Description description) {
                joinDescriptions(myPrecedence, nested, description, sep, lastSep, singleSep);
            }
        };
    }
    
    public static <T> Iterable<T> collectDescriptions(Iterable<T> data, Description target, String sep, String lastSep, String singleSep) {
        return new CollectingIterable<>(data, target, sep, lastSep, singleSep);
    }
    
    public static <T> Iterable<T> collectDescriptions(Iterable<T> data, Description target, String sep) {
        return new CollectingIterable<>(data, target, sep);
    }
    
    private static class CollectingIterable<T> implements Iterable<T> {
        private final Iterable<T> data;
        private final Description target;
        private final String sep;
        private final String lastSep;
        private final String singleSep;

        public CollectingIterable(Iterable<T> data, Description target, String sep, String lastSep, String singleSep) {
            this.data = data;
            this.target = target;
            this.sep = sep;
            this.lastSep = lastSep;
            this.singleSep = singleSep;
        }
        
        public CollectingIterable(Iterable<T> data, Description target, String sep) {
            this.data = data;
            this.target = target;
            this.sep = sep;
            this.lastSep = sep;
            this.singleSep = sep;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                Iterator<T> it = data.iterator();
                int index = 0;
                
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }

                @Override
                public T next() {
                    T result = it.next();
                    if (index++ > 0) {
                        if (it.hasNext()) {
                            target.appendText(sep);
                        } else {
                            target.appendText(index == 2 ? singleSep : lastSep);
                        }
                    }
                    return result;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
    
//    private static abstract class WrappedIterable<T, V> implements Iterable<T> {
//        
//        protected final Iterable<? extends V> it;
//
//        public WrappedIterable(Iterable<? extends V> it) {
//            this.it = it;
//        }
//
//        public WrappedIterable(V[] array) {
//            this(Arrays.asList(array));
//        }
//        
//        protected abstract T get(V source);
//
//        @Override
//        public Iterator<T> iterator() {
//            final Iterator<? extends V> i = it.iterator();
//            return new Iterator<T>() {
//                @Override
//                public boolean hasNext() {
//                    return i.hasNext();
//                }
//                @Override
//                public T next() {
//                    return get(i.next());
//                }
//                @Override
//                public void remove() {
//                    throw new UnsupportedOperationException();
//                }
//            };
//        }
//    }
    
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
