package org.cthul.parser.api;

public interface Match {

    int getStart();
    
    int getEnd();
    
    int getStartIndex();
    
    int getEndIndex();
    
    Object eval();
    
}
