package org.cthul.parser.earleyx;

import org.cthul.parser.earleyx.api.EXRuleSet;
import org.cthul.parser.earleyx.api.EXRule;
import java.util.*;
import org.cthul.parser.util.Format;

public class EarleyXGrammar {
    
    private final Map<String, EXRuleSet> rules = new HashMap<>();
    
    protected void add(EXRule p) {
        EXRuleSet ps = rules.get(p.getSymbol());
        if (ps == null) {
            ps = new EXRuleSet();
            rules.put(p.getSymbol(), ps);
        }
        ps.add(p, p.getPriority());
    }
    
    public Iterable<EXRule> getRules(String symbol, int precedence) {
        EXRuleSet ps = rules.get(symbol);
        if (ps == null) {
            return Collections.emptyList();
        }
        return ps.getAll(precedence);
    }

    public EXRuleSet getRules(String symbol) {
        return rules.get(symbol);
    }
        
    @Override
    public String toString() {
        return Format.join("{", ", ", "}", 1024, " ...}", rules.values());
    }

}
