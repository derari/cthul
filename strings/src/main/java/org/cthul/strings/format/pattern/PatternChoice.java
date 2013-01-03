 package org.cthul.strings.format.pattern;

import org.cthul.strings.format.PatternAPI;

/**
 *
 * @author Arian Treffer
 */
public class PatternChoice extends PatternSet {

    public PatternChoice(PatternAPI api, int groupCount) {
        super(api, groupCount);
    }

    public PatternChoice(PatternAPI api) {
        super(api);
    }
    
    @Override
    protected void beforeFirstSubpattern() {
        baseAPI().append("(?:");
    }
    
    @Override
    protected void nextSubpattern() {
        baseAPI().append('|');
    }
    
    @Override
    protected void afterLastSubpattern() {
        baseAPI().append(')');
    }
        
}
