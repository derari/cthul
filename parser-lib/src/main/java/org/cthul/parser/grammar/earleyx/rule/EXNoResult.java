package org.cthul.parser.grammar.earleyx.rule;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.Match;
import org.cthul.parser.grammar.api.RuleEval;


public class EXNoResult extends EXRule<Input<?>> {

    private final RuleEval eval;
    
    public EXNoResult(RuleEval eval, String symbol, int priority, String[] symbols, int[] priorities) {
        super(symbol, priority, symbols, priorities);
        this.eval = eval;
    }

    @Override
    public Match match(Context<?> context, int start, int end, Match[] right) {
        return new SkipSubtree(this, context, start, end, right);
    }
    
//    public EXMatch antiMatch(Context<?> context, int index) {
//        return new EXMatch(this, eval, context, null, index, index);
//    }
//
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SkipSubtree) {
            obj = ((SkipSubtree) obj).getRule();
        }
        return super.equals(obj);
    }

    @Override
    protected String productionOperator() {
        return "::= :(";
    }

    @Override
    public String toString() {
        return super.toString() + " )";
    }

    public static class SkipSubtree extends EXMatch {
        
        public SkipSubtree(EXRule<?> rule, Context<?> ctx, int inputStart, int inputEnd, Match[] args) {
            super(rule, null, ctx, args, inputStart, inputEnd);
        }

        public EXNoResult getRule() {
            return (EXNoResult) rule;
        }

        @Override
        public Object eval(Object arg) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return ":" + super.toString();
        }
    }
    
}
