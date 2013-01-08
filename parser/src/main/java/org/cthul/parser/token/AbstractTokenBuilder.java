package org.cthul.parser.token;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractTokenBuilder<V, T extends Token<V>> implements TokenBuilder<V, T> {

    private final String input;
    private final GroupProvider groupProvider;
    private final TokenFactory<? super V, ? extends T> defaultFactory;
    
    private int start = 0;
    private int end = 0;
    private String key = null;
    private V value = null;
    private int channel = TokenChannel.Undefined;
    
    private String match = null;
    private String remaining = null;

    private Token<?> current = null;

    public AbstractTokenBuilder(String input, TokenFactory<? super V, ? extends T> defaultTokenFactory, GroupProvider groupProvider) {
        this.input = input;
        this.defaultFactory = defaultTokenFactory;
        this.groupProvider = groupProvider;
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
    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public V getValue() {
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
        return groupProvider.group(i);
    }

    @Override
    public int getGroupCount() {
        return groupProvider.count();
    }

    @Override
    public String getMatch() {
        if (match == null) match = getGroup(0);
        return match;
    }

    @Override
    public abstract int getDefaultIndex();
    
    @Override
    public abstract int getInputIndex();

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
    public T newToken() {
        return newToken(getKey());
    }

    @Override
    public T newToken(String key) {
        return newToken(getChannel(), key);
    }

    @Override
    public T newToken(String key, V value) {
        return newToken(getChannel(), key, value);
    }

    @Override
    public T parseToken(String key, String match) {
        setMatch(match);
        return newToken(key);
    }

    @Override
    public T newToken(int channel) {
        return this.newToken(channel, getKey());
    }

    @Override
    public T newToken(int channel, String key) {
        return newToken(defaultFactory, channel, key);
    }

    @Override
    public T newToken(int channel, String key, V value) {
        return newToken(defaultFactory, channel, key, value);
    }
        
    @Override
    public <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory) {
        return newToken(factory, getKey());
    }

    @Override
    public <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory, String key) {
        return newToken(factory, getChannel(), key);
    }

    @Override
    public <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory, V value) {
        return newToken(factory, getKey(), value);
    }

    @Override
    public <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory, String key, V value) {
        return newToken(factory, getChannel(), key, value);
    }

    @Override
    public <V, T extends Token<V>> T parseToken(TokenFactory<? super V, ? extends T> factory, String key, String match) {
        setMatch(match);
        return newToken(factory, key);
    }

    @Override
    public <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory, int channel) {
        return newToken(factory, channel, getKey());
    }

    @Override
    public <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory, int channel, String key) {
        if (value != null) {
            return newTokenWithValue(factory, channel, key, value);
        }
        final T token = factory.parseToken(
                getDefaultIndex(), 
                key, 0, 
                getMatch(), 
                getInputIndex(), start, end, 
                channel);
        current = token;
        return token;
    }
    
    @SuppressWarnings("unchecked")
    protected <V, T extends Token<V>> T newTokenWithValue(TokenFactory<? super V, ? extends T> factory, int channel, String key, Object value) {
        return newToken(factory, channel, key, (V) value);
    }
    
    @Override
    public <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory, int channel, String key, V value) {
        final T token = factory.createToken(
                getDefaultIndex(), 
                key, 0, 
                value, 
                getInputIndex(), start, end, 
                channel);
        current = token;
        return token;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Token<?>> T getCurrentToken() {
        return (T) current;
    }

    @Override
    public void setCurrentToken(Token<?> token) {
        this.current = token;
    }

}
