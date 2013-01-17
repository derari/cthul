package org.cthul.parser.token;

import org.cthul.parser.api.RuleKey;
import org.cthul.parser.util.Format;

/**
 *
 * @author Arian Treffer
 */
public class ObjectToken<V> implements Token<V> {
    
    @SuppressWarnings("unchecked")
    public static <V> TokenFactory<V, ObjectToken<V>> factory() {
        return (TokenFactory) FACTORY;
    }
    
    public static final TokenFactory<Object, ObjectToken<Object>> FACTORY = 
                    new AbstractTokenFactory<Object, ObjectToken<Object>>() {
        @Override
        protected Object parse(String value) {
            return value;
        }
        @Override
        protected ObjectToken<Object> newToken(int index, String symbol, int priority, Object value, int inputIndex, int inputStart, int inputEnd, int channel) {
            return new ObjectToken<>(index, symbol, priority, value, inputIndex, inputStart, inputEnd, channel);
        }
        @Override
        @SuppressWarnings("unchecked")
        public Class<ObjectToken<Object>> getTokenType() {
            return (Class) ObjectToken.class;
        }
        @Override
        public Class<?> getTokenValueType() {
            return Object.class;
        }
        @Override
        public Class<Object> getParameterType() {
            return Object.class;
        }
    };
    
    @SuppressWarnings("unchecked")
    public static <V> TokenFactory<V, Token<V>> defaultFactory() {
        return (TokenFactory) DEFAULT_FACTORY;
    }
    
    public static final TokenFactory<Object, Token<Object>> DEFAULT_FACTORY = 
                    new AbstractTokenFactory<Object, Token<Object>>() {
        @Override
        protected Object parse(String value) {
            return value;
        }
        @Override
        protected ObjectToken<Object> newToken(int index, String symbol, int priority, Object value, int inputIndex, int inputStart, int inputEnd, int channel) {
            return new ObjectToken<>(index, symbol, priority, value, inputIndex, inputStart, inputEnd, channel);
        }
        @Override
        @SuppressWarnings("unchecked")
        public Class<Token<Object>> getTokenType() {
            return (Class) Token.class;
        }
        @Override
        public Class<?> getTokenValueType() {
            return Object.class;
        }
        @Override
        public Class<Object> getParameterType() {
            return Object.class;
        }
    };
    
    protected final int index;
    protected final String symbol;
    protected final int priority;
    protected final V value;
    protected final int inputIndex;
    protected final int inputStart;
    protected final int inputEnd;
    protected final int channel;

    public ObjectToken(int index, String symbol, int priority, V value, int inputIndex, int inputStart, int inputEnd, int channel) {
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
    @SuppressWarnings("unchecked")
    public Class<? extends V> getValueType() {
        return (Class) (value == null ? null : value.getClass());
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
        sb.append('<');
        sb.append(getStartIndex());
        sb.append('[').append(getInputStart()).
           append('-').append(getInputEnd()).
           append(']');
        String keyString = getKeyString();
        String valueString = getValueString();
        String s = valueString.replaceAll("\\n", "\\\\n");
        final int maxKeyLen = 15;
        int maxValueLen = maxKeyLen+8;
        if (!keyString.equals(valueString)) {
            if (keyString.length() > maxKeyLen) {
                sb.append(keyString, 0, maxKeyLen-3).append("...:");
                maxValueLen -= maxKeyLen;
            } else {
                sb.append(keyString).append(':');
                maxValueLen -= keyString.length();
            }
        }
        if (s.length() > maxValueLen) {
            if (maxValueLen < 8) maxValueLen = 8;
            return sb.append('"').append(s, 0, maxValueLen-3).append("...\">");
        } else {
            return sb.append('"').append(s).append("\">");
        }
    }
}
