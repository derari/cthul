package org.cthul.log.format;

/**
 *
 * @author Arian Treffer
 */
public class FormatException extends RuntimeException {
    
    public static FormatException unexpectedFlag(char flag, String expected, String conversion) {
        if (expected == null || expected.isEmpty()) {
            return new FormatException("Unexpected flag '" + flag + "', " +
                    conversion + " supports no flags");
        } else {
            return new FormatException("Unexpected flag '" + flag + "', " +
                    conversion + " expects [" + expected + "]");
        }
    }

    public static FormatException unsupportedWidth(String conversion) {
        return new FormatException(conversion + " does not support width");
    }
    
    public static FormatException unsupportedPrecision(String conversion) {
        return new FormatException(conversion + " does not support precision");
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
