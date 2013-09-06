package org.cthul.matchers.diagnose;

import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResultMismatch;
import org.cthul.matchers.diagnose.result.MatchResultSuccess;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hamcrest.*;

/**
 * @see #matches(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description)
 * @see #matches(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description, java.lang.String) 
 */
public class QuickDiagnose {
    // implements the under-the-rug pattern
    
    /**
     * Uses the {@code matcher} to validate {@code item}.
     * If validation fails, an error message is stored in {@code mismatch}.
     * <p/>
     * The code is equivalent to
     * <pre>{@code
     * if (matcher.matches(item)) {
     *     return true;
     * } else {
     *     matcher.describeMismatch(item, mismatch);
     *     return false;
     * }
     * }</pre>
     * but uses optimizations for diagnosing matchers.
     * 
     * @param matcher
     * @param item
     * @param mismatch
     * @return true iif {@code item} was matched
     * @see DiagnosingMatcher
     * @see QuickDiagnosingMatcher
     */
    public static boolean matches(Matcher<?> matcher, Object item, Description mismatch) {
        if (mismatch instanceof Description.NullDescription) {
            return matcher.matches(item);
        }
        if (matcher instanceof QuickDiagnosingMatcher) {
            return ((QuickDiagnosingMatcher<?>) matcher).matches(item, mismatch);
        } else if (matcher instanceof DiagnosingMatcher) {
            return DiagnosingHack.matches(matcher, item, mismatch);
        } else {
            return simpleMatch(matcher, item, mismatch);
        }
    }

    /**
     * Similar to {@link #matches(org.hamcrest.Matcher, java.lang.Object, org.hamcrest.Description)},
     * but allows to override the mismatch message.
     * <p/>
     * If matching fails, {@code message} will be appended to {@code mismatch}.
     * Any occurrence of {@code "$1"} in (@code message} will be replaced with
     * the actual mismatch description of {@code matcher}.
     * 
     * @param matcher
     * @param item
     * @param mismatch
     * @param message
     * @return match result
     */
    public static boolean matches(Matcher<?> matcher, Object item, Description mismatch, String message) {
        if (mismatch instanceof Description.NullDescription) {
            return matcher.matches(item);
        } else if (message == null || message.equals("$1")) {
            return matches(matcher, item, mismatch);
        }
        
        if (message.contains("$1")) {
            final Description subMismatch = new StringDescription();
            if (!matches(matcher, item, subMismatch)) {
                mismatch.appendText(message.replace("$1", subMismatch.toString()));
                return false;
            }
        } else {
            if (!matcher.matches(item)) {
                mismatch.appendText(message);
                return false;
            }
        }
        return true;
    }
    
    public static <T> MatchResult<T> matchResult(Matcher<?> matcher, T item) {
        if (matcher instanceof QuickDiagnosingMatcher) {
            return ((QuickDiagnosingMatcher) matcher).matchResult(item);
        }
        StringDescription mismatch = new StringDescription();
        if (matches(matcher, item, mismatch)) {
            return new MatchResultSuccess<>(item, matcher);
        } else {
            return new MatchResultMismatch<>(item, matcher, mismatch.toString());
        }
    }
    
    private static boolean simpleMatch(Matcher<?> matcher, Object item, Description mismatch) {
        if (matcher.matches(item)) {
            return true;
        } else {
            matcher.describeMismatch(item, mismatch);
            return false;
        }
    }
    
    private static boolean enableDiagnosingHack = true;
    
    /**
     * Disables the optimization hack for {@link DiagnosingMatcher},
     * which calls {@link DiagnosingMatcher#matches(java.lang.Object, org.hamcrest.Description) }
     * directly.
     * <p/>
     * This method has to be invoked before the hack is executed the first time.
     * @throws IllegalStateException if the hack was already activated.
     */
    public static synchronized void disableDiagnosingHack() {
        enableDiagnosingHack = false;
        if (DiagnosingHack.diagnosingMatches != null) {
            throw new IllegalStateException("Diagnosing hack already activated");
        }
    }
    
    private static synchronized boolean diagnosingHackEnabled() {
        return enableDiagnosingHack;
    }
    
    public static synchronized boolean diagnosingHackActivated() {
        return DiagnosingHack.hackEnabled;
    }
    
    /**
     * Wrapper class for the diagnosing hack.
     * Is initialized lazy when needed.
     */
    private static class DiagnosingHack {
        // the rug under the rug
    
        private static final Method diagnosingMatches;
        private static final boolean hackEnabled;

        static {
            Method matches = null;
            boolean success = false;
            if (diagnosingHackEnabled()) {
                try {
                    matches = DiagnosingMatcher.class.getMethod("matches", Object.class, Description.class);
                    matches.setAccessible(true);
                    success = true;
                } catch (NoSuchMethodException | SecurityException e) { 
                    success = false;
                }
            }
            hackEnabled = success;
            diagnosingMatches = matches;
        }
        
        private static boolean matches(Matcher<?> matcher, Object item, Description mismatch) {
            if (diagnosingMatches == null) {
                return simpleMatch(matcher, item, mismatch);
            } else {
                try {
                    return invokeDiagnosingMatches(matcher, item, mismatch);
                } catch (IllegalAccessException e) {
                    if (enableDiagnosingHack) {
                        // try to activate hack again
                        try {
                            diagnosingMatches.setAccessible(true);
                            invokeDiagnosingMatches(matcher, item, mismatch);
                        } catch (SecurityException | IllegalAccessException e2) {
                            // TODO: warn that hack is broken now
                            enableDiagnosingHack = false;
                        }
                    }
                    return simpleMatch(matcher, item, mismatch);
                }
            }
        }

        private static boolean invokeDiagnosingMatches(Matcher<?> matcher, Object item, Description mismatch) throws IllegalAccessException {
            try {
                return (boolean) diagnosingMatches.invoke(matcher, item, mismatch);
            } catch (InvocationTargetException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    throw new RuntimeException(cause);
                }
            }
        }
    
    }
    
}
