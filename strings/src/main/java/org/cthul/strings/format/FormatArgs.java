package org.cthul.strings.format;

/**
 * Arguments for formatting, identified by index, character or key.
 * @author Arian Treffer
 */
public interface FormatArgs {
    
    /**
     * @param i zero-based index
     */
    Object get(int i);
    
    Object get(char c);
    
    Object get(String s);
    
}
