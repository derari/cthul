package org.cthul.log.format;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author Arian Treffer
 */
public class Formatter implements Appendable {

    public static void format(Message message, Appendable appendable) {
        new Formatter(message, appendable, Locale.getDefault()).run();
    }
    
    private final Message message;
    private final Appendable appendable;
    private final Locale locale;
    
    private final String formatString;
    
    private int autoIndex = 1;
    private int lastIndex = 0;
    private int ucaseCount = 0;
    private boolean uppercase = false;
    
    private final Object[] tmp = new Object[1];
    private final java.util.Formatter util;

    public Formatter(Message message, Appendable appendable, Locale locale) {
        this.message = message;
        this.appendable = appendable;
        this.locale = locale;
        formatString = message.getMessage();
        util = new java.util.Formatter(appendable, locale);
    }

    public Message getMessage() {
        return message;
    }

    public String getFormatString() {
        return formatString;
    }

    @Override
    public Appendable append(char c) throws IOException {
        if (uppercase) {
            appendable.append(Character.toString(c).toUpperCase(locale));
        } else {
            appendable.append(c);
        }
        return this;
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        if (uppercase) {
            appendable.append(csq.toString().toUpperCase(locale));
        } else {
            appendable.append(csq);
        }
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        csq = csq.subSequence(start, end);
        if (uppercase) {
            appendable.append(csq.toString().toUpperCase(locale));
        } else {
            appendable.append(csq);
        }
        return this;
    }
    
    public void format(final String formatString) throws IOException {
        format(formatString, 0, formatString.length());
    }
    
    public int format(final String formatString, final int start, 
                      final int end) throws IOException {
        final Matcher matcher = PATTERN.matcher(formatString);
        int lastPosition = start;
        while (matcher.find(lastPosition) && matcher.end() <= end) {
            appendable.append(formatString, lastPosition, matcher.start());
            lastPosition = matcher.end();

            final String argId = matcher.group(N_ARG_ID);
            final int index = getArgIndex(argId);

            final String fId = matcher.group(N_FORMAT_ID);
            final char f0 = fId.charAt(0);
            if (f0 == CUSTOM_SHORT || f0 == CUSTOM_SHORT_UC || 
                    f0 == CUSTOM_LONG || f0 == CUSTOM_LONG_UC) {
                int n = customFormat(matcher, index, fId, 
                                     (f0 < 'a'), 
                                     formatString, lastPosition);
                lastPosition += n;
            } else {
                stringFormat(matcher, argId, index);
            }
        }
        if (lastPosition < end) {
            appendable.append(formatString, lastPosition, end);
            lastPosition = end;
        }
        return lastPosition;
    }

    private Format getFormat(final String f) throws FormatException {
        if (f.length() < 2) {
            throw new FormatException("Expected format Id after " + f);
        }
        final Format format;
        switch (f.charAt(0)) {
            case CUSTOM_SHORT:
            case CUSTOM_SHORT_UC:
                final char cfId = f.charAt(1);
                format = message.getConfiguration().shortFormat(cfId);
                break;
            case CUSTOM_LONG:
            case CUSTOM_LONG_UC:
                final int semicolon = f.endsWith(";") ? 1 : 0;
                final String fmtId = f.substring(1, f.length() - semicolon);
                format = message.getConfiguration().longFormat(fmtId);
                break;
            default:
                format = null;
        }
        if (format == null) {
            throw new FormatException("Unknown custom format " + f);
        }
        return format;
    }

    /**
     * Invokes {@link #format(java.lang.String)} with the 
     * {@link #getFormatString() formatString}.
     * Can be published by subclasses.
     */
    protected void run() {
        try {
            format(formatString);
        } catch (IOException e) {
            throw new FormatException(e);
        }
    }
    
    private int getArgIndex(String argId) {
        if (argId == null || argId.isEmpty()) {
            lastIndex = autoIndex++;
        } else {
            switch (argId.charAt(0)) {
                case 'E':
                    lastIndex = 0;
                    break;
                case '<':
                    break;
                case '>':
                    lastIndex++;
                    break;
                case '.':
                    lastIndex = autoIndex++;
                    break;
                default:
                    lastIndex = Integer.parseInt(argId.substring(0, argId.length()-1));
            }
        }
        return lastIndex;
    }
    
    private void stringFormat(Matcher matcher, String argId, int argIndex) throws IOException {
        final String group = matcher.group();
        final String format;
        if (argId == null || argId.isEmpty()) {
            format = group;
        } else {
            final StringBuilder sb = new StringBuilder(group.length());
            sb.append('%');
            sb.append(group, 1 + argId.length(), group.length());
            format = sb.toString();
        }
        tmp[0] = message.getArg(argIndex-1);
        util.format(format, tmp);
    }
    
    private int customFormat(final Matcher matcher, final int index, 
                             final String f, final boolean uppercase,
                             String input, int position) throws IOException {
        final Format format = getFormat(f);
        final Object value = message.getArg(index-1);
        final String flags = getFlags(matcher);
        final int width = getIntGroup(matcher, N_WIDTH);
        final int precision = getIntGroup(matcher, N_PRECISION);
        return execFormat(uppercase, format, value, flags, width, precision, input, position);
    }
    
    private int getIntGroup(Matcher matcher, int groupId) {
        final String group = matcher.group(groupId);
        if (group == null || group.isEmpty()) {
            return -1;
        } else {
            return Integer.parseInt(group);
        }
    }
    
    private String getFlags(Matcher matcher) {
        final String flags = matcher.group(N_FLAGS);
        if (flags.isEmpty()) return null;
        return flags;
    }
    
    private int execFormat(final boolean uppercase, final Format format, 
                           final Object value, final String flags, 
                           final int width, final int precision, String input, 
                           int position) throws IOException {
        if (uppercase) {
            ucaseCount++;
            this.uppercase = true;
        }
        final int result;
        try {
            result = format.format(this, value, flags, width, 
                                   precision, input, position);
        } finally {
            if (uppercase) {
                ucaseCount--;
                this.uppercase = ucaseCount > 0;
            }
        }
        return result;
    }
    
    public static final char CUSTOM_SHORT = 'i';
    public static final char CUSTOM_LONG = 'j';
    
    public static final char CUSTOM_SHORT_UC = 'I';
    public static final char CUSTOM_LONG_UC = 'J';
    
    /* Argument ID
     * 3$ -> third argument
     * E$ -> logged exception
     * .$ -> auto index (default)
     * <$ or < -> last value
     * >$ or > -> next value
     */
    private static final String ARG_ID = "(\\d+\\$|E\\$|\\.\\$|<\\$|<|>\\$|>)?";
    
    /* Flags
     * Special characters
     */
    private static final String FLAGS = "([ !-0:-@\\[-`{-~]+)?"; // [-#+ 0,(\\<]
    private static final String WIDTH_PRECISION = "(\\d+)?(\\.\\d+)?";
    private static final String FORMAT_ID = "(([jJ][_a-zA-Z0-9]+[;]?)|([tTiI]?[a-zA-Z%]))";
    private static final Pattern PATTERN;
    
    private static final int N_ARG_ID    = 1;
    private static final int N_FLAGS     = 2;
    private static final int N_WIDTH     = 3;
    private static final int N_PRECISION = 4;
    private static final int N_FORMAT_ID = 5;
    
    static {
        try {
            PATTERN = Pattern.compile(
                      "%" + ARG_ID + FLAGS + WIDTH_PRECISION + FORMAT_ID);
        } catch (PatternSyntaxException e) {
            System.err.println(e);
            throw e;
        }
    }
    
}
