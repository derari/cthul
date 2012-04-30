package org.cthul.parser;

/**
 *
 * @author Arian Treffer
 */
public interface Grammar {
    
    public Object parse(String startSymbol, TokenStream tokenStream, Context context);
    
}
