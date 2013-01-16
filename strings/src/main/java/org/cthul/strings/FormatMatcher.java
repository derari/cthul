package org.cthul.strings;

import java.util.List;
import java.util.regex.Matcher;
import org.cthul.strings.format.*;

/**
 *
 * @author Arian Treffer
 */
public class FormatMatcher {
    
    private final FormatPattern pattern;
    private final Matcher matcher;
    private final PatternData data;

    protected FormatMatcher(FormatPattern pattern, Matcher matcher, PatternData data) {
        this.pattern = pattern;
        this.matcher = matcher;
        this.data = data;
    }

    public FormatPattern pattern() {
        return pattern;
    }
    
    /**
     * Matches the entire input and returns a list of extracted values.
     * @return list of values, or null if match failed
     */
    public List<Object> match() {
        final IntMatchResults result = new IntMatchResults();
        if (matches(result)) {
            return result.getIntResultList();
        } else {
            return null;
        }
    }
    
    /**
     * Matches the entire input and fills the result.
     * @param result
     * @return true iff match was successfull
     */
    public boolean matches(MatchResults result) {
        if (!matcher.matches()) return false;
        applyMatch(result);
        return true;
    }
    
    /**
     * Finds the next match and fills the result
     * @param result
     * @return true iff match was found
     */
    public boolean find(MatchResults result) {
        if (!matcher.find()) return false;
        applyMatch(result);
        return true;
    }
    
    /**
     * Finds the next match and fills the result
     * @param start
     * @param result
     * @return true iff match was found
     */
    public boolean find(int start, MatchResults result) {
        if (!matcher.find(start)) return false;
        applyMatch(result);
        return true;
    }
    
    protected void applyMatch(MatchResults result) {
        applyMatch(result, 0);
    }
    
    protected void applyMatch(MatchResults result, int caputingBase) {
        new API(result).apply(data, caputingBase);
    }

    protected class API implements MatcherAPI {
        
        protected final MatchResults results;

        public API(MatchResults results) {
            this.results = results;
        }

        @Override
        public void apply(PatternData data, int capturingBase) {
            data.apply(this, matcher, capturingBase, results);
        }
        
    }
    
}
