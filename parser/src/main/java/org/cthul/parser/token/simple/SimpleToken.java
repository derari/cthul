package org.cthul.parser.token.simple;

import org.cthul.parser.api.RuleKey;
import org.cthul.parser.token.Token;
import org.cthul.parser.util.Format;

/**
 *
 * @author Arian Treffer
 */
public class SimpleToken<V> implements Token<V> {
    
    protected final int index;
    protected final String symbol;
    protected final int priority;
    protected final V value;
    protected final int inputIndex;
    protected final int inputStart;
    protected final int inputEnd;
    protected final int channel;

    public SimpleToken(int index, String symbol, int priority, V value, int inputIndex, int inputStart, int inputEnd, int channel) {
        this.index = index;
        this.symbol = symbol;
        this.priority = priority;
        this.value = value;
        this.inputIndex = inputIndex;
        this.inputStart = inputStart;
        this.inputEnd = inputEnd;
        this.channel = channel;
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
    public boolean match(RuleKey key) {
        return getPriority() >= key.getPriority() &&
                getSymbol().equals(key.getSymbol());
    }
    
    @Override
    public V eval() {
        return value;
    }

    @Override
    public int getInputIndex() {
        return inputIndex;
    }

    @Override
    public int getInputStart() {
        return inputStart;
    }

    @Override
    public int getInputEnd() {
        return inputEnd;
    }

    @Override
    public int getStartIndex() {
        return index;
    }

    @Override
    public int getEndIndex() {
        return index+1;
    }

    @Override
    public int getChannel() {
        return channel;
    }
    
    protected String getKeyString() {
        return Format.productionKey(symbol, priority);
    }
    
    protected String getValueString() {
        return String.valueOf(eval());
    }

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        sb.append("<[").append(getInputStart()).
           append('-').append(getInputEnd()).
           append(']');
        String keyString = getKeyString();
        String valueString = getValueString();
        String s = valueString.replaceAll("\\n", "\\\\n");
        if (!keyString.equals(valueString)) {
            sb.append(keyString).append(':');
        }
        return sb.append("\"").append(s).append("\">");
    }
}
