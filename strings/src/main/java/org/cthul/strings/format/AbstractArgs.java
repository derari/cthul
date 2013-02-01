package org.cthul.strings.format;

/**
 * Utility methods for implementing {@link FormatArgs} or {@link MatchResults}.
 * @author Arian Treffer
 */
public abstract class AbstractArgs {
    
    protected static int cToI(char c) {
        if (c < 'A') throw invalidCIndex(c);
        if (c > 'Z') {
            if (c < 'a' || c > 'z') throw invalidCIndex(c);
            return c - 'a' + 26;
        } else {
            return c - 'A';
        }
    }
    
    protected static IllegalArgumentException invalidCIndex(char c) {
        return new IllegalArgumentException("Expected [A-Za-z], got " + c);
    }
    
    protected static char iToC(int i) {
        if (i < 0 || i > 51) {
            throw new IllegalArgumentException("Expected 0-51, got" + i);
        }
        if (i < 26) {
            return (char)('A' + i);
        } else {
            return (char)('a' + i - 26);
        }
    }
    
}
