package org.cthul.strings.format.pattern;

import java.math.BigInteger;
import java.util.Locale;
import java.util.regex.Matcher;
import org.cthul.strings.format.PatternAPI;

/**
 *
 * @author Arian Treffer
 */
public class StandardPattern {
    
    public static final DecimalValue DECIMAL = new DecimalValue();
    public static final StringValue STRING = new StringValue();
    
    public static class StringValue extends PatternAlignmentBase {
        
        @Override
        protected int toRegex(PatternAPI regex, Locale locale, String flags, char padding, int precision, String formatString, int position) {
            regex.addedCapturingGroup();
            regex.append("(.*?)");
            return 0;
        }        

        @Override
        protected Object parse(Matcher matcher, int capturingBase, int width, Object memento, Object lastArgValue) {
            return matcher.group(capturingBase);
        }

    }
    
}
