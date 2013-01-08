package org.cthul.parser.grammar.earleyx.rule;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.Match;
import org.cthul.parser.grammar.api.RuleEval;


public class EXAntiMatch extends EXRule<Input<?>> {

    private final RuleEval eval;
    
    public EXAntiMatch(RuleEval eval, String symbol, int priority, String[] symbols, int[] priorities) {
        super(symbol, priority, symbols, priorities);
        this.eval = eval;
    }

    @Override
    public Match match(Context<? extends Input<?>> context, int start, int end, Match[] right) {
        return new Blocked(this, context, start, end, right);
    }
    
    public EXMatch antiMatch(Context<?> context, int index) {
        return new EXMatch(this, eval, context, null, -1, index, -1, index);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
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
        return super.toString() + " )";
    }

    public static class Blocked extends EXMatch {
        
        public Blocked(EXRule<?> rule, Context<?> ctx, int inputStart, int inputEnd, Match[] args) {
            super(rule, null, ctx, args, -1, inputStart, -1, inputEnd);
        }

        public EXAntiMatch getRule() {
            return (EXAntiMatch) rule;
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
            if (obj instanceof EXAntiMatch) {
                return getRule().equals(obj);
            }
            return super.equals(obj);
        }

        @Override
        public String toString() {
            return "!!" + super.toString();
        }
    }
    
}
