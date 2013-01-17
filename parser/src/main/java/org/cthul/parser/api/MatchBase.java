package org.cthul.parser.api;

public abstract class MatchBase<V> implements Match<V> {

    @Override
    public String toString() {
        return "[" + getStartIndex() + "-" + getEndIndex() + "]";
    }
    
}
