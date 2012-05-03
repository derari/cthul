package org.cthul.strings.format;

/**
 *
 * @author Arian Treffer
 */
public class FormatException extends RuntimeException {
    
    public static FormatException unexpectedFlag(char flag, String expected, String conversion) {
        if (expected == null || expected.isEmpty()) {
            return new FormatException("Unexpected flag '" + flag + "', " +
                    conversion + " does not support flags.");
        } else {
            return new FormatException("Unexpected flag '" + flag + "', " +
                    conversion + " expects [" + expected + "].");
        }
    }

    public static FormatException unsupportedWidth(String conversion) {
        return new FormatException(conversion + " does not support width.");
    }
    
    public static FormatException unsupportedPrecision(String conversion) {
        return new FormatException(conversion + " does not support precision.");
    }
    
    public static FormatException precisionTooHigh(String conversion, int value, int max) {
        return new FormatException(conversion + " expects maximum precision " + 
                        max + ", but was " + value + ".");
    }

    public static FormatException conflictingFlags(char f1, char f2, String expected, String conversion) {
        return new FormatException("Conflicting flags '" + f1 + "' and '" + 
                f2 + "', " + conversion + " exepects only one of [" + expected + "].");
    }

    /**
     * Creates a new instance of <code>FormatException</code> without detail message.
     */
    public FormatException() {
    }

    /**
     * Constructs an instance of <code>FormatException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public FormatException(String msg) {
        super(msg);
    }

    public FormatException(Throwable cause) {
        super(cause);
    }

    public FormatException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
