package org.cthul.monad.util;

public class SafeStrings {
    
    public static String format(String format, Object[] args) {
        try {
            return String.format(format, args);
        } catch (RuntimeException ex) {
            return safeConcatenate(format, args);
        }
    }
    
    public static String safeConcatenate(String string, Object[]... args) {
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
