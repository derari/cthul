package org.cthul.parser.earleyx.factory;

import java.util.ArrayList;
import java.util.List;
import org.cthul.parser.api.MatchEval;
import org.cthul.parser.earleyx.api.EXProduction;
import org.cthul.parser.earleyx.api.EXRule;
import org.cthul.parser.util.Format;
import org.cthul.parser.util.Tools;

public class EXProductionFactory {
    
    private final EarleyXGrammarFactory gf;
    private String symbol;
    private int priority;
    private MatchEval eval;
    private RuleType ruleType = EXPRODUCTION;
    
    private final List<String> symbols = new ArrayList<>();
    private final List<Integer> priorities = new ArrayList<>();

    protected EXProductionFactory(EarleyXGrammarFactory gf, String symbol, int priority) {
        this.gf = gf;
        this.symbol = symbol;
        this.priority = priority;
    }
    
    public EXProductionFactory add(String symbol, int priority) {
        return internAdd(gf.sanatizeSymbol(symbol), priority);
    }
    
    public EXProductionFactory add(EXRuleKey key) {
        return internAdd(key.symbol, key.priority);
    }
    
    protected EXProductionFactory internAdd(String symbol, int priority) {
        symbols.add(symbol);
        priorities.add(priority);
        return this;
    }
    
    public EXRuleKey newSubkey() {
        return newSubkey("", 0);
    }
    
    public EXRuleKey newSubkey(String subSymbol) {
        return newSubkey(subSymbol, 0);
    }
    
    public EXRuleKey newSubkey(int priority) {
        return newSubkey("", priority);
    }
    
    public EXRuleKey newSubkey(String subSymbol, int priority) {
        return new EXRuleKey(gf.generateUniqueSymbol(symbol + "$" + subSymbol, ""), priority);
    }
    
    public EXProductionFactory newSubRule() {
        return newSubRule("", 0);
    }
    
    public EXProductionFactory newSubRule(String subSymbol) {
        return newSubRule(subSymbol, 0);
    }
    
    public EXProductionFactory newSubRule(int priority) {
        return newSubRule("", priority);
    }
    
    public EXProductionFactory newSubRule(String subSymbol, int priority) {
        return gf.newProduction(newSubkey(subSymbol, priority));
    }

    public EXRuleKey create() {
        EXRule p = ruleType.create(eval, symbol, priority, 
                Tools.strings(symbols), Tools.ints(priorities));
        return gf.add(p);
    }
    
    public EXProductionFactory reset() {
        symbols.clear();
        priorities.clear();
        return this;
    }
    
    public EXRuleKey getKey() {
        return new EXRuleKey(symbol, priority);
    }
    
    public String getSymbol() {
        return symbol;
    }

    public EXProductionFactory setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public EXProductionFactory setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public MatchEval getEval() {
        return eval;
    }

    public EXProductionFactory setEval(MatchEval eval) {
        this.eval = eval;
        return this;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    @Override
    public String toString() {
        return Format.production(symbol, priority, Tools.strings(symbols), Tools.ints(priorities));
    }

    public static interface RuleType {
        
        EXRule<?> create(MatchEval eval, String symbol, int priority, String[] symbols, int[] priorities) ;
        
    }
    
    public static final RuleType EXPRODUCTION = new EXProductionType();
    
    public static class EXProductionType implements RuleType {

        @Override
        public EXRule<?> create(MatchEval eval, String symbol, int priority, String[] symbols, int[] priorities) {
            return new EXProduction(eval, symbol, priority, symbols, priorities);
        }
        
    }
    
}
