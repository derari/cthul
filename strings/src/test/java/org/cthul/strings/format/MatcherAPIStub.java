package org.cthul.strings.format;

import java.util.regex.Matcher;

/**
 *
 * @author Arian Treffer
 */
public class MatcherAPIStub implements MatcherAPI {
    
    protected final MatchResults results;
    protected final Matcher matcher;

    public MatcherAPIStub(MatchResults results, Matcher matcher) {
        this.results = results;
        this.matcher = matcher;
    }

    @Override
    public void apply(PatternData data, int caputingBase) {
        data.apply(this, matcher, caputingBase, results);
    }

}
