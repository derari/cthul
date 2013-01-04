package org.cthul.parser.earleyx.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.MatchEval;

public class EXProduction<C extends Context<?>> extends EXRule<C> {
    
    private final MatchEval eval;

    public EXProduction(MatchEval eval, String symbol, int priority) {
        super(symbol, priority);
        this.eval = eval;
    }

    public EXProduction(MatchEval eval, String symbol, int priority, String[] symbols, int[] priorities) {
        super(symbol, priority, symbols, priorities);
        this.eval = eval;
    }

    @Override
    public EXMatch match(Context context, int start, int end, EXMatch[] right) {
        return new EXProductionMatch(eval, this, start, end, right);
    }
    
}
