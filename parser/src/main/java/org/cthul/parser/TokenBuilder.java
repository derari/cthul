package org.cthul.parser;

/**
 *
 * @author Arian Treffer
 */
public interface TokenBuilder {

    public int getStart();

    public void setStart(int start);

    public int getEnd();

    public void setEnd(int end);

    public int getLength();

    public void setLength(int length);

    public void setLength(int start, int length);

    public void setMatch(int start, int end);

    public void setMatch(String match);

    public void setKey(String key);

    public String getKey();

    public void setValue(Object value);

    public Object getValue();

    public void setChannel(int channel);

    public int getChannel();

    public String getGroup(int i);

    public int getGroupCount();

    public String getMatch();

    public int getMatchIndex();

    public String getString();

    public String getRemainingString();

    public Token<?> newToken();

    public Token<?> newToken(String key);

//    public <V, T extends Token<V>> T newToken(String key, V value);
    public <V, T extends Token<V>> T newToken(String key, String match);

    public <V, T extends Token<V>> T newToken(int channel);

    public <V, T extends Token<V>> T newToken(String key, int channel);

//    public <V, T extends Token<V>> T newToken(String key, V value, int channel);
    public <V, T extends Token<V>> T newToken(TokenFactory<V, T> factory);

    public <V, T extends Token<V>> T newToken(String key, TokenFactory<V, T> factory);

    public <V, T extends Token<V>> T newToken(TokenFactory<V, T> factory, V value);

    public <V, T extends Token<V>> T newToken(String key, TokenFactory<V, T> factory, V value);

    public <V, T extends Token<V>> T newToken(int channel, TokenFactory<V, T> factory);

    public <V, T extends Token<V>> T newToken(String key, int channel, TokenFactory<V, T> factory);

    public <V, T extends Token<V>> T newToken(String key, int channel, TokenFactory<V, T> factory, V value);

    public <V> Token<V> getCurrentToken();

    public void setCurrentToken(Token<?> token);

    public static interface GroupProvider {

        public String getGroup(int i);

        public int getCount();
    }
}
