package org.cthul.strings.format;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;

/**
 *
 * @author Arian Treffer
 */
public class PatternAPIStub implements PatternAPI {
    
    public static Proc toRegexCall = Procs.invoke("toRegex", 7);
    public static Proc parseCall = Procs.invoke("parse", 8);

    public static String toRegex(ConversionPattern pattern, Locale locale, String flags, int width, int precision, String formatString, int position) {
        PatternAPIStub api = new PatternAPIStub();
        pattern.toRegex(api, locale, flags, width, precision, formatString, position);
        return api.toString();
    }
    
    public static Object parse(ConversionPattern pattern, String input, Locale locale, String flags, int width, int precision, String formatString, int position) {
        PatternAPIStub api = new PatternAPIStub();
        pattern.toRegex(api, locale, flags, width, precision, formatString, position);
        Matcher matcher = Pattern.compile(api.toString()).matcher(input);
        MatcherAPIStub mApi = new MatcherAPIStub(null, matcher);
        if (!matcher.matches()) throw new AssertionError("no match");
        return pattern.parse(mApi, matcher, 0, api.getMemento(), null);
    }
    
    public static Proc toRegexCall(ConversionPattern pattern, String input, Locale locale, String flags, int width, int precision, String formatString, int position) {
        return toRegexCall.call(pattern, input, locale, flags, width, precision, formatString, position);
    }
    
    public static Proc parseCall(ConversionPattern pattern, String input, Locale locale, String flags, int width, int precision, String formatString, int position) {
        return parseCall.call(pattern, input, locale, flags, width, precision, formatString, position);
    }
    
    private final StringBuilder sb = new StringBuilder();
    private int groupCount = 1;
    private Object memento;
    
    @Override
    public PatternAPI append(char c) {
        sb.append(c);
        return this;
    }

    @Override
    public PatternAPI append(CharSequence csq) {
        sb.append(csq);
        return this;
    }

    @Override
    public PatternAPI append(CharSequence csq, int start, int end) {
        sb.append(csq, start, end);
        return this;
    }

    @Override
    public PatternAPI addedCapturingGroup() {
        groupCount++;
        return this;
    }

    @Override
    public PatternAPI addedCapturingGroups(int i) {
        groupCount += i;
        return this;
    }

    @Override
    public Object putMemento(Object newMemento) {
        Object old = memento;
        memento = newMemento;
        return old;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public Object getMemento() {
        return memento;
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    @Override
    public PatternData parse(PatternAPI api, String format) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PatternData parse(PatternAPI api, String format, int start, int end) {
        throw new UnsupportedOperationException();
    }
    
}
