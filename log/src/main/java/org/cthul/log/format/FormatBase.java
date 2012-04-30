package org.cthul.log.format;

import java.util.Arrays;

/**
 *
 * @author Arian Treffer
 */
public abstract class FormatBase implements Format {
    
    protected static final char[] NO_FLAGS = new char[0];

    protected static char[] flags(String flagString) {
        if (flagString == null || flagString.isEmpty()) {
            return NO_FLAGS;
        }
        final char[] flags = flagString.toCharArray();
        Arrays.sort(flags);
        return flags;
    }
    
    protected void ensureValidFlags(final String actualFlags, 
                                    final char[] expectedFlags) {
        if (actualFlags == null) return;
        for (int i = 0; i < actualFlags.length(); i++) {
            char c = actualFlags.charAt(i);
            if (Arrays.binarySearch(expectedFlags, c) < 0) {
                throw FormatException.unexpectedFlag(
                        c, new String(expectedFlags), getConversionName());
            }
        }
    }
    
    protected void ensureNoFlags(final String flags) {
        if (flags == null || flags.isEmpty()) return;
        throw FormatException.unexpectedFlag(
                flags.charAt(0), null, getConversionName());
    }
    
    protected boolean containsFlag(final char flag, final String flags) {
        if (flags == null) return false;
        return flags.indexOf(flag) >= 0;
    }
    
    protected void ensureNoWidth(int width) {
        if (width < 0) return;
        throw FormatException.unsupportedWidth(getConversionName());
    }
    
    protected void ensureNoPrecision(int precision) {
        if (precision < 0) return;
        throw FormatException.unsupportedPrecision(getConversionName());
    }
    
    protected String getConversionName() {
        return getClass().getName();
    }
            
}
