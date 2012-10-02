package org.cthul.strings;

import java.util.regex.Pattern;

/**
 *
 * @author Arian Treffer
 */
public class RegEx {

    /**
     * Like {@link Pattern#quote(java.lang.String)}, but avoids unnecessary
     * quotes to make result more human-readable.
     * @param string
     * @return 
     */
    public static String quote(final String string) {
        int last = 0;
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (needsQuoting(string.charAt(i))) {
                sb.append(string, last, i);
                last = i = appendQuoted(sb, string, i);
            }
        }
        sb.append(string, last, string.length());
        return sb.toString();
    }
    
    private static final boolean[] QUOTED = new boolean[256];
    static {
        char[] Q_CHARS = ".*+?()[]{}^$\\|".toCharArray();
        for (char q: Q_CHARS) QUOTED[q] = true;
    }
    
    private static boolean needsQuoting(char c) {
        if (c < 256 && QUOTED[c]) return true;
        return needsBlockQuoting(c);
    }
    
    private static boolean needsBlockQuoting(char c) {
        return Character.isWhitespace(c);
    }
    
    private static final int MAX_UNQUOTE_LEN = 6;
    private static final int MAX_SINGLE_QUOTE = 3;
    private static final int MAX_SINGLE_LOOKAHEAD = 2;

    private static int appendQuoted(final StringBuilder sb, final String string, final int start) {
        boolean blockQuote = needsBlockQuoting(string.charAt(start));
        int unquoteLen = 0;
        int i = start+1;
        for (; i < string.length(); i++) {
            char c = string.charAt(i);
            if (needsQuoting(c)) {
                blockQuote = blockQuote || needsBlockQuoting(c);
                unquoteLen = 0;
            } else {
                unquoteLen++;
                if (unquoteLen > MAX_UNQUOTE_LEN) {
                    i++;
                    break;
                }
                // if only one char is quoted
                if (unquoteLen > MAX_SINGLE_LOOKAHEAD && unquoteLen == i-start) {
                    i++;
                    break;
                }
            }
        }
        i -= unquoteLen;
        if (blockQuote || i - start > MAX_SINGLE_QUOTE) {
            appendBlockQuoted(sb, string, start, i);
        } else {
            appendSingleQuoted(sb, string, start, i);
        } 
        return i;
    }

    private static void appendBlockQuoted(StringBuilder sb, String s, int start, int end) {
        int slashEIndex = s.indexOf("\\E", start);
        while (slashEIndex > -1 && slashEIndex < end) {
            if (start < slashEIndex) {
                sb.append("\\Q");
                sb.append(s, start, slashEIndex);
                sb.append("\\E");
            }
            sb.append("\\\\E");
            start = slashEIndex + 2;
            slashEIndex = s.indexOf("\\E", start);
        }
        if (start > end) {
            assert start == end+1 : "appended an 'E' too much";
            sb.setLength(sb.length()-1);
        }
        if (start < end) {
            sb.append("\\Q");
            sb.append(s, start, end);
            sb.append("\\E");
        }
    }

    private static void appendSingleQuoted(StringBuilder sb, String string, int start, int end) {
        for (int i = start; i < end; i++) {
            char c = string.charAt(i);
            if (needsQuoting(c)) {
                sb.append('\\');
            }
            sb.append(c);    
        }
    }
    
}
