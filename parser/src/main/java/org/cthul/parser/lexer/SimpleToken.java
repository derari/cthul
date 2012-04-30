package org.cthul.parser.lexer;

import org.cthul.parser.Token;

/**
 *
 * @author Arian Treffer
 */
public class SimpleToken<V> implements Token<V> {
    
    protected final int id;
    protected final String key;
    protected final V value;
    protected final int start;
    protected final int end;
    protected final int channel;

    public SimpleToken(int id, String key, V value, int start, int end, int channel) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.start = start;
        this.end = end;
        this.channel = channel;
    }

    @Override
    public int getId() {
        return id;
    }
    
    protected String getKeyString() {
        return key;
    }

    @Override
    public boolean match(String key) {
        return getKeyString().equals(key);
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public int getChannel() {
        return channel;
    }
    
    protected String getValueString() {
        return String.valueOf(getValue());
    }
    
    @Override
	public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb);
        return sb.toString();
	}

    @Override
    public void toString(StringBuilder sb) {
        String valueString = getValueString();
        String keyString = getKeyString();
        String s = valueString.replaceAll("\\n", "\\\\n");
		if (keyString.equals(valueString)) {
            sb.append("<\"").append(s).append("\">");
        } else {
            sb.append("<").append(keyString).
               append(":\"").append(s).append("\">");
        }
    }
}
