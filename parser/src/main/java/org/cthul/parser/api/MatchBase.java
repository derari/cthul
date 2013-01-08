package org.cthul.parser.api;

public abstract class MatchBase implements Match {

    @Override
    public String toString() {
        return "[" + getStartIndex() + "-" + getEndIndex() + "]";
    }
    
}
