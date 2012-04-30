package org.cthul.parser.lexer;

import org.cthul.parser.TokenChannel;
import org.cthul.parser.Token;
import org.cthul.parser.TokenBuilder;
import org.cthul.parser.TokenFactory;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractTokenBuilder implements TokenBuilder {

    private final String input;
    private GroupProvider groupProvider;
    private TokenFactory<?, ?> defaultFactory;
    
    private int start = 0;
    private int end = 0;
    private String key = null;
    private Object value = null;
    private int channel = TokenChannel.Undefined;
    
    private String match = null;
    private String remaining = null;

    private Token<?> current = null;

    public AbstractTokenBuilder(String input, TokenFactory<?, ?> defaultTokenFactory, GroupProvider groupProvider) {
        this.input = input;
        this.defaultFactory = defaultTokenFactory;
        this.groupProvider = groupProvider != null ? groupProvider : new DefaultGroupProvider();
    }

    private void tokenChanged() {
        match = null;
        remaining = null;
        current = null;
        key = null;
        value = null;
        channel = TokenChannel.Undefined;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public void setStart(int start) {
        this.start = start;
        tokenChanged();
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public void setEnd(int end) {
        this.end = end;
        tokenChanged();
    }

    @Override
    public int getLength() {
        return end - start;
    }

    @Override
    public void setLength(int length) {
        this.end = start + length;
        tokenChanged();
    }

    @Override
    public void setLength(int start, int length) {
        this.start = start;
        this.end = start + length;
        tokenChanged();
    }

    @Override
    public void setMatch(int start, int end) {
        this.start = start;
        this.end = end;
        tokenChanged();
    }

    @Override
    public void setMatch(String match) {
        this.match = match;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setChannel(int channel) {
        this.channel = channel;
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public String getGroup(int i) {
        return groupProvider.getGroup(i);
    }

    @Override
    public int getGroupCount() {
        return groupProvider.getCount();
    }

    @Override
    public String getMatch() {
        if (match == null) match = getGroup(0);
        return match;
    }

    @Override
    public abstract int getMatchIndex();

    @Override
    public String getString() {
        return input;
    }

    @Override
    public String getRemainingString() {
        if (remaining == null) {
            remaining = input.substring(start);
        }
        return remaining;
    }

    @Override
    public Token<?> newToken() {
        return this.newToken(getKey());
    }

    @Override
    public Token<?> newToken(String key) {
        return this.<Object, Token<Object>>newToken(key, getChannel());
    }

//    @Override
//    public <V, T extends Token<V>> T newToken(String key, V value) {
//        return this.<V,T>newToken(key, value, getChannel());
//    }

    @Override
    public <V, T extends Token<V>> T newToken(int channel) {
        return this.<V,T>newToken(getKey(), channel);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V, T extends Token<V>> T newToken(String key, int channel) {
        return newToken(key, channel, (TokenFactory<V, T>)defaultFactory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V, T extends Token<V>> T newToken(String key, String match) {
        setMatch(match);
        return (T) newToken(key);
    }

//    @Override
//    @SuppressWarnings("unchecked")
//    public <V, T extends Token<V>> T newToken(String key, V value, int channel) {
//        return newToken(key, channel, (TokenFactory<V, T>)defaultFactory, value);
//    }

    @Override
    public <V, T extends Token<V>> T newToken(TokenFactory<V, T> factory) {
        return newToken(getKey(), factory);
    }

    @Override
    public <V, T extends Token<V>> T newToken(String key, TokenFactory<V, T> factory) {
        return newToken(key, getChannel(), factory);
    }

    @Override
    public <V, T extends Token<V>> T newToken(TokenFactory<V, T> factory, V value) {
        return newToken(getKey(), factory, value);
    }

    @Override
    public <V, T extends Token<V>> T newToken(String key, TokenFactory<V, T> factory, V value) {
        return newToken(key, getChannel(), factory, value);
    }

    @Override
    public <V, T extends Token<V>> T newToken(int channel, TokenFactory<V, T> factory) {
        return newToken(getKey(), channel, factory);
    }

    @Override
    public <V, T extends Token<V>> T newToken(String key, int channel, TokenFactory<V, T> factory) {
        if (value != null) {
            return newTokenWithValue(key, channel, factory);
        }
        final T token = factory.parseToken(
                getMatchIndex(),
                key,
                getMatch(),
                start, end, 
                channel);
        current = token;
        return token;
    }
    
    @SuppressWarnings("unchecked")
    private <V, T extends Token<V>> T newTokenWithValue(String key, int channel, TokenFactory<V, T> factory) {
        return newToken(key, channel, factory, (V) value);
    }

    @Override
    public <V, T extends Token<V>> T newToken(String key, int channel, TokenFactory<V, T> factory, V value) {
        final T token = factory.createToken(
                getMatchIndex(), 
                key, 
                value, 
                start, end, 
                channel);
        current = token;
        return token;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Token<V> getCurrentToken() {
        return (Token<V>) current;
    }

    @Override
    public void setCurrentToken(Token<?> token) {
        this.current = token;
    }
    
    protected class DefaultGroupProvider implements GroupProvider {
        @Override
        public String getGroup(int i) {
            return input.substring(getStart(), getEnd());
        }
        @Override
        public int getCount() {
            return 1;
        }
    }
}
