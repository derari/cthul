package org.cthul.parser.grammar.earleyx.rule;

import org.cthul.parser.api.AbstractMatch;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.RuleEval;

public class EXMatch extends AbstractMatch<Object> implements ProductionMatch {
    
    protected final EXRule<?> rule;
    protected final RuleEval eval;
    protected final Context<?> context;
    protected final Match<?>[] matches;

    public EXMatch(EXRule<?> rule, RuleEval eval, Context<?> context, Match<?>[] matches, int start, int startIndex, int end, int endIndex) {
        super(rule.getSymbol(), rule.getPriority(), start, startIndex, end, endIndex);
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
        return eval.eval(context, this);
    }

    @Override
    public String toString() {
        return super.toString() + " " + rule;
    }
}
