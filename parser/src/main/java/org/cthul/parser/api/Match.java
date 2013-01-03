package org.cthul.parser.api;

public abstract class Match {
    
    private final Rule rule;
    private final String symbol;
    private final int priority;
    private final int inputStart;
    private final int inputEnd;
    private final Match[] args;

    public Match(Rule rule, int inputStart, int inputEnd, Match[] args) {
        this.rule = rule;
        this.symbol = rule.getSymbol();
        this.priority = rule.getPriority();
        this.inputStart = inputStart;
        this.inputEnd = inputEnd;
        this.args = args;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getPriority() {
        return priority;
    }

    public int getStart() {
        return inputStart;
    }

    public int getEnd() {
        return inputEnd;
    }

    public Match[] getArgs() {
        return args;
    }

    public Rule getRule() {
        return rule;
    }
    
    public abstract Object eval();

    @Override
    public String toString() {
        return "[" + inputStart + "-" + inputEnd + "] " + rule;
    }
    
}
