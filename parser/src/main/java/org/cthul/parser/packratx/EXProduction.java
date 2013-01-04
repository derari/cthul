package org.cthul.parser.packratx;

import org.cthul.parser.util.PriorityOrderedSet;

public class EXProduction extends PriorityOrderedSet.Link {
    
    private final String symbol;
    private final int priority;
    
//    private final String[] symbols;
//    private final String[] precedences;
    
    EXProduction next = null;

    public EXProduction(String symbol, int priority) {
        this.symbol = symbol;
        this.priority = priority;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public int getPriority() {
        return priority;
    }
    
}
