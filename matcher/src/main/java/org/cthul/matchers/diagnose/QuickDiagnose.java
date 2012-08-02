package org.cthul.matchers.diagnose;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hamcrest.Description;
import org.hamcrest.DiagnosingMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
//import static org.cthul.matchers.

/**
 *
 * @author Arian Treffer
 */
public class QuickDiagnose {
    
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
     * @return 
     */
    public static boolean matches(Matcher<?> matcher, Object item, Description mismatch, String message) {
        if (mismatch instanceof Description.NullDescription) {
            return matcher.matches(item);
        } else if (message == null || message.equals("$1")) {
            return matches(matcher, item, mismatch);
        }
        
        final boolean matched;
        
        if (message.contains("$1")) {
            final Description subMismatch = new StringDescription();
            matched = matches(matcher, item, subMismatch);
            if (!matched) {
                mismatch.appendText(message.replace("$1", subMismatch.toString()));
            }
        } else {
            matched = matcher.matches(item);
            if (!matched) {
                mismatch.appendText(message);
            }
        }
        return matched;
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
    public static void disableDiagnosingHack() {
        enableDiagnosingHack = false;
        if (DiagnosingHack.diagnosingMatches != null) {
            throw new IllegalStateException("Diagnosing hack already activated");
        }
    }
    
    private static boolean diagnosingHackEnabled() {
        return enableDiagnosingHack;
    }
    
    /**
     * Wrapper class for the diagnosing hack.
     * Is initialized lazy when needed.
     */
    private static class DiagnosingHack {
    
        private static final Method diagnosingMatches;

        static {
            Method matches = null;
            if (diagnosingHackEnabled()) {
                try {
                    matches = DiagnosingMatcher.class.getMethod("matches", Object.class, Description.class);
                    matches.setAccessible(true);
                } catch (NoSuchMethodException | SecurityException e) { }
            }
            diagnosingMatches = matches;
        }
        
        private static boolean matches(Matcher<?> matcher, Object item, Description mismatch) {
            if (diagnosingMatches == null) {
                return simpleMatch(matcher, item, mismatch);
            } else {
                try {
                    return invokeDiagnosingMatches(matcher, item, mismatch);
                } catch (IllegalAccessException ex) {
                    if (enableDiagnosingHack) {
                        // try to activate hack again
                        try {
                            diagnosingMatches.setAccessible(true);
                            invokeDiagnosingMatches(matcher, item, mismatch);
                        } catch (SecurityException | IllegalAccessException e) {
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
