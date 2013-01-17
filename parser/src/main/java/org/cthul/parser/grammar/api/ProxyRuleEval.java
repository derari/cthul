package org.cthul.parser.grammar.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;

public class ProxyRuleEval implements RuleEval {

    protected static final ProxyRuleEval INSTANCE = new ProxyRuleEval();
    
    public static ProxyRuleEval instance() {
        return INSTANCE;
    }
    
    @Override
    public Object eval(Context<?> context, ProductionMatch match, Object arg) {
        Match[] matches = match.matches();
        if (matches.length != 1) {
            throw new IllegalArgumentException(
                    "Expected exactly one sub-match, got " + matches.length + 
                    " for " + match);
        }
        return matches[0].eval(arg);
    }
    
}
