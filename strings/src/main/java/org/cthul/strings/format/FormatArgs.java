package org.cthul.strings.format;

/**
 *
 * @author Arian Treffer
 */
public interface FormatArgs {
    
    Object get(int i);
    
    Object get(char c);
    
    Object get(String s);
    
}
