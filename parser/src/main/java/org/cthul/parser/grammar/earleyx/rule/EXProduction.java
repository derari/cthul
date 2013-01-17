package org.cthul.parser.grammar.earleyx.rule;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.Match;
import org.cthul.parser.grammar.api.RuleEval;

public class EXProduction extends EXRule<Input<?>> {
    
    protected final RuleEval eval;

    public EXProduction(RuleEval eval, String symbol, int priority) {
        super(symbol, priority);
        this.eval = eval;
    }

    public EXProduction(RuleEval eval, String symbol, int priority, String[] symbols, int[] priorities) {
        super(symbol, priority, symbols, priorities);
        this.eval = eval;
    }

    @Override
    public Match match(Context<? extends Input<?>> context, int startIndex, int endIndex, Match[] right) {
        return new EXMatch(this, eval, context, right, -1, startIndex, -1, endIndex);
    }
    
}
