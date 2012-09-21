package org.cthul.strings.format;

import java.util.Locale;
import java.util.regex.Matcher;

/**
 *
 * @author Arian Treffer
 */
public interface ConversionPattern {
    
    /**
     * Appends pattern to regex
     * 
     * @param regex
     * @param locale
     * @param flags
     * @param width
     * @param precision
     * @param formatString
     * @param position
     * @return additional characters that were read from {@code formatString},
     *         should be zero for most implementations.
     */
    public int toRegex(PatternAPI pattern, Locale locale, String flags, int width, int precision, String formatString, int position);
    
    public Object parse(Matcher matcher, int capturingBase, Object memento, Object lastArgValue);
    
    static interface Intermediate {
        
        Object complete();
        
    }
}
