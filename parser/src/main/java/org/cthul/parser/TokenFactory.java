package org.cthul.parser;

/**
 *
 * @author Arian Treffer
 */
public interface TokenFactory<V, T extends Token<V>> {
    
    public T parseToken(int id, String key, String value, int start, int end, int channel);
    
    public T createToken(int id, String key, V value, int start, int end, int channel);
    
}
