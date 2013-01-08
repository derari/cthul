package org.cthul.parser.token;

/**
 *
 * @author Arian Treffer
 */
public interface TokenFactory<V, T extends Token<V>> {
    
    public T parseToken(int index, String symbol, int priority, String value, int inputIndex, int inputStart, int inputEnd, int channel);
    
    public T createToken(int index, String symbol, int priority, V value, int inputIndex, int inputStart, int inputEnd, int channel);
    
}
