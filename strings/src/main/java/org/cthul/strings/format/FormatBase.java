package org.cthul.strings.format;

import java.io.IOException;
import java.util.Arrays;

/**
 * Provides some utility methods for implementing a {@link Format}
 * @author Arian Treffer
 */
public abstract class FormatBase implements Format {
    
    protected static final char[] NO_FLAGS = new char[0];

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
        return flags;
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
        for (int i = 0; i < actual.length(); i++) {
            char c = actual.charAt(i);
            if (Arrays.binarySearch(expected, c) < 0) {
                throw FormatException.unexpectedFlag(
                        c, new String(expected), getConversionName());
            }
        }
    }
    
    /**
     * Ensures no flags are given.
     * @param flags 
     * @throws FormatException if flags are given.
     */
    protected void ensureNoFlags(final String flags) {
        if (flags == null || flags.isEmpty()) return;
        throw FormatException.unexpectedFlag(
                flags.charAt(0), null, getConversionName());
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
     * @throws FormatException if more than one expected flag was found.
     */
    protected char selectFlag(final String flags, final char[] choice) {
        char found = '\0';
        if (flags != null) {
            for (int i = 0; i < flags.length(); i++) {
                char f = flags.charAt(i);
                if (Arrays.binarySearch(choice, f) >= 0) {
                    if (found != '\0') {
                        throw FormatException.conflictingFlags(f, found, new String(choice), getConversionName());
                    } else {
                        found = f;
                    }
                }
            }
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
        throw FormatException.unsupportedWidth(getConversionName());
    }
    
    /**
     * Ensures no precision is given
     * @param precision specified value
     * @throws FormatException if precision is given
     */
    protected void ensureNoPrecision(int precision) {
        if (precision < 0) return;
        throw FormatException.unsupportedPrecision(getConversionName());
    }
    
    /**
     * Appends {@code csq} to {@code a}, left-justified.
     * @param a
     * @param csq
     * @param pad padding character padding character
     * @param width minimum of characters that will be written
     * @throws IOException 
     */
    protected static void justifyLeft(Appendable a, CharSequence csq, char pad, int width) throws IOException {
        final int padLen = width - csq.length();
        a.append(csq);
        for (int i = 0; i < padLen; i++) a.append(pad);
     }
    
    /**
     * Left-justifies {@code csq}.
     * @param csq
     * @param pad padding character
     * @param width minimum of characters that will be written minimum of characters that will be written
     * @return justified string
     */
    protected static String justifyLeft(CharSequence csq, char pad, int width) {
        try {
            StringBuilder sb = new StringBuilder(width);
            justifyLeft(sb, csq, pad, width);
            return sb.toString();
        } catch (IOException e) {
            throw new AssertionError("StringBuilder failed", e);
        }
    }
    
    /**
     * Appends {@code csq} to {@code a}, right-justified.
     * @param a
     * @param csq
     * @param pad padding character
     * @param width minimum of characters that will be written
     * @throws IOException 
     */
    protected static void justifyRight(Appendable a, CharSequence csq, char pad, int width) throws IOException {
        final int padLen = width - csq.length();
        for (int i = 0; i < padLen; i++) a.append(pad);
        a.append(csq);
    }
    
    /**
     * Right-justifies {@code csq}.
     * @param csq
     * @param pad padding character
     * @param width minimum of characters that will be written
     * @return justified string
     */
    protected static String justifyRight(CharSequence csq, char pad, int width) {
        try {
            StringBuilder sb = new StringBuilder(width);
            justifyRight(sb, csq, pad, width);
            return sb.toString();
        } catch (IOException e) {
            throw new AssertionError("StringBuilder failed", e);
        }
    }
    
    /**
     * Appends {@code csq} to {@code a}, centered.
     * @param a
     * @param csq
     * @param pad padding character
     * @param width minimum of characters that will be written
     * @throws IOException 
     */
    protected static void justifyCenter(Appendable a, CharSequence csq, char pad, int width) throws IOException {
        final int padLen = width - csq.length();
        for (int i = 0; i < padLen/2; i++) a.append(pad);
        a.append(csq);
        for (int i = 0; i < (padLen+1)/2; i++) a.append(pad);
    }
    
    /**
     * Centers {@code csq}.
     * @param csq
     * @param pad padding character
     * @param width minimum of characters that will be written
     * @return justified string
     */
    protected static String justifyCenter(CharSequence csq, char pad, int width) {
        try {
            StringBuilder sb = new StringBuilder(width);
            justifyCenter(sb, csq, pad, width);
            return sb.toString();
        } catch (IOException e) {
            throw new AssertionError("StringBuilder failed", e);
        }
    }
    
    /**
     * Returns the name of this conversion.
     * @return name of this conversion
     */
    protected String getConversionName() {
        return getClass().getName();
    }
            
}
