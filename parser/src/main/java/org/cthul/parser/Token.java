package org.cthul.parser;

/**
 *
 * @author Arian Treffer
 */
public interface Token<V> extends Value<V> {
    
    public int getId();
    
    public boolean match(String key);
    
    @Override
    public V getValue();
    
    @Override
    public int getStart();
    
    @Override
    public int getEnd();
    
    public int getChannel();
    
    public void toString(StringBuilder sb);
    
}
