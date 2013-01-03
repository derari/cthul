package org.cthul.strings;

import org.cthul.strings.format.FormatArgs;
import org.cthul.strings.format.FormatterConfiguration;

/**
 *
 * @author Arian Treffer
 */
public class Strings {
    
    /**
     * @see Formatter#format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, java.lang.Object, int, int, java.lang.Object[]) 
     */
    public static String format(Object formatString, Object... args) {
        return format((FormatterConfiguration) null, formatString, args);
    }
    
    /**
     * @see Formatter#format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, java.lang.Object, int, int, java.lang.Object[]) 
     */
    public static String format(Throwable t, Object formatString, Object... args) {
        return format(null, t, formatString, args);
    }

    /**
     * @see Formatter#format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, java.lang.Object, int, int, java.lang.Object[]) 
     */
    public static String format(FormatterConfiguration c, Object formatString, Object... args) {
        return format(c, null, formatString, args);
    }
    
    /**
     * @see Formatter#format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, java.lang.Object, int, int, java.lang.Object[]) 
     */
    public static String format(FormatterConfiguration c, Throwable t, Object formatString, Object... args) {
        return new Formatter(c).format(t, formatString, args).toString();
    }
    
    /**
     * @see Formatter#format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, int, int, org.cthul.strings.format.FormatArgs) 
     */
    public static String format(Object formatString, FormatArgs args) {
        return format(null, formatString, args);
    }
    
    /**
     * @see Formatter#format(org.cthul.strings.format.FormatterConfiguration, java.util.Locale, java.lang.Object, int, int, org.cthul.strings.format.FormatArgs) 
     */
    public static String format(FormatterConfiguration c, Object formatString, FormatArgs args) {
        return new Formatter(c).format(formatString, args).toString();
    }

    /**
     * Appends {@code tokens} to {@code target}, separated by {@code sep}.
     * @param tokens
     * @param sb
     */
    public static StringBuilder join(final StringBuilder sb, final String sep, final String... tokens) {
        boolean first = true;
        for (Object t: tokens) {
            if (!first) sb.append(sep);
            else first = false;
            sb.append(t);
        }
        return sb;
    }
    
    public static String join(final String sep, final String... tokens) {
        return join(new StringBuilder(), sep, tokens).toString();
    }
    
    /**
     * Appends {@code tokens} to {@code target}, separated by {@code sep}.
     * @param tokens
     * @param sb
     */
    public static StringBuilder join(final StringBuilder sb, final String sep, final Iterable<?> tokens) {
        boolean first = true;
        for (Object t: tokens) {
            if (!first) sb.append(t);
            else first = false;
            sb.append(t);
        }
        return sb;
    }
    
    public static String join(final String sep, final Iterable<?> tokens) {
        return join(new StringBuilder(), sep, tokens).toString();
    }
    
    public static String[] toStrings(final Object... values) {
        final String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++)
            result[i] = String.valueOf(values[i]);
        return result;
    }

}
