package org.cthul.parser.token;

/**
 *
 * @author Arian Treffer
 */
public interface TokenFactory<V, T extends Token<?>> {
    
    T parseToken(int index, String symbol, int priority, String value, int inputIndex, int inputStart, int inputEnd, int channel);
    
    T createToken(int index, String symbol, int priority, V value, int inputIndex, int inputStart, int inputEnd, int channel);
    
    Class<T> getTokenType();
    
    Class<?> getTokenValueType();
    
    Class<V> getParameterType();
    
}
