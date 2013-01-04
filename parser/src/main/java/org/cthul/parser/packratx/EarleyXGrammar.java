package org.cthul.parser.packratx;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EarleyXGrammar {
    
    private final Map<String, EXProductionSet> productions = new HashMap<>();
    
    public void add(EXProduction p) {
        EXProductionSet ps = productions.get(p.getSymbol());
        if (ps == null) {
            ps = new EXProductionSet();
            productions.put(p.getSymbol(), ps);
        }
        ps.add(p);
    }
    
    public Iterable<EXProduction> getProductions(String symbol, int precedence) {
        EXProductionSet ps = productions.get(symbol);
        if (ps == null) {
            return Collections.emptyList();
        }
        return ps.getAll(precedence);
    }
        
}
