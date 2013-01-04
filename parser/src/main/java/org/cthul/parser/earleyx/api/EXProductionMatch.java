package org.cthul.parser.earleyx.api;

import org.cthul.parser.api.MatchEval;
import org.cthul.parser.api.Rule;

public class EXProductionMatch extends EXMatch {

    private final MatchEval eval;
    
    public EXProductionMatch(MatchEval eval, Rule rule, int inputStart, int inputEnd, EXMatch[] args) {
        super(rule, inputStart, inputEnd, args);
        this.eval = eval;
    }

    @Override
    public Object eval() {
        return eval.eval(this);
    }
        
}
