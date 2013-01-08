package org.cthul.parser.lexer.api;

import org.cthul.parser.api.Match;

/**
 * An atomic match for the parser, that contains one or more input tokens.
 */
public interface TokenMatch<V> extends Match<V> {

    @Override
    int getInputStart();
    
    @Override
    int getInputEnd();
   
    @Override
    int getStartIndex();
    
    @Override
    int getEndIndex();
    
    @Override
    V eval();
    
}
