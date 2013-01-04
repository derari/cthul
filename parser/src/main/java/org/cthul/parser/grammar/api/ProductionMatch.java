package org.cthul.parser.grammar.api;

import org.cthul.parser.api.Match;

/**
 * Created by a parser, may contain other matches.
 */
public interface ProductionMatch extends Match {

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
