package org.cthul.parser.grammar.api;

import org.cthul.parser.api.Match;

/**
 * Created by a parser, may contain other matches.
 */
public interface ProductionMatch extends Match<Object> {

    @Override
    int getInputStart();
    
    @Override
    int getInputEnd();
   
    @Override
    int getStartIndex();
    
    @Override
    int getEndIndex();
    
    @Override
    Object eval(Object arg);
    
    Match<?>[] matches();
    
}
