package org.cthul.parser.token;

import java.util.regex.MatchResult;

/**
 *
 * @author Arian Treffer
 */
public interface TokenBuilder<V, T extends Token<?>> {

    MatchResult getMatchResult();
    
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
    
    void setFactory(TokenFactory<?, ? extends T> factory);

    T newToken();

    T newToken(String key);

    T parseToken(String key, String match);
    
    T newToken(String key, V value);

    T newToken(int channel);

    T newToken(int channel, String key);

    T newToken(int channel, String key, V value);
    
    <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory);

    <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, String key);

    <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, V2 value);

    <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, String key, V2 value);
    
    <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, String key, String match);

    <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, int channel);

    <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, int channel, String key);

    <V2, T2 extends T> T2 newToken(TokenFactory<? super V2, ? extends T2> factory, int channel, String key, V2 value);

    T getCurrentToken();

    void setCurrentToken(T token);

}
