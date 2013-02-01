package org.cthul.parser.rule;

import org.cthul.parser.grammar.api.RuleEval;

public class Rule {
    
    private RuleEval eval;
    
    public Rule() {
    }

    public void setEval(RuleEval eval) {
        this.eval = eval;
    }

    public RuleEval getEval() {
        return eval;
    }

}
