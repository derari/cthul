package org.cthul.strings.format;

import java.io.IOException;
import java.util.Arrays;
import java.util.FormatFlagsConversionMismatchException;

/**
 * Provides some utility methods for implementing a {@link FormatConversion} or
 * {@link FormatPattern}.
 * @author Arian Treffer
 */
public abstract class FormatImplBase {
    
    protected static final char[] NO_FLAGS = new char[0];
    
    protected static final String F_JUSTIFICATION = "-|";
    protected static final String F_PADDING = "_0";
    private static final char[] F_JUST = flags(F_JUSTIFICATION);
    
    /**
     * Converts a string of flags into a sorted flag array,
     * that can be passed as a parameter to other utility methods.
     * @param flagString
     * @return flag array
     */
    protected static char[] flags(String flagString) {
        if (flagString == null || flagString.isEmpty()) {
            return NO_FLAGS;
        }
        final char[] flags = flagString.toCharArray();
        Arrays.sort(flags);
        for (int i = 0; i < flags.length-1; i++) {
            if (flags[i] == flags[i+1]) {
                throw FormatException.duplicateFormatFlags(flagString);
            }
        }
        return flags;
    }
    
    protected <T> T cast(Object arg, Class<T> type) {
        if (arg == null) {
            throw new NullPointerException("Argument is null");
        }
        if (!type.isInstance(arg)) {
            throw FormatException.illegalFormat(getFormatName(), arg.getClass());
        }
        return (T) arg;
    }
    
    /**
     * Ensures that {@code actual} only contains {@code expected} flags.
     * @param actual
     * @param expected a {@link #flags(java.lang.String) flags} array
     * @throws FormatException if invalid flags are found
     */
    protected void ensureValidFlags(final String actual, 
                                    final char[] expected) {
        if (actual == null) return;
        StringBuilder unexpected = null;
        for (int i = 0; i < actual.length(); i++) {
            char c = actual.charAt(i);
            if (Arrays.binarySearch(expected, c) < 0) {
                if (unexpected == null) unexpected = new StringBuilder(actual.length());
                unexpected.append(c);
            }
        }
        if (unexpected != null) {
            throw FormatException.formatFlagsMismatch(
                    unexpected.toString(), getFormatName());
        }
    }
    
    /**
     * Ensures that {@code actual} contains no {@code invalid} flags.
     * @param actual
     * @param invalid a {@link #flags(java.lang.String) flags} array
     * @throws FormatException if invalid flags are found
     */
    protected void ensureNoInvalidFlags(final String actual, 
                                        final char[] invalid) {
        if (actual == null) return;
        StringBuilder unexpected = null;
        for (int i = 0; i < actual.length(); i++) {
            char c = actual.charAt(i);
            if (Arrays.binarySearch(invalid, c) >= 0) {
                if (unexpected == null) unexpected = new StringBuilder(actual.length());
                unexpected.append(c);
            }
        }
        if (unexpected != null) {
            throw FormatException.formatFlagsMismatch(
                    unexpected.toString(), getFormatName());
        }
    }
    
    /**
     * Ensures no flags are given.
     * @param flags 
     * @throws FormatException if flags are given.
     */
    protected void ensureNoFlags(final String flags) {
        if (flags == null || flags.isEmpty()) return;
        throw FormatException.formatFlagsMismatch(flags, getFormatName());
    }
    
    /**
     * Returns whether {@code flags} contains {@code flag}.
     * @param flag
     * @param flags
     * @return whether {@code flags} contains {@code flag}.
     */
    protected boolean containsFlag(final char flag, final String flags) {
        if (flags == null) return false;
        return flags.indexOf(flag) >= 0;
    }
    
    /**
     * Selects exactly one of the expected flags.
     * @param choice a {@link #flags(java.lang.String) flags} array
     * @param flags
     * @return one of {@code choice}, or {@code '\0'}
     * @throws FormatFlagsConversionMismatchException if more than one expected flag was found.
     */
    protected char selectFlag(final String flags, final char[] choice) {
        if (flags == null) return 0;
        StringBuilder conflict = null;
        char found = 0;
        for (int i = 0; i < flags.length(); i++) {
            char f = flags.charAt(i);
            if (Arrays.binarySearch(choice, f) >= 0) {
                if (found != 0) {
                    if (conflict == null) conflict = new StringBuilder(choice.length).append(found);
                    conflict.append(f);
                } else {
                    found = f;
                }
            }
        }
        if (conflict != null) {
            throw FormatException.illegalFlags(
                conflict.toString(), getFormatName());
        }
        return found;
    }
    
    /**
     * Ensures no width is given
     * @param width specified value
     * @throws FormatException if width is given
     */
    protected void ensureNoWidth(int width) {
        if (width < 0) return;
        throw FormatException.illegalWidth(getFormatName(), width);
    }
    
    protected void ensureWidthBetween(int actual, int min, int max) {
        if (min <= actual && actual <= max) return;
        throw FormatException.illegalWidth(getFormatName(), actual, min, max);
    }
    
    /**
     * Ensures no precision is given
     * @param precision specified value
     * @throws FormatException if precision is given
     */
    protected void ensureNoPrecision(int precision) {
        if (precision < 0) return;
        throw FormatException.illegalPrecision(getFormatName(), precision);
    }
    
    protected void ensurePrecisionBetween(int actual, int min, int max) {
        if (min <= actual && actual <= max) return;
        throw FormatException.illegalPrecision(getFormatName(), actual, min, max);
    }
    
    protected static enum Justification {
        Left, Right, Center, None;
    }
    
    protected Justification getJustification(String flags, int width) {
        if (width < 0) {
            ensureNoInvalidFlags(flags, F_JUST);
            return Justification.None;
        }
        return getJustification(flags);
    }
    
    protected Justification getJustification(String flags) {
        char j = selectFlag(flags, F_JUST);
        switch (j) {
            case '-':
                return Justification.Left;
            case '|':
                return Justification.Center;
            default:
                return Justification.Right;
        }
    }
    
    /**
     * Returns the name of this conversion.
     * @return name of this conversion
     */
    protected String getFormatName() {
        return getClass().getName();
    }

    @Override
    public String toString() {
        return getFormatName();
    }
            
}
