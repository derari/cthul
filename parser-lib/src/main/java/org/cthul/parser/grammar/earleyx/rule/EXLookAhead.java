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
        return new LookAhead(this, eval, context, startIndex, right);
    }
    
    @Override
    protected String productionOperator() {
        return "::= &(";
    }

    @Override
    public String toString() {
        return super.toString() + " )";
    }
    
    public static class LookAhead extends EXMatch {
        
        public LookAhead(EXRule<?> rule, RuleEval eval, Context<?> ctx, int inputStart, Match[] args) {
            super(rule, eval, ctx, args, inputStart, inputStart);
        }

        public EXLookAhead getRule() {
            return (EXLookAhead) rule;
        }

        @Override
        public String toString() {
            return "&" + super.toString();
        }
    }
    
}
