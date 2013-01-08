package org.cthul.parser;

import java.util.ArrayList;
import java.util.List;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.util.Format;

public class ProductionBuilder {
    
    protected ParserBuilder<?> parserBuilder;
    protected RuleKey key;
    protected List<RuleKey> production = new ArrayList<>();
    protected RuleEval eval;

    public ProductionBuilder(ParserBuilder<?> parserBuilder, RuleKey key) {
        this.parserBuilder = parserBuilder;
        this.key = key;
    }

    public ProductionBuilder newSubRule(String subKey) {
        if (subKey == null) subKey = "";
        String sym = parserBuilder.keySet.generateUniqueSymbol(key.getSymbol() + "$" + subKey, null);
        return new ProductionBuilder(parserBuilder, parserBuilder.internRk(sym, 0));
    }

    public ProductionBuilder add(RuleKey key) {
        production.add(key);
        return this;
    }
    
    public ProductionBuilder add(String symbol, int priority) {
        return add(new RuleKey(symbol, priority));
    }

    public ProductionBuilder setEval(RuleEval eval) {
        this.eval = eval;
        return this;
    }

    public RuleEval getEval() {
        return eval;
    }

    public ProductionBuilder reset() {
        production.clear();
        return this;
    }
    
    public RuleKey createProduction() {
        return parserBuilder.addProduction(key, production.toArray(RuleKey.EMPTY_ARRAY), eval);
    }

    @Override
    public String toString() {
        return Format.production(key.getSymbol(), key.getPriority(), 
                RuleKey.collectSymbols(production), 
                RuleKey.collectPriorities(production));
    }

}
