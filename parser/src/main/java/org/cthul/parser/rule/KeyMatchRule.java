package org.cthul.parser.rule;

import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.api.ProxyRuleEval;
import org.cthul.parser.grammar.api.RuleEval;

public class KeyMatchRule extends Rule {
    
    protected final RuleKey key;

    public KeyMatchRule(RuleKey key) {
        this.key = key;
    }

    public KeyMatchRule(String symbol, int priority) {
        this(new RuleKey(symbol, priority));
    }

    public RuleKey getKey() {
        return key;
    }
    
}
