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
     * @return the quoted pattern
     */
    public static String quote(final String string) {
        int last = 0;
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (needsQuoting(string.charAt(i))) {
                sb.append(string, last, i);
                last = i = quote(sb, string, i);
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
    
    /** @return true if the character has to be quoted */
    private static boolean needsQuoting(char c) {
        if (c < 256 && QUOTED[c]) return true;
        return needsBlockQuoting(c);
    }
    
    /** @return true if the character cannot be escaped with backslash */
    private static boolean needsBlockQuoting(char c) {
        return c < 33 || Character.isWhitespace(c);
    }
    
    private static final int MAX_UNQUOTE_LEN = 6;
    private static final int MAX_SINGLE_QUOTE_LEN = 3;
    private static final int MAX_SINGLE_UNQUOTE_LEN = 2;

    /**
     * Appends following part of string quoted to {@code target}.
     * @param target
     * @param string
     * @param start
     * @return index of first character that was not appended
     */
    private static int quote(final StringBuilder target, final String string, final int start) {
        int i = quoteSingle(target, string, start);
        if (i < 0) i = quoteBlock(target, string, start, -i);
        return i;
    }
    
    /** 
     * Tries to quote each character with a backslash.
     */
    private static int quoteSingle(final StringBuilder sb, final String string, final int start) {
        int i = start+1;
        if (needsBlockQuoting(string.charAt(start))) return -i;
        int unquoteLen = 0;
        int quotedMap = 1;
        for (int j = 1; i < string.length(); i++, j++) {
            char c = string.charAt(i);
            if (needsQuoting(c)) {
                if (j > MAX_SINGLE_QUOTE_LEN || needsBlockQuoting(c)) return -i-1;
                quotedMap |= 1 << j;
                unquoteLen = 0;
            } else {
                if (unquoteLen == MAX_UNQUOTE_LEN ||
                        (quotedMap == 1 && 
                         unquoteLen == MAX_SINGLE_UNQUOTE_LEN)) {
                    break;
                }
                unquoteLen++;
            }
        }
        appendSingleQuoted(sb, string, start, i - unquoteLen, quotedMap);
        sb.append(string, i - unquoteLen, i);
        return i;
    }
    
    private static int quoteBlock(final StringBuilder sb, final String string, final int start, int i) {
        int unquoteLen = 0;
        for (; i < string.length(); i++) {
            char c = string.charAt(i);
            if (needsQuoting(c)) {
                unquoteLen = 0;
            } else {
                if (unquoteLen == MAX_UNQUOTE_LEN) {
                    break;
                }
                unquoteLen++;
            }
        }
        appendBlockQuoted(sb, string, start, i - unquoteLen);
        sb.append(string, i - unquoteLen, i);
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

    private static void appendSingleQuoted(StringBuilder sb, String string, int start, int end, int quoteMap) {
        for (int i = start; i < end; i++) {
            char c = string.charAt(i);
            if ((quoteMap & 1) == 1) {
                sb.append('\\');
            }
            quoteMap >>>= 1;
            sb.append(c);    
        }
    }
    
}
