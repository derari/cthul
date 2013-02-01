package org.cthul.strings.format;

import java.util.Locale;
import java.util.regex.Matcher;

/**
 * Converts a format pattern into a regular expression.
 * 
 * @author Arian Treffer
 */
public interface ConversionPattern {
    
    /**
     * Appends pattern to regex
     * 
     * @param pattern
     * @param locale
     * @param flags
     * @param width
     * @param precision
     * @param formatString
     * @param position
     * @return additional characters that were read from {@code formatString},
     *         should be zero for most implementations.
     */
    int toRegex(PatternAPI pattern, Locale locale, String flags, int width, int precision, String formatString, int position);

    /**
     * Parses a pattern match.
     * 
     * @param matcherAPI
     * @param matcher
     * @param capturingBase
     * @param memento
     * @param lastArgValue
     * @return the parsed value
     */
    Object parse(MatcherAPI matcherAPI, Matcher matcher, int capturingBase, Object memento, Object lastArgValue);
    
    static interface Intermediate {
        
        Object complete();
        
    }
}
