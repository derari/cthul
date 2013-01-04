package org.cthul.parser.earleyx.factory;

import java.util.*;
import org.cthul.parser.earleyx.EarleyXGrammar;
import org.cthul.parser.earleyx.api.EXRule;
import org.cthul.parser.earleyx.api.EXStringTokenRule;
import org.cthul.parser.util.Format;

public class EarleyXGrammarFactory {

    private final Map<String, String> stringCache = new HashMap<>();
    private final Set<String> generatedSymbols = new HashSet<>();
    private final SortedSet<RuleTemplate> rules = new TreeSet<>();
    private final Map<String, EXRuleKey> tokens = new HashMap<>();
    private int nextRuleId = 0;
    
    @SuppressWarnings("RedundantStringConstructorCall")
    protected String cached(String s) {
        String c = stringCache.get(s);
        if (c == null) {
            c = new String(s);
            stringCache.put(c, c);
        }
        return c;
    }
    
    protected EXRuleKey add(EXRule rule) {
        RuleTemplate rt = new RuleTemplate(nextRuleId++, rule);
        rules.add(rt);
        return rt.key;
    }
    
    public EarleyXGrammar create() {
        Grammar grammar = new Grammar();
        for (RuleTemplate rt: rules) {
            grammar.add(rt.rule);
        }
        return grammar;
    }
    
    public EXRuleKey key(String symbol, int priority) {
        return new EXRuleKey(cached(symbol), priority);
    }
    
    public EXRuleKey createToken(String symbol, String... strings) {
        if (symbol == null) {
            if (strings.length == 1) return token(strings[0]);
            symbol = generateSymbolForToken(strings);
        }        
        EXRule rule = new EXStringTokenRule(symbol, 0, strings);
        return add(rule);
    }
    
    public EXRuleKey token(String string) {
        EXRuleKey key = tokens.get(string);
        if (key != null) return key;
        String sym = generateSymbolForToken(string);
        EXRule rule = new EXStringTokenRule(sym, 0, string);
        key = add(rule);
        tokens.put(string, key);
        return key;
    }
    
    public EXProductionFactory newProduction(String symbol, int priority) {
        return internNewProduction(sanatizeSymbol(symbol), priority);
    }
    
    public EXProductionFactory newProduction(EXRuleKey key) {
        return internNewProduction(key.symbol, key.priority);
    }
    
    protected EXProductionFactory internNewProduction(String symbol, int priority) {
        return new EXProductionFactory(this, cached(symbol), priority);
    }
    
    protected EXProductionFactory internNewProduction(EXRuleKey key) {
        return internNewProduction(key.symbol, key.priority);
    }
    
    protected String sanatizeSymbol(String string) {
        return string.replace("$", "$$");
    }
    
    protected String generateSymbol(String string) {
        string = cached(string);
        if (!generatedSymbols.add(string)) 
            throw new IllegalArgumentException(string);
        return string;
    }
    
    protected boolean generatedExists(String s) {
        return generatedSymbols.contains(s);
    }
    
    protected String generateSymbolForToken(String... values) {
        String s = values.length == 0 ? "epsilon" : values[0];
        if (s == null || s.isEmpty()) s = "epsilon";
        for (int i = 0; i < s.length() && i < 10; i++) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                s = s.substring(0, i) + "#" + ((int) c) + s.substring(i+1);
            }
        }
        if (s.length() > 10) s = s.substring(0, 10);
        if (values.length > 1) s += "+" + (values.length-1);
        s = "$" + s;
        return generateUniqueSymbol(s, "_");
    }

    protected String generateUniqueSymbol(String s, String keySep) {
        if (generatedExists(s)) {
            return generateUniqueSymbol(keySep == null ? s : s+keySep);
        } else {
            return s;
        }
    }
    
    protected String generateUniqueSymbol(String s) {
        int i = 1;
        while (generatedExists(s+i)) i++;
        return generateSymbol(s+i);
    }

    @Override
    public String toString() {
        return Format.join("{", ", ", "}", 1024, "...}", rules);
    }
    
    protected static class Grammar extends EarleyXGrammar {
        @Override
        protected void add(EXRule p) {
            super.add(p);
        } 
    }
    
    private static class RuleTemplate implements Comparable<RuleTemplate> {
        
        private final int rId;
        private final EXRule<?> rule;
        private final EXRuleKey key;

        public RuleTemplate(int rId, EXRule<?> rule) {
            this.rId = rId;
            this.rule = rule;
            this.key = new EXRuleKey(rule);
        }

        @Override
        public int compareTo(RuleTemplate o) {
            int c = rule.getSymbol().compareTo(o.rule.getSymbol());
            if (c != 0) return c;
            c = o.rule.getPriority() - rule.getPriority();
            if (c != 0) return c;
            return rId - o.rId;
        }

        @Override
        public String toString() {
            return rule.toString();
        }
        
    }
    
}
