package org.cthul.parser;

/**
 *
 * @author Arian Treffer
 */
public interface Value<V> {
    
    public V getValue();
    
    public int getStart();
    
    public int getEnd();
    
}
