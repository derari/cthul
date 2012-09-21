package org.cthul.strings.format.pattern;

import java.math.BigInteger;
import java.util.Locale;
import java.util.regex.Matcher;
import org.cthul.strings.format.PatternAPI;

/**
 *
 * @author Arian Treffer
 */
public class Standard {
    
    public static final DecimalPattern DECIMAL = new DecimalPattern();
    public static final StringPattern STRING = new StringPattern();
    
    public static class DecimalPattern extends PatternAlignmentBase {
        
        @Override
        protected int toRegex(PatternAPI regex, Locale locale, String flags, char padding, int precision, String formatString, int position) {
            regex.addedCapturingGroup();
            regex.append("([-+]?[0-9]+)");
            return 0;
        }        

        @Override
        public Object parse(Matcher matcher, int capturingBase, Object memento, Object lastArgValue) {
            BigInteger bi = new BigInteger(matcher.group(capturingBase));
            int len = bi.bitLength();
            if (len <= 32) return bi.intValue();
            if (len <= 64) return bi.longValue();
            return bi;
        }

    }
    
    public static class StringPattern extends PatternAlignmentBase {
        
        @Override
        protected int toRegex(PatternAPI regex, Locale locale, String flags, char padding, int precision, String formatString, int position) {
            regex.addedCapturingGroup();
            regex.append("(.*?)");
            return 0;
        }        

        @Override
        public Object parse(Matcher matcher, int capturingBase, Object memento, Object lastArgValue) {
            return matcher.group(capturingBase);
        }

    }
    
}
