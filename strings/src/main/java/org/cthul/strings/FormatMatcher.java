package org.cthul.strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import org.cthul.strings.format.ConversionPattern;

/**
 *
 * @author Arian Treffer
 */
public class FormatMatcher {
    
    private final FormatPattern pattern;
    private final Matcher matcher;
    private final int patternCount;
    private final FormatPattern.Match[] matches;

    protected FormatMatcher(FormatPattern pattern, Matcher matcher, int patternCount, FormatPattern.Match[] matches) {
        this.pattern = pattern;
        this.matcher = matcher;
        this.patternCount = patternCount;
        this.matches = matches;
    }

    public FormatPattern pattern() {
        return pattern;
    }
    
    public List<Object> match() {
        final List<Object> result = new ArrayList<>();
        if (matches(result)) {
            return result;
        } else {
            return null;
        }
    }
    
    public boolean matches(List<Object> result) {
        if (!matcher.matches()) return false;
        applyMatch(result);
        return true;
    }
    
    public boolean find(List<Object> result) {
        if (!matcher.find()) return false;
        applyMatch(result);
        return true;
    }
    
    public boolean find(int start, List<Object> result) {
        if (!matcher.find(start)) return false;
        applyMatch(result);
        return true;
    }
    
    protected void applyMatch(List<Object> result) {
        for (int i = 0; i < patternCount; i++) {
            FormatPattern.Match match = matches[i];
            Object value = getArg(result, match.getArgId());
            value = match.getPattern().parse(matcher, match.getCaptureBase(), match.getMemento(), value);
            result.set(i, value);
        }
        final int length = result.size();
        for (int i = 0; i < length; i++) {
            Object o = result.get(i);
            if (o instanceof ConversionPattern.Intermediate) {
                o = ((ConversionPattern.Intermediate) o).complete();
                result.set(i, o);
            }
        }
    }

    protected Object getArg(List<Object> result, int argId) {
        while (result.size() <= argId) result.add(null);
        return result.get(argId);
    }
    
}
