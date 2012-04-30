package org.cthul.parser;

/**
 *
 * @author Arian Treffer
 */
public interface Lexer {
    
    public TokenStream scan(String input, Context context) throws NoMatchException;
    
}
