package org.cthul.parser.rule;

import org.cthul.parser.api.RuleKey;

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
