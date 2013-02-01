package org.cthul.parser.api;

import java.util.List;
import java.util.Objects;
import org.cthul.parser.util.Format;

public class RuleKey implements Comparable<RuleKey> {
    
    public static final RuleKey[] EMPTY_ARRAY = new RuleKey[0];
    
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
    
    public static String[] collectSymbols(RuleKey... keys) {
        final String[] result = new String[keys.length];
        for (int i = 0; i < result.length; i++)
            result[i] = keys[i].getSymbol();
        return result;
    }
    
    public static int[] collectPriorities(RuleKey... keys) {
        final int[] result = new int[keys.length];
        for (int i = 0; i < result.length; i++)
            result[i] = keys[i].getPriority();
        return result;
    }
    
    public static String[] collectSymbols(List<RuleKey> keys) {
        final String[] result = new String[keys.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = keys.get(i).getSymbol();
        return result;
    }
    
    public static int[] collectPriorities(List<RuleKey> keys) {
        final int[] result = new int[keys.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = keys.get(i).getPriority();
        return result;
    }

    @Override
    public int compareTo(RuleKey o) {
        int c = symbol.compareTo(o.symbol);
        if (c != 0) return c;
        return priority - o.priority;
    }
    
}
