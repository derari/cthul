package org.cthul.parser.grammar.packrat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cthul.parser.grammar.packrat.PRProduction.Part;

/**
 *
 * @author derari
 */
public class ProductionParser {
    
    /*
     * (a a / b b) (c / d)
     * 
     * a - a - c - z
     * b - b / d /
     * 
     * a /
     * 
     * a - z
     * z
     * 
     * / a
     * 
     * z
     * 
     */
    
    public static Part[] parse(final String production) {
        Matcher matcher = tokenPattern.matcher(production);
        List<Part> alts = new ArrayList<>();
        List<Part> parts = new ArrayList<>();
        Part last = null;
        Part alt = null;

        int start = 0;
        scan:
        while (start < production.length() && matcher.find(start)) {
            if (start != matcher.start()) {
                throw new IllegalArgumentException(String.format(
                        "Unexpected token in production at %d: %s",
                        start, production.substring(start, 
                            Math.min(start + 5, matcher.start()))));
            }
            Part next = null;
            String token = null;
            final int t = getTokenType(matcher);
            switch (t) {
                case G_QUOTED_TOKEN:
                    String quoted = matcher.group(G_QUOTED_TOKEN);
                    token = quoted.substring(1, quoted.length() - 1);
                case G_SIMPLE_TOKEN:
                    if (token == null) token = matcher.group(G_SIMPLE_TOKEN);
                    next = new Part();
                    next.key = token;
                    break;
                case G_ALTERNATIVE:
                    if (last == null) break scan;
                    break;
                case G_WHITESPACE:
                    break;
                default:
                    throw new AssertionError("Token group index " + t);
            }
            if (next != null) {
                next.index = parts.size();
                parts.add(next);
                if (last != null) last.next = next;
                last = next;
            }
            start = matcher.end();
        }
        if (start < production.length()) {
            throw new IllegalArgumentException(String.format(
                    "Unexpected token in production at %d: %s",
                    start, production.substring(start, 
                            Math.min(start+5, production.length()))));
        }
        return parts.toArray(new Part[parts.size()]);
    }
    
    private static int getTokenType(Matcher matcher) {
        for (int g: TOKEN_GROUPS) {
            if (matcher.group(g) != null) return g;
        }
        throw new AssertionError("Unexpected match " + matcher);
    }
    
    private static final int G_WHITESPACE =     1;
    private static final int G_SIMPLE_TOKEN =   2;
    private static final int G_QUOTED_TOKEN =   3;
    private static final int G_ALTERNATIVE =    5;
    private static final int[] TOKEN_GROUPS = {
            G_WHITESPACE, G_SIMPLE_TOKEN, G_QUOTED_TOKEN, G_ALTERNATIVE,
        };
    
    private static final Pattern tokenPattern = Pattern.compile(
            "(\\s+)" +              // white space
            "|([a-zA-Z0-9$_]+)" +   // simple token
            "|('([^']|\\\\')+')" +  // quoted token
            "|(/)" +                // alt token
        "");

}
