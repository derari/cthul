package org.cthul.parser.api;

import org.cthul.parser.earleyx.*;
import org.cthul.parser.util.Format;
import org.cthul.parser.util.PriorityOrderedSet;

public abstract class Rule<C extends Context<?>> extends PriorityOrderedSet.Link {
    
    private static final String[] NO_SYMBOLS = {};
    private static final int[] NO_PRIORITIES = {};
    
    private final String symbol;
    private final int priority;
    
    private final String[] symbols;
    private final int[] priorities;
    
    public Rule(String symbol, int priority) {
        this(symbol, priority, null, null);
    }
    
    public Rule(String symbol, int priority, String[] symbols, int[] priorities) {
        this.symbol = symbol;
        this.priority = priority;
        this.symbols = symbols != null ? symbols : NO_SYMBOLS;
        this.priorities = priorities != null ? priorities : NO_PRIORITIES;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public String getSymbol(int i) {
        return symbols[i];
    }

    public int getPriority(int i) {
        return priorities[i];
    }

    protected String[] getSymbols() {
        return symbols;
    }

    protected int[] getPriorities() {
        return priorities;
    }
    
    public int getLength() {
        return symbols.length;
    }
    
    protected String productionOperator() {
        return "::=";
    }
    
    @Override
    public String toString() {
        return Format.production(symbol, priority, productionOperator(), symbols, priorities);
    }

}
