package org.cthul.parser.lexer;

import org.cthul.parser.Token;
import org.cthul.parser.TokenFactory;
import org.cthul.parser.annotation.Channel;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractTokenFactory<V, T extends Token<V>> implements TokenFactory<V, T> {

    @Override
    public T parseToken(int id, String key, String value, int start, int end, int channel) {
        return createToken(id, key, parse(value), start, end, channel);
    }

    @Override
    public T createToken(int id, String key, V value, int start, int end, int channel) {
        if (channel == Channel.Undefined) {
            channel = defaultChannel();
        }
        if (key == null) {
            key = defaultKey(value);
        }
        return newToken(id, key, value, start, end, channel);
    }
    
    protected int defaultChannel() {
        return Channel.Default;
    }
    
    protected String defaultKey(V value) {
        return String.valueOf(value);
    }
    
    protected abstract V parse(String value);
    
    protected abstract T newToken(int id, String key, V value, int start, int end, int channel);
    
}
