package org.cthul.strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts strings into Java identifiers.
 * @author Arian Treffer
 */
public class JavaNames {

    /** Adds a non-empty substring to the list */
    private static void add(List<String> list, int start, int end, String s) {
        if (end > start) {
            list.add(s.substring(start, end));
        }
    }

    /**
     * Splits a String into parts that can be concatenated to a valid
     * Java identifier.
     * <p>
     * The result contains no empty strings.
     * @param s string to tokenize
     * @return array of strings
     */
    public static String[] tokenize(final String s) {
        final List<String> result = new ArrayList<>();
        
        int start = 0;                  // start of current token
        boolean uppercaseToken = false; // if current token is all uppercase
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (! Character.isJavaIdentifierPart(c) ||
                (result.isEmpty() && start == i &&
                    !Character.isJavaIdentifierStart(c))) {
                // This character should not appear in a Java name at all,
                // or this would be the first character of the result, 
                //    but it is not valid here.
                // Treat it as a separator.
                c = '_';
            }
            if (Character.isLowerCase(c) || Character.isDigit(c)) {
                // the token goes on, unless it's all uppercase
                if (uppercaseToken) {
                    // uppercase token ends here. The last uppercase
                    // character already was part of the next token.
                    add(result, start, i-1, s);
                    start = i-1;
                    // this is no uppercase token
                    uppercaseToken = false;
                }
            } else if (Character.isUpperCase(c)) {
                if (!uppercaseToken) {
                    // token ends here, new token starts
                    add(result, start, i, s);
                    start = i;
                }
                if (start == i) {
                    // first character is uppercase, this has potential for
                    // being an uppercase token, such as "HTTP"
                    uppercaseToken = true;
                } else {
                    assert uppercaseToken : "token should be uppercase";
                }
            } else { // c == '_'
                // token ends here, next char is start of next token
                add(result, start, i, s);
                start = i+1;
                // if this was an uppercase token, it ends now anyway
                uppercaseToken = false;
            }
        }
        // add last token
        add(result, start, s.length(), s);

        return result.toArray(new String[result.size()]);
    }

    public static String firstToUpper(String s) {
        return appendFirstToUpper(new StringBuilder(), s).toString();
    }
    
    public static StringBuilder appendFirstToUpper(StringBuilder target, String s) {
        if (s.isEmpty()) return target;
        target.append(Character.toUpperCase(s.charAt(0)));
        if (s.length() == 1) return target;
        target.append(s.substring(1).toLowerCase());
        return target;
    }
    
    public static String[] allToLower(final String... tokens) {
        for (int i = 0; i < tokens.length; i++)
            tokens[i] = tokens[i].toLowerCase();
        return tokens;
    }

    public static String[] allToUpper(final String... tokens) {
        for (int i = 0; i < tokens.length; i++)
            tokens[i] = tokens[i].toUpperCase();
        return tokens;
    }
    
    public static String[] allToCamel(final String... tokens) {
        for (int i = 0; i < tokens.length; i++)
            tokens[i] = firstToUpper(tokens[i]);
        return tokens;
    }
    
    /**
     * Converts {@code string} into CamelCase format, the first
     * character being in lower case.
     * @param string
     * @return String in camel case
     */
    public static String camelCase(String string) {
        return camelCase(true, string);
    }

    /**
     * Converts {@code string} into CamelCase format, the first
     * character being in upper case.
     * @param string
     * @return String in camel case
     */
    public static String CamelCase(String string) {
        return camelCase(false, string);
    }

    private static String camelCase(boolean firstToLower, String string) {
        return camelCase(new StringBuilder(), firstToLower, tokenize(string)).toString();
    }

    /**
     * Appends {@code tokens} to {@code target} in CamelCase format.
     * @param tokens parts of the word
     * @param target string builder the word is appended to
     * @param firstToLower if true, the first character will be in lowercase
     */
    public static StringBuilder camelCase(final StringBuilder target, boolean firstToLower, final String... tokens) {
        for (String t: tokens) {
            if (firstToLower) {
                firstToLower = false;
                target.append(t.toLowerCase());
            } else {
                appendFirstToUpper(target, t);
            }
        }
        return target;
    }

    /**
     * Appends {@code tokens} to {@code target}, all lower case,
     * separated by under_scores.
     * @param tokens
     * @param sb
     */
    public static StringBuilder under_score(final StringBuilder sb, final String... tokens) {
        return Strings.join(sb, "_", allToLower(tokens));
    }

    /**
     * Appends {@code tokens} to {@code target}, all upper case,
     * separated by under_scores.
     * @param tokens
     * @param sb
     */
    public static StringBuilder UNDER_SCORE(final StringBuilder sb, final String... tokens) {
        return Strings.join(sb, "_", allToUpper(tokens));
    }

    /**
     * Converts {@code string} into lower case, under_score format.
     * @param string
     * @return String in camel case
     */
    public static String under_score(String string) {
        return under_score(new StringBuilder(), tokenize(string)).toString();
    }

    /**
     * Converts {@code string} into upper case, under_score format.
     * @param string
     * @return String in camel case
     */
    public static String UNDER_SCORE(String string) {
        return UNDER_SCORE(new StringBuilder(), tokenize(string)).toString();
    }

}
