package org.cthul.parser;

/**
 *
 * @author Arian Treffer
 */
public interface TokenStream {
    
    public void start();
    
    public <V> Token<V> first();
    
    public <V> Token<V> next();
    
    public <V> Token<V> next(int channel);
    
    public <V> Token<V> previous();
    
    public <V> Token<V> previous(int channel);
    
    public <V> Token<V> last();
    
    public int getLength();
    
    public String getString();
    
}
