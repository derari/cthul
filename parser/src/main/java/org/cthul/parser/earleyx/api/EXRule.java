package org.cthul.parser.earleyx.api;

import org.cthul.parser.api.Context;
import org.cthul.parser.api.Rule;

public abstract class EXRule<C extends Context<?>> extends Rule<C> {

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
    
    public abstract EXMatch match(C context, int start, int end, EXMatch[] right);
    
}
