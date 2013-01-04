package org.cthul.parser.api;

public abstract class XMatch {
    
    private final Rule rule;
    private final String symbol;
    private final int priority;
    private final int inputStart;
    private final int inputEnd;
    private final XMatch[] args;

    public XMatch(Rule rule, int inputStart, int inputEnd, XMatch[] args) {
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

    public XMatch[] getArgs() {
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
