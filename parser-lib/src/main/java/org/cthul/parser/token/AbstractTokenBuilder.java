package org.cthul.parser.token;

import java.util.regex.MatchResult;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractTokenBuilder<V, T extends Token<?>> implements TokenBuilder<V, T> {

    private final Class<T> tokenType;
    private final Class<?> tValueType;
    private final Class<V> paramType;
    private final String input;
    private final MatchResult groupProvider;
    private final TokenFactory<?, ? extends T> defaultFactory;
    
    private int start = 0;
    private int end = 0;
    private String key = null;
    private V value = null;
    private int channel = TokenChannel.Undefined;
    
    private String match = null;
    private String remaining = null;

    private TokenFactory<?, ? extends T> curFactory = null;
    private boolean tokenPrepared = false;
    private T current = null;

    public AbstractTokenBuilder(String input, MatchResult groupProvider, TokenFactory<V, T> defaultFactory) {
        this(defaultFactory.getTokenType(), defaultFactory.getTokenValueType(), defaultFactory.getParameterType(), input, groupProvider, defaultFactory);
    }
    
    @SuppressWarnings("unchecked")
    public AbstractTokenBuilder(Class<T> tokenType, Class<?> tValueType, Class<V> paramType, String input, MatchResult groupProvider, TokenFactory<? super V, ? extends T> defaultFactory) {
        this.tokenType = tokenType != null ? tokenType : (Class) Token.class;
        this.tValueType = tValueType != null ? tValueType : Object.class;
        this.paramType = paramType != null ? paramType : (Class) Object.class;
        this.input = input;
        this.groupProvider = groupProvider;
        this.defaultFactory = defaultFactory != null ? defaultFactory : (TokenFactory) ObjectToken.defaultFactory();
        checkFactory(defaultFactory);
    }

    protected void prepareNextToken(String key, TokenFactory<?, ? extends T> f, int start, int end) {
        tokenPrepared = true;
        setMatch(start, end);
        setKey(key);
        setFactory(f);
    }

    private void tokenChanged() {
        match = null;
        remaining = null;
        current = null;
        key = null;
        value = null;
        channel = TokenChannel.Undefined;
    }
    
    private ClassCastException mismatch(String type, Class<?> clazz, Object o) {
        return new ClassCastException("Invalid " + type + ": expected " + clazz + ", got " + o);
    }
    
    private void checkArgument(V arg) {
        if (arg == null) return;
        if (!paramType.isInstance(arg)) {
            throw mismatch("argument", paramType, arg);
        }
    }

    private void checkToken(T token) {
        if (token == null) return;
        if (!tokenType.isInstance(token)) {
            throw mismatch("token", tokenType, token);
        }
        Class<?> vType = token.getValueType();
        if (vType == null) return;
        if (!tValueType.isAssignableFrom(vType)) {
            throw mismatch("token value type", tValueType, vType);
        }
    }

    private void checkFactory(TokenFactory<?,?> factory) {
        if (factory == null) return;
        Class<?> tType = factory.getTokenType();
        if (tType == null) return;
        if (!tokenType.isAssignableFrom(tType)) {
            throw mismatch("token type", tokenType, tType);
        }
        // do not check factory value type, 
        // will be checked for created token instead
    }
    
    private void checkFactoryArgument(TokenFactory<?,?> factory, Object arg) {
        if (arg == null) return;
        Class<?> pType = factory.getParameterType();
        if (pType == null) return;
        if (!pType.isInstance(arg)) {
            throw mismatch("argument", pType, arg);
        }        
    }

    @Override
    public MatchResult getMatchResult() {
        return groupProvider;
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
        checkArgument(value);
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
        return groupProvider.groupCount();
    }

    @Override
    public String getMatch() {
        if (match == null) match = input.substring(start, end);
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
    public void setFactory(TokenFactory<?, ? extends T> factory) {
        checkFactory(factory);
        curFactory = factory;
    }
    
    @SuppressWarnings("unchecked")
    protected <V2> TokenFactory<V2, ? extends T> factory() {
        return (TokenFactory) (curFactory != null ? curFactory : defaultFactory);
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
        return newToken(factory(), channel, key);
    }

    @Override
    public T newToken(int channel, String key, V value) {
        return newToken(factory(), channel, key, value);
    }
    
    @Override
    public <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory) {
        return newToken(factory, getKey());
    }

    @Override
    public <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, String key) {
        return newToken(factory, getChannel(), key);
    }

    @Override
    public <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, V2 value) {
        return newToken(factory, getKey(), value);
    }

    @Override
    public <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, String key, V2 value) {
        return newToken(factory, getChannel(), key, value);
    }

    @Override
    public <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, String key, String match) {
        setMatch(match);
        return newToken(factory, key);
    }

    @Override
    public <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, int channel) {
        return newToken(factory, channel, getKey());
    }

    @Override
    public <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, int channel, String key) {
        if (value != null) {
            return newTokenWithValue(factory, channel, key, value);
        }
        final T2 token = factory.parseToken(
                getDefaultIndex(), 
                key != null ? key : this.key, 0, 
                getMatch(), 
                getInputIndex(), start, end, 
                channel);
        setCurrentToken(token);
        return token;
    }
    
    @SuppressWarnings("unchecked")
    protected <V2, T2 extends T> T2 newTokenWithValue(TokenFactory<? super V2, ? extends T2> factory, int channel, String key, Object value) {
        return newToken(factory, channel, key, (V2) value);
    }
    
    @Override
    public <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, int channel, String key, V2 value) {
        checkFactoryArgument(factory, value);
        final T2 token = factory.createToken(
                getDefaultIndex(), 
                key != null ? key : this.key, 0, 
                value, 
                getInputIndex(), start, end, 
                channel);
        setCurrentToken(token);
        return token;
    }

    @Override
    public T getCurrentToken() {
        if (current == null && tokenPrepared) newToken();
        return current;
    }

    @Override
    public void setCurrentToken(T token) {
        checkToken(token);
        this.current = token;
    }
    
}
