package org.cthul.strings.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Arian Treffer
 */
public abstract class FormatStringParser<E extends Exception> {

    protected int autoIndex = 1;
    protected int lastIndex = 0;

    public FormatStringParser() {
    }

    protected abstract void appendText(CharSequence csq, int start, int end) throws E;

    protected abstract void appendPercent() throws E;

    protected abstract void appendNewLine() throws E;
    
    protected abstract int customShortFormat(char formatId, int argId, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws E;

    protected abstract int customLongFormat(String formatId, int argId, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws E;

    protected abstract void standardFormat(String formatId, int argId, String flags, int width, int precision) throws E;

    protected int appendPercent(Matcher matcher, String fId, CharSequence formatString, int lastPosition) throws E {
        appendPercent();
        return 0;
    }

    protected int appendNewLine(Matcher matcher, String fId, CharSequence formatString, int lastPosition) throws E {
        appendNewLine();
        return 0;
    }
    
    protected int customShortFormat(Matcher matcher, String fId, CharSequence formatString, int lastPosition, boolean uppercase) throws E {
        final char formatId = getShortFormatId(fId);
        final int argId = getArgIndex(matcher);
        final String flags = getFlags(matcher);
        final int width = getWidth(matcher);
        final int precision = getPrecision(matcher);
        return customShortFormat(formatId, argId, flags, width, precision, formatString, lastPosition, uppercase);
    }

    protected int customLongFormat(Matcher matcher, String fId, CharSequence formatString, int lastPosition, boolean uppercase) throws E {
        final String formatId = getLongFormatId(fId);
        final int argId = getArgIndex(matcher);
        final String flags = getFlags(matcher);
        final int width = getWidth(matcher);
        final int precision = getPrecision(matcher);
        return customLongFormat(formatId, argId, flags, width, precision, formatString, lastPosition, uppercase);
    }

    protected int standardFormat(Matcher matcher, String fId, CharSequence formatString, int lastPosition) throws E {
        final String formatId = getStandardFormatId(fId);
        final int argId = getArgIndex(matcher);
        final String flags = getFlags(matcher);
        final int width = getWidth(matcher);
        final int precision = getPrecision(matcher);
        standardFormat(formatId, argId, flags, width, precision);
        return 0;
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

    protected int charIndex(char c) {
        return lastIndex = parseCharIndex(c);
    }
    
    protected int parseCharIndex(char c) {
        throw new UnsupportedOperationException("character index not supported");
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
            lastPosition = matcher.end();
            lastPosition += applyFormat(matcher, formatString, lastPosition);
        }
        if (lastPosition < end) {
            appendText(formatString, lastPosition, end);
            lastPosition = end;
        }
        return lastPosition;
    }
    
    protected int applyFormat(Matcher matcher, CharSequence formatString, int lastPosition) throws E {
        final String fId = getFormatId(matcher);
        final char f0 = fId.charAt(0);

        boolean uppercase = false;
        switch (f0) {
            case '%':
                return appendPercent(matcher, fId, formatString, lastPosition);
            case 'n':
                return appendNewLine(matcher, fId, formatString, lastPosition);
            case CUSTOM_SHORT_UC:
                uppercase = true;
            case CUSTOM_SHORT:
                return customShortFormat(matcher, fId, formatString, lastPosition, uppercase);
            case CUSTOM_LONG_UC:
                uppercase = true;
            case CUSTOM_LONG:
                return customLongFormat(matcher, fId, formatString, lastPosition, uppercase);
            default:
                return standardFormat(matcher, fId, formatString, lastPosition);
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
    
    protected int getArgIndex(Matcher matcher) {
        return parseArgIndex(matcher.group(G_ARG_ID));
    }
    
    protected int parseArgIndex(String argId) {
        if (argId == null || argId.isEmpty()) {
            return autoIndex();
        }
        char c = argId.charAt(0);
        switch (c) {
            case '`':
            case '<':
                return prevIndex();
            case '´':
            case '>':
                return nextIndex();
            case '$':
            case '.':
                return autoIndex();
        }
        if (c >= '0' && c <= '9') {
            int i = Integer.parseInt(argId.substring(0, argId.length()-1));
            return intIndex(i);
        }
        return charIndex(c);
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
    
    protected static final String P_ARG_ID    = "(\\d+\\$|[a-zA-Z]\\$|\\.?\\$|[<>`´]\\$?)?";
    protected static final String P_FLAGS     = "([^a-zA-Z%]*[^.1-9a-zA-Z%])?"; // [-#+ 0,(\\<]
    protected static final String P_WIDTH     = "(\\d+)?";
    protected static final String P_PRECISION = "(\\.\\d+)?";
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
