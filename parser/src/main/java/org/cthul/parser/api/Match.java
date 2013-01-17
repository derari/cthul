package org.cthul.parser.api;

public interface Match<V> extends Value<V> {
    
    String getSymbol();
    
    int getPriority();

    int getInputStart();
    
    int getInputEnd();
    
    int getStartIndex();
    
    int getEndIndex();
    
    @Override
    V eval(Object arg);
    
}
