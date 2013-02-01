package org.cthul.parser.grammar.earleyx.rule;

import org.cthul.parser.api.*;

public abstract class EXRule<I extends Input<?>> extends AbstractRule {

    public EXRule(RuleKey key) {
        this(key.getSymbol(), key.getPriority());
    }
    
    public EXRule(String symbol, int priority) {
        super(symbol, priority);
    }

    public EXRule(String symbol, int priority, String[] symbols, int[] priorities) {
        super(symbol, priority, symbols, priorities);
    }

    @Override
    public String[] getSymbols() {
        return super.getSymbols();
    }

    @Override
    public int[] getPriorities() {
        return super.getPriorities();
    }
    
    public abstract Match match(Context<? extends I> context, int start, int end, Match[] right);
    
}
