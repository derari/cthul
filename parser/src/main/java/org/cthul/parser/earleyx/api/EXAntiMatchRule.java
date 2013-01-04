package org.cthul.parser.earleyx.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.MatchEval;
import org.cthul.parser.api.Rule;

public class EXAntiMatchRule<C extends Context<?>> extends EXRule<C> {

    private final MatchEval eval;
    
    public EXAntiMatchRule(MatchEval eval, String symbol, int priority, String[] symbols, int[] priorities) {
        super(symbol, priority, symbols, priorities);
        this.eval = eval;
    }

    @Override
    public EXMatch match(C context, int start, int end, EXMatch[] right) {
        return new Blocked(this, start, end, right);
    }
    
    public EXMatch antiMatch(C context, int index) {
        return new EXProductionMatch(eval, this, index, index, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Blocked) {
            obj = ((Blocked) obj).getRule();
        }
        return super.equals(obj);
    }

    @Override
    protected String productionOperator() {
        return "::= !(";
    }

    @Override
    public String toString() {
        return super.toString() + ")";
    }

    public static class Blocked extends EXMatch {
        
        public Blocked(Rule rule, int inputStart, int inputEnd, EXMatch[] args) {
            super(rule, inputStart, inputEnd, args);
        }

        @Override
        public EXAntiMatchRule<?> getRule() {
            return (EXAntiMatchRule) super.getRule();
        }

        @Override
        public Object eval() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return getRule().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof EXAntiMatchRule) {
                return getRule().equals(obj);
            }
            return super.equals(obj);
        }
    }
    
}
