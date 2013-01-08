package org.cthul.parser.grammar.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.RuleEval;

public class NullRuleEval implements RuleEval {

    protected static final NullRuleEval INSTANCE = new NullRuleEval();
    
    public static NullRuleEval instance() {
        return INSTANCE;
    }
    
    @Override
    public Object eval(Context<?> context, ProductionMatch match) {
        return null;
    }
    
}
