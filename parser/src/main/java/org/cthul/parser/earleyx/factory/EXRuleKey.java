package org.cthul.parser.earleyx.factory;

import org.cthul.parser.earleyx.api.EXRule;
import org.cthul.parser.util.Format;

public class EXRuleKey {
    
    final String symbol;
    final int priority;

    public EXRuleKey(EXRule rule) {
        this(rule.getSymbol(), rule.getPriority());
    }
    
    public EXRuleKey(String symbol, int priority) {
        this.symbol = symbol;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return Format.productionKey(symbol, priority);
    }
    
}
