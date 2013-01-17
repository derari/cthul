package org.cthul.parser.grammar.api;

import org.cthul.parser.api.Context;

public interface RuleEval {
    
    Object eval(Context<?> context, ProductionMatch match, Object arg);
    
}
