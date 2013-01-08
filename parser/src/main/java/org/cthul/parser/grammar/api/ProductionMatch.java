package org.cthul.parser.grammar.api;

import org.cthul.parser.api.Match;

/**
 * Created by a parser, may contain other matches.
 */
public interface ProductionMatch<V> extends Match<V> {

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
    
    Match[] matches();
    
}
