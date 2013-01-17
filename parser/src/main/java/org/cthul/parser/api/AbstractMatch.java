package org.cthul.parser.api;

public abstract class AbstractMatch<V> extends MatchBase<V> {

    private final String symbol;
    private final int priority;
    private final int start, startIndex;
    private final int end, endIndex;

    public AbstractMatch(String symbol, int priority, int start, int startIndex, int end, int endIndex) {
        this.symbol = symbol;
        this.priority = priority;
        this.start = start;
        this.startIndex = startIndex;
        this.end = end;
        this.endIndex = endIndex;
    }
    
    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public int getInputStart() {
        return start;
    }

    @Override
    public int getInputEnd() {
        return end;
    }

    @Override
    public int getStartIndex() {
        return startIndex;
    }

    @Override
    public int getEndIndex() {
        return endIndex;
    }

    @Override
    public abstract V eval();
    
}
