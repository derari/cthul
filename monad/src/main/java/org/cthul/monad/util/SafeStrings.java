package org.cthul.monad.util;

import java.util.Arrays;

public class SafeStrings {

    public static String toString(Object value) {
        try {
            return String.valueOf(value);
        } catch (RuntimeException ex) {
            return value.getClass().getCanonicalName() + ": " + ex.getMessage();
        }
    }

    public static String format(String format, Object... args) {
        if (args == null || args.length == 0) return format;
        if (format == null) return Arrays.toString(args);
        try {
            return String.format(format, args);
        } catch (RuntimeException ex) {
            return safeConcatenate(format, args);
        }
    }

    public static String safeConcatenate(String string, Object... args) {
        StringBuilder sb = new StringBuilder(string);
        sb.append(" % [");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(",");
            try {
                sb.append(args[i]);
            } catch (RuntimeException ex2) {
                sb.append(ex2);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
