package org.cthul.parser.token;

/**
 *
 * @author Arian Treffer
 */
public interface TokenBuilder<V, T extends Token<V>> {

    int getStart();

    void setStart(int start);

    int getEnd();

    void setEnd(int end);

    int getLength();

    void setLength(int length);

    void setLength(int start, int length);

    void setMatch(int start, int end);

    void setMatch(String match);

    void setKey(String key);

    String getKey();

    void setValue(V value);

    V getValue();

    void setChannel(int channel);

    int getChannel();

    String getGroup(int i);

    int getGroupCount();

    String getMatch();

    int getDefaultIndex();
    
    int getInputIndex();

    String getString();

    String getRemainingString();

    T newToken();

    T newToken(String key);

    T parseToken(String key, String match);
    
    T newToken(String key, V value);

    T newToken(int channel);

    T newToken(int channel, String key);

    T newToken(int channel, String key, V value);
    
    <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory);

    <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory, String key);

    <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory, V value);

    <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory, String key, V value);
    
    <V, T extends Token<V>> T parseToken(TokenFactory<? super V, ? extends T> factory, String key, String match);

    <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory, int channel);

    <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory, int channel, String key);

    <V, T extends Token<V>> T newToken(TokenFactory<? super V, ? extends T> factory, int channel, String key, V value);

    <T extends Token<?>> T getCurrentToken();

    void setCurrentToken(Token<?> token);

    public static interface GroupProvider {

        String group(int i);

        int count();
    }
}
