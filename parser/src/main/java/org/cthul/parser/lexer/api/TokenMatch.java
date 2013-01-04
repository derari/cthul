package org.cthul.parser.lexer.api;

import org.cthul.parser.api.Match;

/**
 * An atomic match for the parser, that contains one or more input tokens.
 */
public interface TokenMatch extends Match {

    @Override
    int getStart();
    
    @Override
    int getEnd();
   
    @Override
    int getStartIndex();
    
    @Override
    int getEndIndex();
    
    @Override
    Object eval();
    
}
