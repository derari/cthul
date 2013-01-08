package org.cthul.parser.token;


/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractTokenFactory<V, T extends Token<V>> implements TokenFactory<V, T> {

    @Override
    public T parseToken(int index, String symbol, int priority, String value, int inputIndex, int inputStart, int inputEnd, int channel) {
        return createToken(index, symbol, priority, parse(value), inputIndex, inputStart, inputEnd, channel);
    }

    @Override
    public T createToken(int index, String symbol, int priority, V value, int inputIndex, int inputStart, int inputEnd, int channel) {
        if (channel == TokenChannel.Undefined) {
            channel = defaultChannel();
        }
        if (symbol == null) {
            symbol = defaultKey(value);
        }
        return newToken(index, symbol, priority, value, inputIndex, inputStart, inputEnd, channel);
    }
    
    protected int defaultChannel() {
        return TokenChannel.Default;
    }
    
    protected String defaultKey(V value) {
        return String.valueOf(value);
    }
    
    protected abstract V parse(String value);
    
    protected abstract T newToken(int index, String symbol, int priority, V value, int inputIndex, int inputStart, int inputEnd, int channel);
    
}
