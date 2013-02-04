package org.cthul.parser.grammar.earleyx.algorithm;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.util.Format;

public class TestRuleEval implements RuleEval {
    
    public static final TestRuleEval INSTANCE = new TestRuleEval();

    public static TestRuleEval instance() {
        return INSTANCE;
    }
    
    @Override
    public Object eval(Context<?> context, ProductionMatch match, Object arg) {
        Match[] args = (Match[]) match.matches();
        if (args == null) return match.getSymbol() + "()";
        Object[] values = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            values[i] = args[i].eval(arg);
        }
        return Format.join(match.getSymbol()+"(", ",", ")", values);
    }
    
}
