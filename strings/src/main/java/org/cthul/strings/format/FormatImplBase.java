package org.cthul.strings.format;

import java.util.Arrays;
import java.util.FormatFlagsConversionMismatchException;

/**
 * Provides some utility methods for implementing a {@link FormatConversion} or
 * {@link ConversionPattern}.
 * @author Arian Treffer
 */
public abstract class FormatImplBase {
    
    protected static final char[] NO_FLAGS = new char[0];
    
    protected static final String F_JUSTIFICATION = "-|";
    protected static final String F_PADDING = "_0";
    private static final char[] F_JUST = flags(F_JUSTIFICATION);
    
    /**
     * Converts strings of flags into a sorted flag array,
     * that can be passed as a parameter to other utility methods.
     * @param flagStrings
     * @return flag array
     */
    protected static char[] flags(final CharSequence... flagStrings) {
        final StringBuilder sb = new StringBuilder();
        for (CharSequence flags: flagStrings) sb.append(flags);
        return flags(sb.toString());
    }
    
    /**
     * Converts a string of flags into a sorted flag array,
     * that can be passed as a parameter to other utility methods.
     * @param flagString
     * @return flag array
     */
    protected static char[] flags(final String flagString) {
        if (flagString == null || flagString.isEmpty()) {
            return NO_FLAGS;
        }
        noDupsExcept(flagString, NO_FLAGS);
        final char[] flags = flagString.toCharArray();
        Arrays.sort(flags);
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
     * @param flags
     * @param expected a {@link #flags(java.lang.String) flags} array
     * @throws FormatException if invalid flags are found
     */
    protected void ensureValidFlags(final String flags, 
                                    final char[] expected) {
        if (flags == null) return;
        StringBuilder unexpected = null;
        for (int i = 0; i < flags.length(); i++) {
            char c = flags.charAt(i);
            if (Arrays.binarySearch(expected, c) < 0) {
                if (unexpected == null) unexpected = new StringBuilder();
                unexpected.append(c);
            }
        }
        if (unexpected != null) {
            throw FormatException.formatFlagsMismatch(
                    unexpected.toString(), getFormatName());
        }
    }
    
    protected void ensureNoDuplicates(final String flags) {
        ensureNoDuplicatesExcept(flags, NO_FLAGS);
    }
    
    protected void ensureNoDuplicatesExcept(final String flags, final char[] dups) {
        noDupsExcept(flags, dups);
    }
    
    private static void noDupsExcept(final String flags, final char[] dups) {
        if (flags == null) return;
        StringBuilder duplicates = null;
        for (int i = 0; i < flags.length(); i++) {
            char c = flags.charAt(i);
            if (oneOf(c, dups)) continue;
            boolean dup = false;
            for (int j = i+1; j < flags.length(); j++) {
                if (flags.charAt(j) == c) {
                    dup = true;
                    break;
                }
            }
            if (dup) {
                if (duplicates == null) duplicates = new StringBuilder();
                if (duplicates.indexOf(flags.substring(i, i+1)) < 0) {
                    duplicates.append(c);
                }
            }
        }
        if (duplicates != null) {
            throw FormatException.duplicateFlags(duplicates.toString());
        }        
    }
    
    /**
     * Ensures that {@code actual} contains no {@code invalid} flags.
     * @param flags
     * @param invalid a {@link #flags(java.lang.String) flags} array
     * @throws FormatException if invalid flags are found
     */
    protected void ensureNoInvalidFlags(final String flags, 
                                        final char[] invalid) {
        if (flags == null) return;
        StringBuilder unexpected = null;
        for (int i = 0; i < flags.length(); i++) {
            char c = flags.charAt(i);
            if (oneOf(c, invalid)) {
                if (unexpected == null) unexpected = new StringBuilder();
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
            if (oneOf(f, choice)) {
                if (found != 0) {
                    if (conflict == null) conflict = new StringBuilder().append(found);
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
    
    protected static boolean oneOf(char c, char[] flags) {
        return Arrays.binarySearch(flags, c) >= 0;
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
        None, Left, Right, Center;
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
