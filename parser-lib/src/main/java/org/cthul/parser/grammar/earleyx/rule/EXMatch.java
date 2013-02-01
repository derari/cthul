package org.cthul.parser.grammar.earleyx.rule;

import org.cthul.parser.api.*;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.RuleEval;

public class EXMatch extends AbstractMatch<Object> implements ProductionMatch {
    
    protected final EXRule<?> rule;
    protected final RuleEval eval;
    protected final Context<?> context;
    protected final Match<?>[] matches;

    public EXMatch(EXRule<?> rule, RuleEval eval, Context<?> context, Match<?>[] matches, int startIndex, int endIndex) {
        this(rule, eval, context, context.getInput(), matches, startIndex, endIndex);
    }
    
    public EXMatch(EXRule<?> rule, RuleEval eval, Context<?> context, Input<?> input, Match<?>[] matches, int startIndex, int endIndex) {
        super(rule.getSymbol(), rule.getPriority(), input.startPosition(startIndex), startIndex, input.endPosition(endIndex), endIndex);
        this.rule = rule;
        this.eval = eval;
        this.context = context;
        this.matches = matches;
    }

    @Override
    public Match[] matches() {
        return matches;
    }

    @Override
    public Object eval() {
        return eval(null);
    }
    
    @Override
    public Object eval(Object arg) {
        return eval.eval(context, this, arg);
    }

    @Override
    public String toString() {
        return super.toString() + " " + rule;
    }
}
