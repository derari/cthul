package org.cthul.parser.api;

import java.util.Objects;
import org.cthul.parser.util.Format;

public class RuleKey {
    
    final String symbol;
    final int priority;

    public RuleKey(String symbol, int priority) {
        this.symbol = symbol;
        this.priority = priority;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return Format.productionKey(symbol, priority);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.symbol);
        hash = 29 * hash + this.priority;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RuleKey other = (RuleKey) obj;
        if (!Objects.equals(this.symbol, other.symbol)) {
            return false;
        }
        if (this.priority != other.priority) {
            return false;
        }
        return true;
    }
    
}
