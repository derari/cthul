package org.cthul.strings.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements a template method that parses format strings.
 * Subclasses define how the formats are interpreted.
 * 
 * @author Arian Treffer
 */
public abstract class FormatStringParser<E extends Exception> {

    protected int autoIndex = 1;
    protected int lastIndex = 0;
    protected Object lastArg = null;

    public FormatStringParser() {
    }

    protected abstract void appendText(CharSequence csq, int start, int end) throws E;

    protected abstract void appendPercent() throws E;

    protected abstract void appendNewLine() throws E;
    
    protected abstract int customShortFormat(char formatId, Object arg, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws E;

    protected abstract int customLongFormat(String formatId, Object arg, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws E;

    protected abstract void standardFormat(String formatId, Object arg, String flags, int width, int precision) throws E;

    protected int appendPercent(Matcher matcher, String fId, CharSequence formatString) throws E {
        appendPercent();
        return matcher.end();
    }

    protected int appendNewLine(Matcher matcher, String fId, CharSequence formatString) throws E {
        appendNewLine();
        return matcher.end();
    }
    
    protected int customShortFormat(Matcher matcher, String fId, CharSequence formatString, boolean uppercase) throws E {
        final char formatId = getShortFormatId(fId);
        final Object arg = getArg(formatString, matcher);
        final String flags = getFlags(matcher);
        final int width = getWidth(matcher);
        final int precision = getPrecision(matcher);
        final int lastPosition = matcher.end();
        return lastPosition + customShortFormat(formatId, arg, flags, width, precision, formatString, lastPosition, uppercase);
    }

    protected int customLongFormat(Matcher matcher, String fId, CharSequence formatString, boolean uppercase) throws E {
        final String formatId = getLongFormatId(fId);
        final Object arg = getArg(formatString, matcher);
        final String flags = getFlags(matcher);
        final int width = getWidth(matcher);
        final int precision = getPrecision(matcher);
        final int lastPosition = matcher.end();
        return lastPosition + customLongFormat(formatId, arg, flags, width, precision, formatString, lastPosition, uppercase);
    }

    protected int standardFormat(Matcher matcher, String fId, CharSequence formatString) throws E {
        final String formatId = getStandardFormatId(fId);
        final Object arg = getArg(formatString, matcher);
        final String flags = getFlags(matcher);
        final int width = getWidth(matcher);
        final int precision = getPrecision(matcher);
        standardFormat(formatId, arg, flags, width, precision);
        return matcher.end();
    }
    
    protected int autoIndex() {
        return lastIndex = autoIndex++;
    }

    protected int prevIndex() {
        return lastIndex;
    }

    protected int nextIndex() {
        return ++lastIndex;
    }

    protected int intIndex(int i) {
        return lastIndex = i;
    }

    protected char charIndex(char c) {
        return c;
    }
    
    protected String stringIndex(String s) {
        return s;
    }
    
    public int parse(CharSequence formatString) throws E {
        return parse(formatString, 0, formatString.length());
    }
    
    public int parse(final CharSequence formatString, 
                            final int start, final int end) throws E {
        final Matcher matcher = matcher(formatString);
        int lastPosition = start;
        while (matcher.find(lastPosition) && matcher.end() <= end) {
            int mStart = matcher.start();
            if (lastPosition < mStart)
                appendText(formatString, lastPosition, mStart);
            lastPosition = applyFormat(matcher, formatString);
        }
        if (lastPosition < end) {
            appendText(formatString, lastPosition, end);
            lastPosition = end;
        }
        return lastPosition;
    }
    
    protected int applyFormat(Matcher matcher, CharSequence formatString) throws E {
        final String fId = getFormatId(matcher);
        final char f0 = fId.charAt(0);

        boolean uppercase = false;
        switch (f0) {
            case '%':
                return appendPercent(matcher, fId, formatString);
            case 'n':
                return appendNewLine(matcher, fId, formatString);
            case CUSTOM_SHORT_UC:
                uppercase = true;
            case CUSTOM_SHORT:
                return customShortFormat(matcher, fId, formatString, uppercase);
            case CUSTOM_LONG_UC:
                uppercase = true;
            case CUSTOM_LONG:
                return customLongFormat(matcher, fId, formatString, uppercase);
            default:
                return standardFormat(matcher, fId, formatString);
        }
    }
    
    protected Matcher matcher(CharSequence formatString) {
        return PATTERN.matcher(formatString);
    }
    
    protected String getFormatId(Matcher matcher) {
        return matcher.group(G_FORMAT_ID);
    }
    
    protected char getShortFormatId(String formatId) {
        return formatId.charAt(1);
    }
    
    protected String getLongFormatId(String formatId) {
        final int semicolon = formatId.endsWith(";") ? 1 : 0;
        return formatId.substring(1, formatId.length() - semicolon);        
    }
    
    protected String getStandardFormatId(String formatId) {
        return formatId;
    }
    
    protected abstract Object getArg(int i);
    
    protected abstract Object getArg(char c);
    
    protected abstract Object getArg(String s);
    
    protected Object getArg(CharSequence csq, Matcher matcher) {
        return lastArg = getArg(csq, matcher, G_ARG_ID);
    }
    
    protected Object getArg(final CharSequence csq, final Matcher matcher, final int gId) {
        int start = matcher.start(gId);
        int end = matcher.end(gId);
        if (start < 0 || start == end) return getArg(autoIndex());
        char c = csq.charAt(start);
        switch (c) {
            case '`':
            case '<':
                if (csq.charAt(start+1) == '<') {
                    return lastArg;
                }
                return getArg(prevIndex());
            case '´':
            case '>':
                return getArg(nextIndex());
            case '$':
            case '.':
                return getArg(autoIndex());
            case '?':
            case ':':
                String s = csq.subSequence(start+1, end-1).toString();
                return getArg(stringIndex(s));
        }
        if (c >= '0' && c <= '9') {
            int i = Integer.parseInt(csq.subSequence(start, end-1).toString());
            return getArg(intIndex(i));
        }
        return getArg(charIndex(c));
    }
    
    protected String getFlags(Matcher matcher) {
        return nullOrNotEmpty(matcher.group(G_FLAGS));
    }
    
    protected int getWidth(Matcher matcher) {
        return parseInt(matcher.group(G_WIDTH));
    }
    
    protected int getPrecision(Matcher matcher) {
        return parseInt(matcher.group(G_PRECISION));
    }
    
    protected int parseInt(String string) {
        if (string == null || string.isEmpty()) {
            return -1;
        } else {
            return Integer.parseInt(string);
        }
    }
    
    protected String nullOrNotEmpty(String string) {
        if (string == null || string.isEmpty()) return null;
        return string;
    }

    public static final char CUSTOM_SHORT = 'i';
    public static final char CUSTOM_LONG = 'j';
    
    public static final char CUSTOM_SHORT_UC = 'I';
    public static final char CUSTOM_LONG_UC = 'J';
    
    protected static final String P_ARG_ID    = "(\\d+\\$|[a-zA-Z]\\$|[?:][^$]*\\$|\\.?\\$|<<\\$?|[<>`´]\\$?)?";
    protected static final String P_FLAGS     = "([^a-zA-Z%]*?[^.1-9a-zA-Z%])?"; // [-#+ 0,(\\<]
    protected static final String P_WIDTH     = "(\\d+)?";
    protected static final String P_PRECISION = "(?:\\.(\\d+))?";
    protected static final String P_FORMAT_ID = "((?:[jJ][_a-zA-Z0-9]+[;]?)|(?:[tTiI]?[a-zA-Z])|%)";
    protected static final Pattern PATTERN;
    
    protected static final int G_ARG_ID    = 1;
    protected static final int G_FLAGS     = 2;
    protected static final int G_WIDTH     = 3;
    protected static final int G_PRECISION = 4;
    protected static final int G_FORMAT_ID = 5;
    
    static {
        PATTERN = Pattern.compile(
            "%" + P_ARG_ID + P_FLAGS + P_WIDTH + P_PRECISION + P_FORMAT_ID);
    }
}
