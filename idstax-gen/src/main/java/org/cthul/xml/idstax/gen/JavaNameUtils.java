package org.cthul.xml.idstax.gen;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts strings into java identifiers.
 * @author derari
 */
public class JavaNameUtils {

    private static void add(List<String> list, int start, int end, String s) {
        if (end > start) {
            list.add(s.substring(start, end));
        }
    }

    public static String[] tokenize(String s) {
        List<String> result = new ArrayList<String>();
        
        int nextStart = 0;
        boolean uppercaseToken = false;
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (! Character.isJavaIdentifierPart(c) ||
                (result.isEmpty() && !Character.isJavaIdentifierStart(c))) {
                c = '_';
            }
            
            if (Character.isLowerCase(c)) {
                if (uppercaseToken) {
                    if (nextStart+1==i) {
                        // only the first character was uppercase,
                        // this is no uppercase token
                        uppercaseToken = false;
                        i++;
                    } else {
                        // uppercase token ends here. The last uppercase
                        // character already was part of the next token
                        add(result, nextStart, i-1, s);
                        nextStart = i-1;
                        i++;
                    }
                } else {
                    // just another lowercase char
                    i++;
                }
            } else if (Character.isUpperCase(c)) {
                if (nextStart == i) {
                    // first character is uppercase, this has potential for
                    // being an uppercase token, like "HTTP"
                    uppercaseToken = true;
                    i++;
                } else if (uppercaseToken) {
                    // this is an uppercase token, continue
                    i++;
                } else {
                    // token ends here, new token starts
                    add(result, nextStart, i, s);
                    nextStart = i;
                }
            } else { // c == '_'
                // token ends here, next char is start of next token
                add(result, nextStart, i, s);
                i++;
                nextStart = i;
            }
        }
        // add last token
        add(result, nextStart, i, s);
        
        return result.toArray(new String[result.size()]);
    }

    private static void firstToUpper(String s, StringBuilder target) {
        if (s.isEmpty()) return;
        target.append(Character.toUpperCase(s.charAt(0)));
        if (s.length() == 1) return;
        target.append(s.substring(1).toLowerCase());
    }

    public static String camelCase(String s) {
        return camelCase(s, true);
    }

    public static String CamelCase(String s) {
        return camelCase(s, false);
    }

    private static String camelCase(String s, boolean firstToLower) {
        String[] tokens = tokenize(s);
        StringBuilder sb = new StringBuilder();
        for (String t: tokens) {
            if (firstToLower && sb.length() == 0) {
                sb.append(t.toLowerCase());
            } else {
                firstToUpper(t, sb);
            }
        }
        return sb.toString();
    }

    public static String underscore(String s) {
        String[] tokens = tokenize(s);
        StringBuilder sb = new StringBuilder();
        for (String t: tokens) {
            if (sb.length() > 0) sb.append('_');
            sb.append(t);
        }
        return sb.toString();
    }

    public static String UNDERSCORE(String s) {
        return underscore(s).toUpperCase();
    }

}
