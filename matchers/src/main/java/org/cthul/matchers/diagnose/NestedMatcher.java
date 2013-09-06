package org.cthul.matchers.diagnose;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

/**
 * Combines multiple matchers.
 * When creating a (mismatch) description, {@link #getPrecedence()} is
 * used to determine if parantheses should be inserted to resolve ambiguities.
 * 
 * @param <T> 
 * @see Matcher
 */
public abstract class NestedMatcher<T> 
                extends QuickDiagnosingMatcherBase<T>
                implements PrecedencedMatcher {

    @Override
    public int getMismatchPrecedence() {
        return getPrecedence();
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
    
    /**
     * Appends description of {@code s} to {@code d},
     * enclosed in parantheses if necessary.
     * @param d
     * @param s 
     */
    protected void nestedDescribe(Description d, SelfDescribing s) {
        boolean paren = useParen(s);
        if (paren) d.appendText("(");
        s.describeTo(d);
        if (paren) d.appendText(")");
    }
    
    /**
     * Appends description of {@code s} to {@code d},
     * enclosed in parantheses if necessary.
     * @param d
     * @param s 
     * @param message
     */
    protected void nestedDescribe(Description d, SelfDescribing s, String message) {
        if (message == null) {
            nestedDescribe(d, s);
            return;
        }
        boolean paren = useParen(s);
        if (paren) d.appendText("(");
        if (message.contains("$1")) {
            StringDescription sd = new StringDescription();
            sd.appendDescriptionOf(s);
            d.appendText(message.replace("$1", sd.toString()));
        } else {
            d.appendText(message);
        }
        if (paren) d.appendText(")");
    }
    
    /**
     * Appends mismatch description of {@code m} to {@code d},
     * enclosed in parantheses if necessary.
     * @param d
     * @param m
     * @param item 
     */
    protected void nestedDescribeMismatch(Description d, Matcher<?> m, Object item) {
        boolean paren = useParenMismatch(m);
        if (paren) d.appendText("(");
        m.describeMismatch(item, d);
        if (paren) d.appendText(")");
    }
    
    /**
     * Appends mismatch description of {@code m} to {@code d},
     * enclosed in parantheses if necessary.
     * @param d
     * @param m
     * @param item 
     * @param message 
     */
    protected void nestedDescribeMismatch(Description d, final Matcher<?> m, final Object item, String message) {
        boolean paren = useParenMismatch(m);
        if (paren) d.appendText("(");
        if (message.contains("$1")) {
            StringDescription sd = new StringDescription();
            m.describeMismatch(item, sd);
            d.appendText(message.replace("$1", sd.toString()));
        } else {
            d.appendText(message);
        }
        if (paren) d.appendText(")");
    }
    
    /**
     * Invokes {@link #quickMatch(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description)}
     * for {@code m}, 
     * enclosed in parantheses if necessary.
     * @param matcher
     * @param item
     * @param mismatch
     * @return 
     */
    protected boolean nestedQuickMatch(Matcher<?> matcher, Object item, Description mismatch) {
        boolean paren = useParenMismatch(matcher);
        if (paren) {
            return quickMatch(matcher, item, mismatch, "($1)");
        } else {
            return quickMatch(matcher, item, mismatch);
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
    protected boolean nestedQuickMatch(Matcher<?> matcher, Object item, Description mismatch, String message) {
        if (message == null) {
            return nestedQuickMatch(matcher, item, mismatch);
        }
        boolean paren = useParenMismatch(matcher);
        if (paren) message = "(" + message + ")";
        return quickMatch(matcher, item, mismatch, message);
    }

    private boolean useParen(Object nested) {
        int pNested = precedenceOf(nested);
        int pSelf = getPrecedence();
        return useParentheses(pSelf, pNested);
    }

    private boolean useParenMismatch(Object nested) {
        int pNested = mismatchPrecedenceOf(nested);
        int pSelf = getMismatchPrecedence();
        return useParentheses(pSelf, pNested);
    }
    
    protected static boolean useParentheses(int pSelf, int pNested) {
        return pNested < P_UNARY ? pNested <= pSelf : pNested < pSelf;
    }
    
    protected static class NestedMismatch<T, M extends Matcher<?>> 
                     extends MatchResultMismatch<T, M> {

        public NestedMismatch(T value, M matcher) {
            super(value, matcher);
        }
        
        protected void nestedDescribe(boolean paren, SelfDescribing sd, Description d) {
            if (paren) d.appendText("(");
            d.appendDescriptionOf(sd);
            if (paren) d.appendText(")");
        }
        
        protected void nestedDescribeMatcher(Mismatch<?> nested, Description d) {
            boolean paren = useParentheses(getMatcherPrecedence(), nested.getMatcherPrecedence());
            nestedDescribe(paren, nested.getMatcherDescription(), d);
        }
        
        protected void nestedDescribeExpected(Mismatch<?> nested, Description d) {
            boolean paren = useParentheses(getExpectedPrecedence(), nested.getExpectedPrecedence());
            nestedDescribe(paren, nested.getExpectedDescription(), d);
        }
        
        protected void nestedDescribeMismatch(Mismatch<?> nested, Description d) {
            boolean paren = useParentheses(getMismatchPrecedence(), nested.getMismatchPrecedence());
            nestedDescribe(paren, nested.getMismatchDescription(), d);
        }
        
        protected void nestedDescribe(boolean paren, SelfDescribing sd, Description d, String message) {
            if (message == null) {
                nestedDescribe(paren, sd, d);
                return;
            }
            if (paren) d.appendText("(");
            if (message.contains("$1")) {
                String nestedText = StringDescription.toString(sd);
                d.appendText(message.replace("$1", nestedText));
            } else {
                d.appendText(message);
            }
            if (paren) d.appendText(")");
        }
        
        protected void nestedDescribeMatcher(Mismatch<?> nested, Description d, String message) {
            boolean paren = useParentheses(getMatcherPrecedence(), nested.getMatcherPrecedence());
            nestedDescribe(paren, nested.getMatcherDescription(), d, message);
        }
        
        protected void nestedDescribeExpected(Mismatch<?> nested, Description d, String message) {
            boolean paren = useParentheses(getExpectedPrecedence(), nested.getExpectedPrecedence());
            nestedDescribe(paren, nested.getExpectedDescription(), d, message);
        }
        
        protected void nestedDescribeMismatch(Mismatch<?> nested, Description d, String message) {
            boolean paren = useParentheses(getMismatchPrecedence(), nested.getMismatchPrecedence());
            nestedDescribe(paren, nested.getMismatchDescription(), d, message);
        }
        
    }
}
