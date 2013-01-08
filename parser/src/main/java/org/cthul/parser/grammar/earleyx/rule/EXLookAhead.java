package org.cthul.parser.grammar.earleyx.rule;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;
import org.cthul.parser.grammar.api.RuleEval;

public class EXLookAhead extends EXProduction {

    public EXLookAhead(RuleEval eval, String symbol, int priority, String[] symbols, int[] priorities) {
        super(eval, symbol, priority, symbols, priorities);
    }

    @Override
    public Match match(Context<?> context, int startIndex, int endIndex, Match[] right) {
        return super.match(context, startIndex, startIndex, right);
    }
    
}
